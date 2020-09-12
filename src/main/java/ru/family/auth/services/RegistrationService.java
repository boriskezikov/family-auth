package ru.family.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static ru.family.auth.kafka.KafkaConstants.MAIL_2FA_TOPIC;
import static ru.family.auth.utils.UtilsHolder.generate2FaCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final KafkaTemplate<String, TwoFaDTO> kafkaTemplate;
    private final UserStorage userStorage;
    private final CredentialsStorage credentialsStorage;
    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PublicUser register(UserRegistrationDTO userRegistrationDTO) {
        var registered = credentialsStorage.findByLoginAndPassword(userRegistrationDTO.getEmail(), userRegistrationDTO.getPassword());
        var now = LocalDateTime.now();
        if (registered.isEmpty()) {

            UserPrincipal userPrincipal = UserPrincipal.builder()
                    .login(userRegistrationDTO.getEmail())
                    .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                    .twoFaCode(generate2FaCode())
                    .build();


            PublicUser user = PublicUser.builder()
                    .firstName(userRegistrationDTO.getFirstName())
                    .lastName(userRegistrationDTO.getLastName())
                    .primaryEmail(userRegistrationDTO.getEmail())
                    .principal(credentialsStorage.save(userPrincipal))
                    .created(now)
                    .updated(now)
                    .build();

            var saved = userStorage.save(user);
            log.info("Created temporary user: {}. Waiting for activation!", user);
            kafkaTemplate.send(MAIL_2FA_TOPIC, TwoFaDTO.builder()
                    .mail(user.getPrimaryEmail())
                    .twoFaCode(user.getPrincipal().getTwoFaCode())
                    .build());
            return saved;
        }
        throw new ASBusinessException("User already registered!");
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

    /**
     * Send new two-factor auth code
     */
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


    /**
     * Deletes user temporary account if registration is not complete.
     */
    @Scheduled(cron = "0 0/15 * * * *")
    public void deleteUnsignedUserJob() {
        var now = LocalDateTime.now();
        ((List<PublicUser>) userStorage.findAll())
                .stream()
                .parallel()
                .filter(user -> !user.isEnabled())
                .forEach(user -> {
                    var registered = user.getCreated();
                    if (Duration.between(registered, now).toMinutes() >= 15) {
                        userStorage.delete(user);
                        log.warn("User {} has not confirmed his registration and has been removed.", user.getId());
                    }
                });
    }

}
