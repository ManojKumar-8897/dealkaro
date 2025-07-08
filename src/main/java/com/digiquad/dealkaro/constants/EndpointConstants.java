package com.digiquad.dealkaro.constants;

public class EndpointConstants {

    /**
     * API Prefixes
     */
    public static final String API_VERSION_1 = "v1";
    public static final String WEB_API_PREFIX = "/web/api/" + API_VERSION_1;

    /**
     * COMMON PATHS
     */
    public static final String VIEW = "/view";
    public static final String UPDATE = "/update";
    public static final String UNIQUE_CHECK = "/uniquecheck";
    public static final String DELETE = "/{id}";

    /**
     * Authentication Paths
     */
    public static final String AUTHENTICATION_API = WEB_API_PREFIX + "/auth";
    public static final String LOGIN = AUTHENTICATION_API + "/login";


    public static final String REFRESH_TOKEN = AUTHENTICATION_API + "/refreshToken";

    /**
     * Admin related endpoints
     */
    public static final String ADMIN_API = WEB_API_PREFIX + "/admin";
    public static final String APPROVE_USER = ADMIN_API + "/approve/user/{userId}";
    public static final String ADMIN_VIEW = ADMIN_API + VIEW;
    public static final String ADMIN_UPDATE = ADMIN_API + UPDATE;
    public static final String ADMIN_DELETE = ADMIN_API + DELETE;

    /**
     * User related endpoints
     */
    public static final String USER_API = WEB_API_PREFIX + "/user";
    public static final String USER_REGISTER = USER_API + "/register";
    public static final String USER_VIEW = USER_API + VIEW;
    public static final String USER_UPDATE = USER_API + UPDATE;
    public static final String USER_DELETE = USER_API + DELETE;
    public static final String USER_UNIQUECHECK = USER_API + UNIQUE_CHECK;
    public static final String USER_SET_PASSWORD = USER_API + "/setPassword";


    public static final String USER_LOGOUT = USER_API + "/logout";
    public static final String USER_LOGOUT_ALL = USER_API + "/logout-all";
    public static final String USER_SESSIONS = USER_API + "/sessions";


    //super admin


    public static final String SUPER_ADMIN_API = WEB_API_PREFIX + "/superadmin";
    public static final String SUPER_ADMIN_VIEW = SUPER_ADMIN_API + VIEW;
    public static final String IMAGE_UPLOAD_URL = SUPER_ADMIN_API + "/uploadimage";
    public static final String SUPER_ADMIN_UPDATE = SUPER_ADMIN_API + "/update/superadmin";
}