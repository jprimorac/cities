package com.porsche.digital.cities.repository;

import com.porsche.digital.cities.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
public interface UserRepository extends MongoRepository<User, String>
{
    Optional<User> findByEmail(String email);

    Page<User> findByEmailContains(String email, Pageable pageable);

    Page<User> findAllByEmail(String email, Pageable pageable);

    Page<User> findAllByEmailContainsAndEmail(String email, String auth, Pageable pageable);

    Boolean existsByEmail(String email);
}
