package com.porsche.digital.cities.errors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
public class CitiesAccessDeniedHandler implements AccessDeniedHandler
{
    @Override
    public void handle(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            AccessDeniedException e
    )
    {
        throw e;
    }
}
