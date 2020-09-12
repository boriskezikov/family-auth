package ru.family.auth.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Builder
@Data
public class TwoFaDTO {
    @Email(regexp = ".+@.+\\..+")
    private String mail;
    private String twoFaCode;
}
