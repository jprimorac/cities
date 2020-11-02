package com.porsche.digital.cities.model;

import org.springframework.data.annotation.Id;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
public class User
{
    @Id
    private String id;
    private String email;
    private String password;

    public User(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public enum Role
    {
        USER
    }

    public String getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Role getRole()
    {
        return Role.USER;
    }
}
