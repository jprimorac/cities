package com.porsche.digital.cities.repository;

import com.porsche.digital.cities.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
@Repository
public class CustomCityRepository
{
    private final MongoTemplate mongoTemplate;

    public CustomCityRepository(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<City> findAllSortedByFavourites(Pageable pageable)
    {
/*
        final Aggregation agg = newAggregation(
                unwind("favourites"),
                group("name", "description", "population").sum(ArrayOperators.Size.lengthOfArray("favourites")).as("count"),
                sort(DESC, "count")
                //skip(pageable.getPageNumber() * pageable.getPageSize()),
                //limit(pageable.getPageSize())
        );
*/

        final Aggregation agg = newAggregation(
                unwind("favourites"),
                group("_id").push("favourites").as("favourites").sum("1").as("count")
                            .first("name").as("name")
                            .first("description").as("description")
                            .first("population").as("population")
                ,
                sort(DESC, "count"),
                skip(pageable.getPageNumber() * pageable.getPageSize()),
                limit(pageable.getPageSize())
        );

        final List<City> results = mongoTemplate
                .aggregate(agg, "city", City.class)
                .getMappedResults();

        return new PageImpl<>(results, pageable, results.size());
    }

}
