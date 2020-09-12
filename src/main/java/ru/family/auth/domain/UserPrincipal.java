package ru.family.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.math.BigInteger;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_principal")
@EqualsAndHashCode(of = "id")
public class UserPrincipal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_principal_ids_gen")
    @SequenceGenerator(name = "user_principal_ids_gen", sequenceName = "user_principal_id_seq", allocationSize = 1)
    private BigInteger id;

    @Column(nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;

    private String twoFaCode;

    @OneToOne(fetch = FetchType.EAGER)
    private PublicUser publicUser;
}
