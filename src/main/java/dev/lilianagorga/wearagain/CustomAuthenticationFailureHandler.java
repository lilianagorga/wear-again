package dev.lilianagorga.wearagain;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
    setDefaultFailureUrl("/login?error=true");
    super.onAuthenticationFailure(request, response, exception);
    String errorMessage = exception.getMessage();
    request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", errorMessage);
  }
}


