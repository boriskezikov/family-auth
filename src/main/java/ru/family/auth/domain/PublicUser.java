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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "public_user")
@EqualsAndHashCode(of = "id")
public class PublicUser {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "public_user_ids_gen")
    @SequenceGenerator(name = "public_user_ids_gen", sequenceName = "public_user_id_seq", allocationSize = 1)
    private BigInteger id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    @Pattern(regexp = "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$", flags = Pattern.Flag.UNICODE_CASE)
    private transient String phone;

    @Column(nullable = false, unique = true)
    @Email(regexp = ".+@.+\\..+")
    private String primaryEmail;

    @Email(regexp = ".+@.+\\..+")
    @Column(unique = true)
    private String secondaryEmail;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime updated;

    @Column
    private boolean isEnabled;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_principal_id", nullable = false)
    private UserPrincipal principal;


}
