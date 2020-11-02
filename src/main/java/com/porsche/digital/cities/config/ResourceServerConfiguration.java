package com.porsche.digital.cities.config;

import com.porsche.digital.cities.errors.CitiesAccessDeniedHandler;
import com.porsche.digital.cities.errors.CitiesAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter
{
    private final CitiesAuthenticationEntryPoint citiesAuthenticationEntryPoint;

    public ResourceServerConfiguration(CitiesAuthenticationEntryPoint citiesAuthenticationEntryPoint)
    {
        this.citiesAuthenticationEntryPoint = citiesAuthenticationEntryPoint;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
    {
        resources.resourceId("api");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception
    {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/_ah/start")
                .permitAll()
                .antMatchers("/oauth/token")
                .permitAll()
                .antMatchers("/api/v1/cities")
                .permitAll()
                .antMatchers("/api/v1/register")
                .permitAll()
                .antMatchers("/api/v1/register/**")
                .permitAll()
                .antMatchers(
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger-ui/**"
                )
                .permitAll()
                .antMatchers("/api/**")
                .authenticated()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(citiesAuthenticationEntryPoint)
                .accessDeniedHandler(new CitiesAccessDeniedHandler());
    }

}
