package ru.family.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.family.auth.dto.UserCredentialsDTO;
import ru.family.auth.repository.CredentialsStorage;
import ru.family.auth.repository.UserStorage;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CredentialsStorage credentialsStorage;
    private final UserStorage userStorage;

    public UserCredentialsDTO authenticate(String email) {
        var user = credentialsStorage.findByLogin(email)
                .orElseThrow(() -> {
                    log.error("User with login {} not found!", email);
                    throw new EntityNotFoundException(format("User with email %s not found!", email));
                });
        return UserCredentialsDTO.builder()
                .username(user.getLogin())
                .password(user.getPassword()).build();
    }

    public BigInteger getUserIdByLogin(String login) {

        var user = userStorage.findByPrimaryEmail(login);
        if (user.isPresent()) {
            return user.get().getId();
        }
        throw new EntityNotFoundException(format("User with email %s not found!", login));

    }
}
