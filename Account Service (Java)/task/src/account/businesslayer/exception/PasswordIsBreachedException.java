package account.businesslayer.exception;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.BindException;


public class PasswordIsBreachedException extends CustomException{
    public PasswordIsBreachedException(String message, String path) {
        super(message, path);
    }
}
