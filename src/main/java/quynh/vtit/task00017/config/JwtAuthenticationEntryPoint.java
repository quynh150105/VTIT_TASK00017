package quynh.vtit.task00017.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import quynh.vtit.task00017.base.ApiResponse;
import quynh.vtit.task00017.base.constant.ErrorMessage;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.error(resolveMessage(exception)));
    }

    private String resolveMessage(AuthenticationException exception) {
        if (!(exception instanceof OAuth2AuthenticationException oauthException)) {
            return ErrorMessage.UNAUTHORIZED;
        }

        String detail = (oauthException.getError().getErrorCode() + " "
                + oauthException.getError().getDescription()).toLowerCase(Locale.ROOT);
        if (detail.contains("revoked")) {
            return ErrorMessage.Auth.ERR_TOKEN_ALREADY_INVALIDATED;
        }
        if (detail.contains("expired")) {
            return ErrorMessage.Auth.ERR_TOKEN_EXPIRED;
        }
        return ErrorMessage.Auth.ERR_MALFORMED_TOKEN;
    }
}
