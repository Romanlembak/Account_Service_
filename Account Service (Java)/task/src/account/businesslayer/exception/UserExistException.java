package account.businesslayer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
public class UserExistException extends CustomException{
    public UserExistException(String message, String path) {
        super(message, path);
    }
}
