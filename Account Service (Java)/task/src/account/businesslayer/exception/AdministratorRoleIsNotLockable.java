package account.businesslayer.exception;

public class AdministratorRoleIsNotLockable extends CustomException {
    public AdministratorRoleIsNotLockable(String massage, String path) {
        super(massage, path);
    }
}
