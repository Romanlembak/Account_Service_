package account.businesslayer.exception;

public class OnlyOneRoleException extends CustomException{
    public OnlyOneRoleException(String message, String path) {
        super(message, path);
    }
}
