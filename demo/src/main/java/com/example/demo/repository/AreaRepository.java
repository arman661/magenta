package com.example.demo.repository;

import com.example.demo.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface AreaRepository extends JpaRepository<Area, Long>, CrudRepository<Area, Long>,
        JpaSpecificationExecutor<Area> {

}
