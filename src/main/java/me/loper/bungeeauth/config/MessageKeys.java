package me.loper.bungeeauth.config;

import me.loper.bungeeauth.util.ImmutableCollectors;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class MessageKeys {

    private MessageKeys() {}

    public static final ConfigKey<Message> FAIL_SESSION_CREATION = ConfigKeyTypes.messageKey(
            c -> c.getString("fail-session-creation", ""));

    public static final ConfigKey<Message> FAILED_AUTHENTICATION = ConfigKeyTypes.messageKey(
            c -> c.getString("failed-authentication", ""));

    public static final ConfigKey<Message> LOGIN_TIMEOUT = ConfigKeyTypes.messageKey(
            c -> c.getString("timeout-login", ""));

    public static final ConfigKey<Message> FAILED_REGISTER = ConfigKeyTypes.messageKey(
            c -> c.getString("failed-register", ""));

    public static final ConfigKey<Message> TEMPORARY_FORBIDDEN_ACCESS = ConfigKeyTypes.messageKey(
            c -> c.getString("temporary-forbidden-access", ""));

    public static final ConfigKey<Message> FORBIDDEN_ACCESS = ConfigKeyTypes.messageKey(
            c -> c.getString("forbidden-access", ""));

    public static final ConfigKey<Message> EXCEEDED_LOGIN_ATTEMPTS = ConfigKeyTypes.messageKey(
            c -> c.getString("exceeded-login-attempts", ""));

    public static final ConfigKey<Message> BAD_NICKNAME = ConfigKeyTypes.messageKey(
            c -> c.getString("bad-nickname", ""));

    public static final ConfigKey<Message> BAD_REQUEST = ConfigKeyTypes.messageKey(
            c -> c.getString("bad-request", ""));

    public static final ConfigKey<Message> WRONG_PASSWORD = ConfigKeyTypes.messageKey(
            c -> c.getString("wrong-password", ""));

    public static final ConfigKey<Message> PASSWORD_MISMATCH = ConfigKeyTypes.messageKey(
            c -> c.getString("password-mismatch", ""));

    public static final ConfigKey<Message> PASSWORD_MIN_LENGTH = ConfigKeyTypes.messageKey(
            c -> c.getString("password-min-length", ""));

    public static final ConfigKey<Message> ACCOUNT_NOT_REGISTERED = ConfigKeyTypes.messageKey(
            c -> c.getString("account-not-registered", ""));

    public static final ConfigKey<Message> USER_AUTHENTICATED = ConfigKeyTypes.messageKey(
            c -> c.getString("user-authenticated", ""));

    public static final ConfigKey<Message> LOGIN_USAGE = ConfigKeyTypes.messageKey(
            c -> c.getString("usage-account-login", ""));

    public static final ConfigKey<Message> LOGIN_TITLE = ConfigKeyTypes.messageKey(
            c -> c.getString("login-title", ""));

    public static final ConfigKey<Message> LOGIN_SUBTITLE = ConfigKeyTypes.messageKey(
            c -> c.getString("login-subtitle", ""));

    public static final ConfigKey<Message> LOGIN_CHAT_MESSAGE = ConfigKeyTypes.messageKey(
            c -> c.getString("tip-account-login", ""));

    public static final ConfigKey<Message> REGISTER_TITLE = ConfigKeyTypes.messageKey(
            c -> c.getString("register-title", ""));

    public static final ConfigKey<Message> REGISTER_SUBTITLE = ConfigKeyTypes.messageKey(
            c -> c.getString("register-subtitle", ""));

    public static final ConfigKey<Message> REGISTER_CHAT_MESSAGE = ConfigKeyTypes.messageKey(
            c -> c.getString("tip-account-register", ""));

    public static final ConfigKey<Message> ALREADY_REGISTERED = ConfigKeyTypes.messageKey(
            c -> c.getString("already-registered", ""));

    public static final ConfigKey<Message> NO_ACTUAL_SERVER = ConfigKeyTypes.messageKey(
            c -> c.getString("no-actual-server", ""));

    public static final ConfigKey<Message> VERSION_OUTDATED = ConfigKeyTypes.messageKey(
            c -> c.getString("version-outdated", ""));

    public static final ConfigKey<Message> CHANGEPASSWORD_SELF_USAGE = ConfigKeyTypes.messageKey(
        c -> c.getString("changepassword-usage-self", ""));

    public static final ConfigKey<Message> CHANGEPASSWORD_OTHER_USAGE = ConfigKeyTypes.messageKey(
        c -> c.getString("changepassword-usage-other", ""));

    public static final ConfigKey<Message> CHANGEPASSWORD_OTHER_SUCCESS = ConfigKeyTypes.messageKey(
        c -> c.getString("changepassword-success-other", ""));

    public static final ConfigKey<Message> CHANGEPASSWORD_SELF_SUCCESS = ConfigKeyTypes.messageKey(
        c -> c.getString("changepassword-success-self", ""));


    private static final List<ConfigKeyTypes.BaseConfigKey<?>> KEYS;

    static {
        // get a list of all keys
        KEYS = Arrays.stream(MessageKeys.class.getFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> ConfigKey.class.equals(f.getType()))
                .map(f -> {
                    try {
                        return (ConfigKeyTypes.BaseConfigKey<?>) f.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(ImmutableCollectors.toList());

        // set ordinal values
        for (int i = 0; i < KEYS.size(); i++) {
            KEYS.get(i).ordinal = i;
        }
    }

    /**
     * Gets a list of the keys defined in this class.
     *
     * @return the defined keys
     */
    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}