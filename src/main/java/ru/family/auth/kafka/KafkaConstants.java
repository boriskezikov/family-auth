package ru.family.auth.kafka;

public interface KafkaConstants {
    String BOOTSTRAP = "moped-01.srvs.cloudkafka.com:9094,moped-02.srvs.cloudkafka.com:9094,moped-03.srvs.cloudkafka.com:9094";
    String USERNAME = "2z2j7jw9";
    String PASSWORD = "gGBcXuQW6peGnHhInj9TE09n_d4IadbE";
    String TOPIC_PREFIX = USERNAME + "-";

    String MAIL_2FA_TOPIC = TOPIC_PREFIX + "mail-auth2fa-account-created";
    String PASSWORD_RESET = TOPIC_PREFIX + "password-reset";
}
