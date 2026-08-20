package quynh.vtit.task00017.base.constant;

public class UrlConstant {

    public static class AUTH {
        public static final String PRE_FIX = "/auth";
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
        public static final String PRE_FIX = "/user";
        public static final String GET_PROFILE = PRE_FIX + "/profile";
        public static final String Get_All_PROFILE = PRE_FIX + "/all";
        public static final String UPDATE_PROFILE = PRE_FIX + "/profile";
        public static final String CHANGE_PASSWORD = PRE_FIX + "/change-password";
        private User() {
        }
    }

    public static class Category {
        public static final String PRE_FIX = "/categories";
        public static final String Get_All = PRE_FIX + "/all";
        public static final String CREATE = PRE_FIX + "/creation";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        private Category() {
        }
    }
}
