package account.presentation.controller;

import account.businesslayer.entity.*;
import account.businesslayer.exception.CustomException;
import account.businesslayer.mapper.DtoMapper;
import account.businesslayer.security.AccountAdapter;
import account.businesslayer.service.AccountService;
import account.presentation.dto.AccountPaymentsDTO;
import account.presentation.dto.PaymentDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
public class AccountController {
    private final AccountService service;
    private final DtoMapper dtoMapper;
    public AccountController(AccountService service, DtoMapper dtoMapper) {
        this.service = service;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/api/auth/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Account account) throws NoSuchMethodException {
        return ResponseEntity.ok(service.save(account, "/api/auth/signup"));
    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<?> changePassword(@Valid @RequestBody NewPassword newPassword, @AuthenticationPrincipal AccountAdapter accountAdapter) throws NoSuchMethodException {
        service.updatePassword(newPassword, accountAdapter, "/api/auth/changepass");
        return ResponseEntity.ok(Map.of(
                "email", accountAdapter.getUsername(),
                "status", "The password has been updated successfully"));
    }

    @GetMapping("/api/empl/payment")
    public ResponseEntity<?> viewPayment(@AuthenticationPrincipal AccountAdapter accountAdapter, @RequestParam(required = false) Optional<String> period) {
        List<AccountPaymentsDTO> list = dtoMapper.convertAccountToAccountPaymentsDTOs(accountAdapter.getAccount());
        if(period.isEmpty()) {
            return ResponseEntity.ok(list);
        }
        if(!period.get().matches("(0[1-9]|1[0-2])-[0-9]+")) {
            throw new  CustomException("Date is invalid","/api/empl/payment");
        }
        return ResponseEntity.ok(list.stream().filter(dto -> dto.getPeriod().equals(DtoMapper.convertData(period.get()))).findFirst().orElse(new AccountPaymentsDTO()));
    }


    @PostMapping("/api/acct/payments")
    public ResponseEntity<?> addPayments(@RequestBody List<@Valid PaymentDTO> paymentDTOList) {
        service.savePayments(paymentDTOList, "/api/acct/payments");
        return ResponseEntity.ok(Map.of("status", "Added successfully!"));
    }

    @PutMapping("/api/acct/payments")
    public ResponseEntity<?> updateSalary(@RequestBody List<@Valid PaymentDTO> paymentDTOList) {
        service.updateSalary(paymentDTOList, "/api/acct/payments");
        return ResponseEntity.ok(Map.of("status", "Updated successfully!"));
    }

    @GetMapping("/api/admin/user/")
    public ResponseEntity<?> getAccounts() {
        return ResponseEntity.ok(service.getAccounts());
    }

    @DeleteMapping("/api/admin/user/{userEmail}")
    public ResponseEntity<?> deleteAccount(@PathVariable() String userEmail, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        service.deleteAccount(userEmail, accountAdapter);
        return ResponseEntity.ok((Map.of(
                "user", userEmail,
                "status", "Deleted successfully!")));
    }

    @PutMapping("/api/admin/user/role")
    public ResponseEntity<?> changeRoles(@RequestBody @Valid RoleChanger roleChanger, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        return ResponseEntity.ok(service.changeRoles(roleChanger, accountAdapter));
    }

    @PutMapping("/api/admin/user/access")
    public ResponseEntity<?> lock(@RequestBody @Valid Locker locker, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        service.lock(locker, accountAdapter);
        return ResponseEntity.ok(Map.of("status", "User " + locker.getUser().toLowerCase() + " " + locker.getOperation().name().toLowerCase() + "ed!"));
    }

    @GetMapping("/api/security/events/")
    public ResponseEntity<?> getEvents() {
        return ResponseEntity.ok(service.getEvents());
    }
}
