package com.example.demo;


import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DistanceRepository extends CrudRepository<Distance, Long> {
    Optional<Distance> findByFromCityIdAndToCityId(Long id, Long id1);
}
