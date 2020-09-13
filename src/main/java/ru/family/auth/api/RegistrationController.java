package ru.family.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.family.auth.domain.LoginEnum;
import ru.family.auth.domain.PublicUser;
import ru.family.auth.dto.UserRegistrationDTO;
import ru.family.auth.dto.Verify2FaDTO;
import ru.family.auth.services.RegistrationService;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    @ResponseStatus(value = HttpStatus.CREATED)
    public PublicUser signUp(@RequestBody @Validated UserRegistrationDTO userRegistrationDTO) {
        return registrationService.register(userRegistrationDTO);
    }

    @PostMapping("/verify2fa")
    @ResponseStatus(value = HttpStatus.OK)
    public LoginEnum verify2fa(@RequestBody @Validated Verify2FaDTO dto) {
        return registrationService.validate2Fa(dto);
    }


    @GetMapping("/resend2fa")
    @ResponseStatus(value = HttpStatus.OK)
    public void resend2fa(@RequestParam @NotEmpty String mail) {
        registrationService.resend2Fa(mail);
    }


}
