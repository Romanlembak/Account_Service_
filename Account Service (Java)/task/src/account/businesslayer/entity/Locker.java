package account.businesslayer.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public class Locker {
    @NotBlank
    String user;
    @Enumerated(EnumType.STRING)
    LockUnlockOption operation;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LockUnlockOption getOperation() {
        return operation;
    }

    public void setOperation(LockUnlockOption operation) {
        this.operation = operation;
    }
}
