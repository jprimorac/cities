package com.porsche.digital.cities.repository;

import com.porsche.digital.cities.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */

public interface CityRepository extends MongoRepository<City, String>
{
    List<City> findByName(
            @Param("name")
                    String name
    );

    Page<City> findByFavourites(String id, Pageable pageable);

    Page<City> findByOrderByFavouritesCountDesc(Pageable pageable);

}
