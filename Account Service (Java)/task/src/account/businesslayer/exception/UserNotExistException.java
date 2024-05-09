package account.businesslayer.exception;

public class UserNotExistException extends CustomException{
    public UserNotExistException(String message, String path) {
        super(message, path);
    }
}
