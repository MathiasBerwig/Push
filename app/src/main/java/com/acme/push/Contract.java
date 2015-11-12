package com.acme.push;

public class Contract {

    public static final String appId = "com.acme.push";

    public static final class REGISTRATION {
        public static final String REGISTRATION_COMPLETE = "registrationComplete";
        public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

        public static final String MESSAGE_RECEIVED = "messageReceived";
        public static final String TOKEN = "token";
        public static final String USERNAME = "username";
    }
}
