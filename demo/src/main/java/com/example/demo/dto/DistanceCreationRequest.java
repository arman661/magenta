package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistanceCreationRequest {
    private String fromCity;
    private String toCity;
    private Double distance;
}
