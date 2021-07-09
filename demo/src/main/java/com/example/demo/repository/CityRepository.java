package com.example.demo.repository;

import com.example.demo.entity.City;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CityRepository extends CrudRepository<City, Long> {
    Optional<City> findByName(String name);
}
