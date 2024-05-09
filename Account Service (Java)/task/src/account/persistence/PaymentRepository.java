package account.persistence;

import account.businesslayer.entity.Account;
import account.businesslayer.entity.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByEmployeeAndPeriod(Account employee, String period);
}
