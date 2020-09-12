package ru.family.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.family.auth.dto.UserCredentialsDTO;
import ru.family.auth.services.AuthenticationService;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public void authenticate(@RequestBody @Validated UserCredentialsDTO userCredentialsDTO) {
        authenticationService.authenticate(userCredentialsDTO);
    }

}
