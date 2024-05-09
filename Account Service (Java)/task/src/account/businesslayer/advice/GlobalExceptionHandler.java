package account.businesslayer.advice;

import account.businesslayer.exception.*;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.ConstraintViolationException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({CustomException.class, UserExistException.class, PasswordIsBreachedException.class, IdenticalPasswordExceptions.class, UserNotExistException.class, PaymentNotExistException.class, AdministratorRoleIsNotRemovableException.class, OnlyOneRoleException.class})
    public ResponseEntity<?> handleCustomException(CustomException e){
        if(e.getMessage().matches(".* not (found|exist)!")) {
            ExceptionBodyResponseWithMessage body = new ExceptionBodyResponseWithMessage(e.getPath(), e.getMessage());
            body.setError("Not Found");
            body.setStatus(404);
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ExceptionBodyResponseWithMessage(e.getPath(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String path = e.getParameter().getMethod().getAnnotation(PostMapping.class).value()[0];
        if(e.getMessage().matches(".*passwordValidateError.*")) {
            return new ResponseEntity<>(new ExceptionBodyResponseWithMessage(path, "Password length must be 12 chars minimum!"), HttpStatus.BAD_REQUEST);
        } else if (e.getMessage().matches(".*SalaryValidateError.*")) {
            return new ResponseEntity<>(new ExceptionBodyResponseWithMessage(path, "Salary must be positive value"), HttpStatus.BAD_REQUEST);
        } else if (e.getMessage().matches(".*DataValidateError.*")) {
            return new ResponseEntity<>(new ExceptionBodyResponseWithMessage(path, "Date is invalid"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ExceptionBodyResponse(path), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException() {
            return new ResponseEntity<>(new ExceptionBodyResponseWithMessage("/api/acct/payments", "Date and period is invalid"), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(JdbcSQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleJdbcSQLIntegrityConstraintViolationException() {
        return new ResponseEntity<>(new ExceptionBodyResponseWithMessage("/api/acct/payments", "Payment is not unique"), HttpStatus.BAD_REQUEST);
    }
}
