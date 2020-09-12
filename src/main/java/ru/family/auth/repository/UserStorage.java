package ru.family.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.family.auth.domain.PublicUser;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserStorage extends CrudRepository<PublicUser, BigInteger> {

    Optional<PublicUser> findByPrimaryEmail(String email);
}
