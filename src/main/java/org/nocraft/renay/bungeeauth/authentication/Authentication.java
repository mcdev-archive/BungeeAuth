package org.nocraft.renay.bungeeauth.authentication;

public interface Authentication {
    enum Result {
        SUCCESS_LOGIN,
        WRONG_PASSWORD,
        AUTHENTICATION_FAILED,
        ALREADY_AUTHENTICATED
    }
}
