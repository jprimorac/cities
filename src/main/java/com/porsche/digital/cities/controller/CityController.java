package com.porsche.digital.cities.controller;

import com.porsche.digital.cities.errors.EntityExistsException;
import com.porsche.digital.cities.errors.EntityNotFoundException;
import com.porsche.digital.cities.model.City;
import com.porsche.digital.cities.model.User;
import com.porsche.digital.cities.repository.CityRepository;
import com.porsche.digital.cities.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
@RestController
@RequestMapping("/api/v1/cities")
public class CityController
{
    CityRepository cityRepository;
    UserRepository userRepository;

    public CityController(CityRepository cityRepository, UserRepository userRepository)
    {
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
    }

    private void addToFavourites(City city, User user)
    {
        city.getFavourites().add(user.getId());
        city.setFavouritesCount(city.getFavourites().size());
        cityRepository.save(city);
    }

    private void removeFromFavourites(City city, User user)
    {
        city.getFavourites().remove(user.getId());
        city.setFavouritesCount(city.getFavourites().size());
        cityRepository.save(city);
    }

    private User getUser(OAuth2Authentication authentication)
    {
        String username = (String) authentication.getUserAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByEmail(username);
        return optionalUser.orElseThrow(() -> new EntityNotFoundException(User.class, username));
    }

    @GetMapping()
    Page<City> all(
            Pageable pageable,
            @RequestParam(value = "sort", required = false)
                    String sort
    )
    {
        if (!StringUtils.isEmpty(sort) && sort.equals("favourites"))
        {
            return cityRepository.findByOrderByFavouritesCountDesc(pageable);
        }
        return cityRepository.findAll(pageable);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    void create(
            @Valid
            @RequestBody
                    City city
    )
    {
        boolean cityExists = !cityRepository.findByName(city.getName()).isEmpty();
        if (cityExists)
        {
            throw new EntityExistsException(City.class, city.getName());
        }
        cityRepository.save(city);
    }

    @GetMapping("/favourites")
    @PreAuthorize("hasAuthority('USER')")
    Page<City> favourites(Pageable pageable, OAuth2Authentication authentication)
    {
        String username = (String) authentication.getUserAuthentication().getPrincipal();
        User user = userRepository.findByEmail(username).get();
        return cityRepository.findByFavourites(user.getId(), pageable);
    }

    @PutMapping("/favourites/{id}")
    @PreAuthorize("hasAuthority('USER')")
    void addToFavourites(
            @PathVariable
                    String id, OAuth2Authentication authentication
    )
    {
        User user = getUser(authentication);
        Optional<City> optionalCity = cityRepository.findById(id);
        City city = optionalCity.orElseThrow(() -> new EntityNotFoundException(City.class, id));
        addToFavourites(city, user);

    }

    @DeleteMapping("/favourites/{id}")
    @PreAuthorize("hasAuthority('USER')")
    void removeFromFavourites(
            @PathVariable
                    String id, OAuth2Authentication authentication
    )
    {
        User user = getUser(authentication);
        Optional<City> optionalCity = cityRepository.findById(id);
        City city = optionalCity.orElseThrow(() -> new EntityNotFoundException(City.class, id));
        removeFromFavourites(city, user);
    }
}
