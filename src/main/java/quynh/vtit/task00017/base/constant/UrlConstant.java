package quynh.vtit.task00017.base.constant;

public class UrlConstant {

    public static class AUTH {
        private static final String PRE_FIX = "/auth";
        public static final String REGISTER = PRE_FIX + "/register";
        public static final String LOGIN = PRE_FIX + "/login";
        public static final String LOGOUT = PRE_FIX + "/logout";
        public static final String FORGOT_PASSWORD = PRE_FIX + "/forgot-password";
        public static final String RESET_PASSWORD = PRE_FIX + "/reset-password";
        public static final String PASSWORD = PRE_FIX + "/change-password";
        public static final String USERS_ME = PRE_FIX + "/users/me";

        private AUTH(){}
    }

    public static class User {
        private static final String PRE_FIX = "/user";
        public static final String GET_PROFILE = PRE_FIX + "/profile";
        public static final String Get_All_PROFILE = PRE_FIX + "/all";
        public static final String UPDATE_PROFILE = PRE_FIX + "/profile";
        public static final String CHANGE_PASSWORD = PRE_FIX + "/change-password";
        private User() {
        }
    }

    public static class Category {
        private static final String PRE_FIX = "/categories";
        public static final String Get_All = PRE_FIX + "/all";
        public static final String CREATE = PRE_FIX + "/creation";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        private Category() {
        }
    }

    public static class Wallet {
        private static final String PRE_FIX = "/wallets";
        public static final String GET_ALL = PRE_FIX + "/all";
        public static final String CREATE = PRE_FIX + "/creation";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";

        private Wallet() {
        }
    }

    public static class Transaction {
        private static final String PRE_FIX = "/transactions";
        public static final String GET_ALL = PRE_FIX + "/all";
        public static final String GET_DETAIL = PRE_FIX + "/{id:\\d+}";
        public static final String CREATE = PRE_FIX + "/creation";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String EXPORT = PRE_FIX + "/export";

        private Transaction() {
        }
    }
    public static class Export {
        private static final String PRE_FIX = "/export";
        public static final String TRANSACTION = PRE_FIX + "/transactions";
        public static final String RECONCILIATION_TRANSACTION = PRE_FIX + "/reconciliation/transactions";
        public static final String GET_DETAIL = PRE_FIX + "/{id}";
        public static final String CREATE = PRE_FIX + "/creation";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String EXPORT = PRE_FIX + "/export";

        private Export() {
        }
    }

    public static class Budget{
        private static final String PRE_FIX = "/budgets";
        public static final String GET_ALL = PRE_FIX + "/all";
        public static final String CREATE = PRE_FIX + "/creation";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";

        private Budget(){}
    }

}
