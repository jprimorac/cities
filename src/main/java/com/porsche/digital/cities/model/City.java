package com.porsche.digital.cities.model;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
public class City
{
    @Id
    private String id;

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Min(1)
    private int population;
    private Set<String> favourites = new HashSet<>();
    private int favouritesCount;

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getPopulation()
    {
        return population;
    }

    public void setPopulation(int population)
    {
        this.population = population;
    }

    public Set<String> getFavourites()
    {
        return favourites;
    }

    public void setFavourites(Set<String> favourites)
    {
        this.favourites = favourites;
    }

    public int getFavouritesCount()
    {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount)
    {
        this.favouritesCount = favouritesCount;
    }
}
