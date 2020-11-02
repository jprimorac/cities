package com.porsche.digital.cities.controller;

import com.porsche.digital.cities.model.User;
import com.porsche.digital.cities.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
@Validated
@RestController
@RequestMapping("/api/v1/register")
public class RegisterController
{
    private final UserService userService;

    public RegisterController(UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @Valid
            @RequestBody
                    User user
    )
    {
        userService.createNewUser(user);
    }

    @PostMapping("/validateEmail")
    Boolean emailExists(
            @RequestParam
                    String email
    )
    {
        return userService.emailExists(email);
    }

}
