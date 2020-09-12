package ru.family.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {

    @NonNull
    private String login;
    @NonNull
    private String password;
}
