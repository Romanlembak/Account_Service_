package account.businesslayer.mapper;

import account.businesslayer.entity.Account;
import account.businesslayer.entity.Role;
import account.presentation.dto.AccountDTO;
import account.presentation.dto.AccountPaymentsDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

@Component
public class DtoMapper {
    public List<AccountPaymentsDTO> convertAccountToAccountPaymentsDTOs(Account account) {
        return account.getPayments()

                .stream()
                .map(payment -> new AccountPaymentsDTO(account.getName(), account.getLastname(), convertData(payment.getPeriod()), String.format("%d dollar(s) %d cent(s)", payment.getSalary() / 100, payment.getSalary() % 100)))
                .sorted(Comparator.comparing(dto -> ((AccountPaymentsDTO) dto).getPeriod().transform(str -> LocalDate.of(Integer.parseInt(str.split("-")[1]), Month.valueOf(str.split("-")[0].toUpperCase()).getValue(), 1))).reversed())
                .toList();
    }

    public static String convertData(String input) {
        String startChars = input.split("-")[0];
        String month = Month.of(Integer.parseInt(startChars)).name();
        return month.charAt(0) + month.substring(1).toLowerCase() + input.substring(startChars.length());
    }

    public AccountDTO convertAccountToAccountDTO(Account account) {
        return new AccountDTO(account.getId(), account.getName(), account.getLastname(), account.getEmail(), account.getRoles().stream().map(Role::getName).toList());
    }
}
