package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;

@Data
@AllArgsConstructor
public class CalculationResponse {
    private CalculationType calculationType;
    private String fromCity;
    private String toCity;
    private Double distance;
}
