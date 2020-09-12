package ru.family.auth.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
public class Verify2FaDTO {
    @Email(regexp = ".+@.+\\..+")
    @NotNull
    private String email;
    @NotNull
    private String twoFaCode;
}
