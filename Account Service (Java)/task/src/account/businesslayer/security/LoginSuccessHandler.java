package account.businesslayer.security;

import account.businesslayer.entity.Account;
import account.businesslayer.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AccountService service;

    public LoginSuccessHandler(AccountService service) {
        this.service = service;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Account account = ((AccountAdapter) authentication.getPrincipal()).getAccount();
        if(account.getFailedAttempt() > 0) {
            service.resetFailedAttempts(account);
        }
    }
}
