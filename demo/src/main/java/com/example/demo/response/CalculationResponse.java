package com.example.demo.response;

import com.example.demo.CalculationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationResponse {
    private CalculationType calculationType;
    private String fromCity;
    private String toCity;
    private Double distance;
}
