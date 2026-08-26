package quynh.vtit.task00017.base.constant;

public class ErrorMessage {

    public static final String ERR_EXCEPTION_GENERAL = "exception.general";
    public static final String UNAUTHORIZED = "exception.unauthorized";
    public static final String FORBIDDEN = "exception.forbidden";
    public static final String BAD_REQUEST = "exception.bad.request";
    public static final String FORBIDDEN_UPDATE_DELETE = "exception.forbidden.update-delete";
    public static final String ERR_UPLOAD_IMAGE_FAIL = "exception.upload.image.fail";

    public static final String INVALID_SOME_THING_FIELD = "invalid.general";
    public static final String INVALID_FORMAT_SOME_THING_FIELD = "invalid.general.format";
    public static final String INVALID_SOME_THING_FIELD_IS_REQUIRED = "invalid.general.required";
    public static final String NOT_BLANK_FIELD = "invalid.general.not-blank";
    public static final String INVALID_FORMAT_PASSWORD = "invalid.password-format";
    public static final String INVALID_PASSWORD = "invalid.password";
    public static final String INVALID_FORMAT_EMAIL = "invalid.email-format";
    public static final String INVALID_FORMAT_FULL_NAME = "invalid.full-name-format";

    public static class Auth {
        public static final String ERR_INVALID_CREDENTIALS = "exception.auth.username.or.password.wrong";
        public static final String INVALID_REFRESH_TOKEN = "exception.auth.invalid.refresh.token";
        public static final String EXPIRED_REFRESH_TOKEN = "exception.auth.expired.refresh.token";
        public static final String ERR_LOGIN_FAIL = "exception.auth.login.fail";
        public static final String ERR_GET_TOKEN_CLAIM_SET_FAIL = "exception.auth.get.token.claim.set.fail";
        public static final String ERR_TOKEN_EXPIRED = "exception.auth.token.expired";
        public static final String ERR_TOKEN_INVALIDATED = "exception.auth.token.invalidated";
        public static final String ERR_MALFORMED_TOKEN = "exception.auth.malformed.token";
        public static final String ERR_TOKEN_ALREADY_INVALIDATED = "exception.auth.token.already.invalidated";
        public static final String ERR_INVALID_OTP = "exception.auth.otp.invalid";
    }

    public static class User {
        public static final String ERR_USER_NOT_EXISTED = "exception.user.user.not.existed";
        public static final String ERR_USERNAME_EXISTED = "exception.user.username.existed";
        public static final String ERR_EMAIL_EXISTED = "exception.user.email.existed";
        public static final String ERR_PHONE_EXISTS = "exception.user.phone.exists";
    }

    public static class Admin {
        public static final String ERR_NOT_ADMIN = "exception.admin.not.admin";
    }

    public static class Category {
        public static final String ERR_CATEGORY_NOT_FOUND = "exception.category.not.found";
        public static final String ERR_CATEGORY_EXISTS = "exception.category.exists";
        public static final String ERR_CATEGORY_IS_SYSTEM = "exception.category.issystem";
    }

    public static class Wallet {
        public static final String ERR_WALLET_NOT_FOUND = "exception.wallet.not.found";
        public static final String ERR_WALLET_EXISTS = "exception.wallet.exists";
    }

    public static class Transaction {
        public static final String ERR_TRANSACTION_NOT_FOUND = "exception.transaction.not.found";
        public static final String ERR_TRANSFER_WALLET_REQUIRED = "exception.transaction.transfer.wallet.required";
        public static final String ERR_TRANSFER_WALLET_SAME = "exception.transaction.transfer.wallet.same";
        public static final String ERR_TRANSACTION_CURRENCY_MISMATCH = "exception.transaction.currency.mismatch";
        public static final String ERR_TRANSFER_CURRENCY_MISMATCH = "exception.transaction.transfer.currency.mismatch";
        public static final String ERR_INSUFFICIENT_BALANCE = "exception.transaction.insufficient.balance";
    }

    public static class Budget{
        public static final String ERR_INVALID_DATE_RANGE = "exception.budget.invalid.date.range";
        public static final String ERR_BUDGET_EXISTED = "exception.budget.existed";
        public static final String ERR_BUDGET_NOT_FOUND = "exception.budget.notfound";
    }
}
