package ru.family.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.family.auth.configuration.ASBusinessException;
import ru.family.auth.domain.LoginEnum;
import ru.family.auth.domain.PublicUser;
import ru.family.auth.domain.UserPrincipal;
import ru.family.auth.dto.TwoFaDTO;
import ru.family.auth.dto.UserRegistrationDTO;
import ru.family.auth.dto.Verify2FaDTO;
import ru.family.auth.repository.CredentialsStorage;
import ru.family.auth.repository.UserStorage;

import static java.lang.String.format;
import static ru.family.auth.kafka.KafkaConstants.MAIL_2FA_TOPIC;
import static ru.family.auth.utils.UtilsHolder.generate2FaCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final KafkaTemplate<String, TwoFaDTO> kafkaTemplate;
    private final UserStorage userStorage;
    private final CredentialsStorage credentialsStorage;

    public PublicUser register(UserRegistrationDTO userRegistrationDTO) {
        var registered = credentialsStorage.findByLoginAndPassword(userRegistrationDTO.getEmail(), userRegistrationDTO.getPassword());
        if (registered.isEmpty()) {
            var user = PublicUser.builder()
                    .firstName(userRegistrationDTO.getFirstName())
                    .lastName(userRegistrationDTO.getLastName())
                    .primaryEmail(userRegistrationDTO.getEmail())
                    .principal(UserPrincipal.builder()
                            .login(userRegistrationDTO.getEmail())
                            .password(userRegistrationDTO.getPassword())
                            .twoFaCode(generate2FaCode())
                            .build()
                    )
                    .build();

            var saved = userStorage.save(user);
            log.info("Created temporary user: {}. Waiting for activation!", user);
            kafkaTemplate.send(MAIL_2FA_TOPIC, TwoFaDTO.builder()
                    .mail(user.getPrimaryEmail())
                    .twoFaCode(user.getPrincipal().getTwoFaCode())
                    .build());
            return saved;
        }
        throw new ASBusinessException(format("User already registered! Registration date: %s", registered.get().getPublicUser().getCreated()));
    }

    public LoginEnum validate2Fa(final Verify2FaDTO verify2FaDTO) {
        var publicUserOpt = userStorage.findByPrimaryEmail(verify2FaDTO.getEmail());
        if (publicUserOpt.isPresent()) {
            var publicUser = publicUserOpt.get();
            if (publicUser.getPrincipal().getTwoFaCode().equals(verify2FaDTO.getTwoFaCode())) {
                publicUser.setEnabled(true);
                userStorage.save(publicUser);
                log.info("Two-factor verification code accepted");
                return LoginEnum.CORRECT;
            } else {
                log.info("Two-factor verification code does not match!");
                return LoginEnum.CODES_DOES_NOT_MATCH;
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
    }


    public void resend2Fa(final String mail) {
        userStorage.findByPrimaryEmail(mail).ifPresent(
                user -> {
                    if (user.getPrimaryEmail().equals(mail)) {
                        user.getPrincipal().setTwoFaCode(generate2FaCode());
                        userStorage.save(user);
                        kafkaTemplate.send(MAIL_2FA_TOPIC, TwoFaDTO.builder()
                                .mail(mail)
                                .twoFaCode(user.getPrincipal().getTwoFaCode())
                                .build());
                    }
                }
        );
        log.info("Verification code sent!");
    }

}
