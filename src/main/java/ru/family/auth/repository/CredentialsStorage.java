package ru.family.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.family.auth.domain.UserPrincipal;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CredentialsStorage extends CrudRepository<UserPrincipal, BigInteger> {

    Optional<UserPrincipal> findByLoginAndPassword(String login, String password);
    Optional<UserPrincipal> findByLogin(String login);
}
