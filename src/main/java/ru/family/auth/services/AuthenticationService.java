package ru.family.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.family.auth.dto.UserCredentialsDTO;
import ru.family.auth.repository.CredentialsStorage;

import javax.persistence.EntityNotFoundException;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CredentialsStorage credentialsStorage;

    public void authenticate(UserCredentialsDTO userCredentialsDTO) {
        credentialsStorage.findByLoginAndPassword(userCredentialsDTO.getLogin(), userCredentialsDTO.getPassword())
                .orElseThrow(() -> {
                    log.error("User with credentials {} not found!", userCredentialsDTO);
                    throw new EntityNotFoundException(format("User with credentials %s not found!", userCredentialsDTO));
                });
    }
}
