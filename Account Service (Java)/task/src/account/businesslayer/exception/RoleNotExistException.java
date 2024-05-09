package account.businesslayer.exception;

public class RoleNotExistException extends CustomException{
    public RoleNotExistException(String message, String path) {
        super(message, path);
    }
}
