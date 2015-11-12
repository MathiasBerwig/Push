package com.acme.push;

public class Config {

    // TODO: Find a better way to store this settings
    public static class ADRESSES {
        public static final String SERVER_URL = "http://200.132.198.178/";
        public static final String INSERT_USER_URL = SERVER_URL + "Push-server/insert_user.php";
        public static final String DELETE_USER_URL = SERVER_URL + "Push-server/delete_user.php";

    }

    public static class GCM {
        public static final String DEFAULT_SENDER_ID = "1041547163175";
    }

}
