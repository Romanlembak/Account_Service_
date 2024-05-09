package account.businesslayer.security;

import account.businesslayer.advice.ExceptionBodyResponseWithMessage;
import account.businesslayer.entity.Event;
import account.businesslayer.service.AccountService;
import account.persistence.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.awt.*;
import java.io.IOException;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    private final AccountService service;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper, AccountService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(403);
        ExceptionBodyResponseWithMessage body = new ExceptionBodyResponseWithMessage( request.getServletPath(), "Access Denied!");
        body.setError("Forbidden");
        body.setStatus(403);
        response.getWriter().print(objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(body));
        service.saveEvent("ACCESS_DENIED", request.getUserPrincipal().getName(), request.getServletPath(), request.getServletPath());
    }
}
