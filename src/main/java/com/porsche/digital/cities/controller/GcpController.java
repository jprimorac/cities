package com.porsche.digital.cities.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
@RestController
@RequestMapping("/_ah/start")
public class GcpController
{

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public void healthcheck()
    {

    }
}
