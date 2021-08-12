package com.example.demo.dto;

import com.example.demo.CalculationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationResponse {
    private CalculationType calculationType;
    private String fromCity;
    private String toCity;
    private Double distance;
}
