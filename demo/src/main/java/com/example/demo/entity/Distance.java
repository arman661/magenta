package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Distance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "from_city")
    private long fromCityId;
    @Column(name = "to_city")
    private long toCityId;
    private Double distance;
}
