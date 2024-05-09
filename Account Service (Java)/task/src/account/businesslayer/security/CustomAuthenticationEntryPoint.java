package account.businesslayer.security;

import account.businesslayer.advice.ExceptionBodyResponseWithMessage;
import account.businesslayer.entity.Account;
import account.businesslayer.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final AccountService service;


    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper, AccountService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        String message = "";
        if (header != null) {
            String email = new String(Base64.getDecoder().decode(request.getHeader("Authorization").split(" ")[1])).split(":")[0];
            String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            Optional<Account> optional = service.getAccount(email);
            if (optional.isPresent()) {
                Account account = optional.get();
                if(account.isAccountNonLocked()) {
                    service.saveEvent("LOGIN_FAILED", email, path, path);
                    if (account.getFailedAttempt() < AccountService.MAX_FAILED_ATTEMPTS) {
                        service.increaseFailedAttempts(account);
                    } else {
                        service.saveEvent("BRUTE_FORCE", email, path, path);
                        service.lock(account);
                        service.saveEvent("LOCK_USER", account.getEmail(), "Lock user " + account.getEmail(), path);
                    }
                } else  {
                    message = "User account is locked";
                }
            } else {
                service.saveEvent("LOGIN_FAILED", email, path, path);
            }
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(401);
        ExceptionBodyResponseWithMessage body = new ExceptionBodyResponseWithMessage( (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI), message);
        body.setError("Unauthorized");
        body.setStatus(401);
        response.getWriter().print(objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(body));
    }
}
