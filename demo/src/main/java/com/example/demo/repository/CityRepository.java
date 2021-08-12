package com.example.demo.repository;

import com.example.demo.entity.City1;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CityRepository extends CrudRepository<City1, Long> {
    Optional<City1> findByName(String name);
}
