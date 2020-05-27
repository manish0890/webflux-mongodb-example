package com.manish0890.application.constants;

public class RequestMappingConstants {

    private RequestMappingConstants(){
        // Private constructor for enabling only static access
    }

    public static final String ROOT = "/";

    // User API Endpoints
    public static final String USER = ROOT + "user";
    public static final String USER_BY_ID = "/{id}";
    public static final String USER_GET_BY_PHONE = "/phone/{phone}";
    public static final String USER_GET_BY_ZIP = "/zip/{zip}";
    public static final String USER_GET_ALL = "/all";

}
