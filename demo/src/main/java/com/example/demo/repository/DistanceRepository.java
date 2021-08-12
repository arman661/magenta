package com.example.demo.repository;


import com.example.demo.entity.Distance1;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DistanceRepository extends CrudRepository<Distance1, Long> {
    Optional<Distance1> findByFromCityIdAndToCityId(Long id, Long id1);
}
