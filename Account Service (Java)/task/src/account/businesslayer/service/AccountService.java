package account.businesslayer.service;

import account.businesslayer.entity.*;
import account.businesslayer.exception.*;
import account.businesslayer.mapper.DtoMapper;
import account.businesslayer.security.AccountAdapter;
import account.persistence.AccountRepository;
import account.persistence.EventRepository;
import account.persistence.RoleRepository;
import account.persistence.PaymentRepository;
import account.presentation.dto.AccountDTO;
import account.presentation.dto.PaymentDTO;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService {
    public static final int MAX_FAILED_ATTEMPTS = 5;

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final RoleRepository roleRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;
    private final Set<String>  breachedPasswords;
    private final DtoMapper mapper;

    public AccountService(AccountRepository accountRepository, PaymentRepository paymentRepository, RoleRepository roleRepository, EventRepository eventRepository, @Lazy PasswordEncoder passwordEncoder, DtoMapper mapper) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.roleRepository = roleRepository;
        this.eventRepository = eventRepository;
        createRoles();
        this.passwordEncoder = passwordEncoder;
        breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
        this.mapper = mapper;
    }

    private void createRoles() {
        try {
            roleRepository.save(new Role(1, "ROLE_AUDITOR", true));
            roleRepository.save(new Role(2, "ROLE_ADMINISTRATOR", false));
            roleRepository.save(new Role(3, "ROLE_ACCOUNTANT", true));
            roleRepository.save(new Role(4, "ROLE_USER", true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveEvent(String action, String subject, String object, String path) {
        eventRepository.save(new Event(action, subject, object, path));
    }

    public AccountDTO save(Account account, String path) {
        String password = account.getPassword();
        checkPassword(password, path);
        account.setPassword(passwordEncoder.encode(password));
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new UserExistException("User exist!", path);
        }
        if (!accountRepository.existsById(1L)) {
            account.addRole(new Role(2,"ROLE_ADMINISTRATOR", false));
        } else {
            account.addRole(new Role(4,"ROLE_USER", true));
        }
        accountRepository.save(account);
        eventRepository.save(new Event("CREATE_USER", "Anonymous", account.getEmail(), path));
        return mapper.convertAccountToAccountDTO(account);
    }

    public void increaseFailedAttempts(Account account) {
        account.setFailedAttempt(account.getFailedAttempt() + 1);
        accountRepository.save(account);
    }

    public void resetFailedAttempts(Account account) {
        account.setFailedAttempt(0);
        accountRepository.save(account);
    }

    public void lock(Account account) {
        account.setAccountNonLocked(false);
        accountRepository.save(account);
    }

    private void checkPassword(String password, String path) {
        if(breachedPasswords.contains(password)) {
            throw new PasswordIsBreachedException("The password is in the hacker's database!", path);
        }
    }

    public void updatePassword(NewPassword newPassword, AccountAdapter accountAdapter, String path){
        String password = newPassword.getNewPassword();
        checkPassword(password, path);
        if(passwordEncoder.matches(password, accountAdapter.getPassword())) {
            throw new IdenticalPasswordExceptions("The passwords must be different!", path);
        }
        Account account = accountAdapter.getAccount();
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        eventRepository.save(new Event("CHANGE_PASSWORD", account.getEmail(), account.getEmail(), path));
    }

    public void savePayments(List<PaymentDTO> paymentDTOList, String path) {
        paymentRepository.saveAll(paymentDTOList.stream().map(dto -> this.convertDTOtoPayment(dto, path)).toList());
    }


    public void updateSalary(List<PaymentDTO> paymentDTOList, String path) {
        for (PaymentDTO dto : paymentDTOList) {
            Payment payment = paymentRepository.findByEmployeeAndPeriod(getAccountByEmail(dto, path), dto.getPeriod()).orElseThrow(() -> new PaymentNotExistException("payment not exist", path));
            payment.setSalary(dto.getSalary());
            paymentRepository.save(payment);
        }
    }

    public List<AccountDTO> getAccounts() {
        return ((List<Account>) accountRepository.findAll()).stream()
                .map(mapper::convertAccountToAccountDTO)
                .toList();
    }

    public void deleteAccount(String email, AccountAdapter accountAdapter) {
        Account account = getAccount("/api/admin/user/" + email, email);
        if (account.getId() == 1) {
            throw new AdministratorRoleIsNotRemovableException("Can't remove ADMINISTRATOR role!", "/api/admin/user/" + email);
        }
        accountRepository.delete(account);
        eventRepository.save(new Event("DELETE_USER", accountAdapter.getUsername(), account.getEmail(), "/api/admin/user"));
    }

    public AccountDTO changeRoles(RoleChanger roleChanger, AccountAdapter accountAdapter) {
        String url = "/api/admin/user/role";
        String email = roleChanger.getUser().toLowerCase();
        Account account = getAccount("/api/admin/user/role", email);
        Role role = roleRepository.findByName("ROLE_" + roleChanger.getRole()).orElseThrow(() -> new RoleNotExistException("Role not found!", url));
        String action;
        String str;
        String toOrFrom;
        if(roleChanger.getOption() == GrantRemoveOption.REMOVE) {
            if(role.getName().equals("ROLE_ADMINISTRATOR")){
                throw new AdministratorRoleIsNotRemovableException("Can't remove ADMINISTRATOR role!", url);
            }
            if (!account.getRoles().contains(role)) {
                throw new RoleNotExistException("The user does not have a role!", url);
            }
            if(account.getRoles().size() == 1) {
                throw new OnlyOneRoleException("The user must have at least one role!", url);
            }
            account.removeRole(role);
            action = "REMOVE_ROLE";
            str = "Remove role ";
            toOrFrom = " from ";
            account.removeRole(role);
        } else {
            account.getRoles().forEach(r -> {
                if(r.isBusinessRole() ^ role.isBusinessRole()) {
                    throw new CombineAdministrativeAndBusinessRolesException("The user cannot combine administrative and business roles!", url);
                }
            });
            action = "GRANT_ROLE";
            str = "Grant role ";
            toOrFrom = " to ";
            account.addRole(role);
        }
        accountRepository.save(account);
        eventRepository.save(new Event(action, accountAdapter.getUsername(), str + role.getName().substring(5) + toOrFrom + account.getEmail(), url));
        return mapper.convertAccountToAccountDTO(account);
    }

    public void lock(Locker locker, AccountAdapter accountAdapter) {
        String path = "/api/admin/user/access";
        Account account = getAccount(locker.getUser().toLowerCase()).orElseThrow( () -> new UserExistException("User not exist", path));
        if(account.getRoles().stream().map(Role::getName).collect(Collectors.toSet()).contains("ROLE_ADMINISTRATOR")) {
            throw new AdministratorRoleIsNotLockable("Can't lock the ADMINISTRATOR!", path);
        }
        String action;
        String str;
        if(locker.getOperation() == LockUnlockOption.LOCK) {
            action = "LOCK_USER";
            str = "Lock user ";
            account.setAccountNonLocked(false);
        } else {
            action = "UNLOCK_USER";
            str = "Unlock user ";
            account.setAccountNonLocked(true);
            account.setFailedAttempt(0);
        }
        accountRepository.save(account);
        eventRepository.save(new Event(action, accountAdapter.getUsername(), str + account.getEmail(), path));
    }

    public List<Event> getEvents() {
        return (List<Event>) eventRepository.findAll();
    }

    public Account getAccountByEmail(PaymentDTO dto, String path) {
        String email = dto.getEmployee();
        return getAccount(path, email);
    }

    private Account getAccount(String path, String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new UserNotExistException(path.contains(email) || path.equals("/api/admin/user/role") ? "User not found!" : "Employee not exist!", path));
    }

    public Optional<Account> getAccount(String email) {
        return accountRepository.findByEmail(email);
    }

    private Payment convertDTOtoPayment(PaymentDTO dto, String path) {
        return new Payment(getAccountByEmail(dto, path), dto.getPeriod(), dto.getSalary());
    }
}
