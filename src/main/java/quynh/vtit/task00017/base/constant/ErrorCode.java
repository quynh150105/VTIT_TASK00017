package quynh.vtit.task00017.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),

    CATEGORY_HAS_PRODUCTS(HttpStatus.CONFLICT, "Category has products"),

    CATEGORY_SLUG_ALREADY_EXISTS(HttpStatus.CONFLICT, "Category slug already exists"),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),

    PRODUCT_SLUG_ALREADY_EXISTS(HttpStatus.CONFLICT, "Product slug already exists"),

    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "Current password is invalid"),

    INVALID_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED,
            "Username or password is incorrect"),

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED,
            "Invalid credentials"),

    ACCOUNT_BLOCKED(HttpStatus.FORBIDDEN,
            "Account is blocked"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "Unauthorized"),

    FORBIDDEN(HttpStatus.FORBIDDEN,
            "Forbidden"),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,
            "Token is invalid"),

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,
            "Refresh token is invalid"),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,
            "Token has expired"),

    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,
            "Refresh token has expired"),

    TOKEN_ALREADY_INVALIDATED(HttpStatus.UNAUTHORIZED,
            "Token has been logged out"),

    ACCESS_DENIED(HttpStatus.FORBIDDEN,
            "Access denied"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error");

    private final HttpStatus status;
    private final String message;
}
