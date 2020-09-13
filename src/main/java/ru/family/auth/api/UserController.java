package ru.family.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.family.auth.services.AuthenticationService;

import java.math.BigInteger;

@RestController
@RequestMapping("auth/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    @GetMapping
    public BigInteger getUserIdByLogin(@RequestParam String login) {
        return authenticationService.getUserIdByLogin(login);
    }
}
