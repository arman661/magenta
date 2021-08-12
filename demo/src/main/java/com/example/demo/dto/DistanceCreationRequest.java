package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;

@Data
@AllArgsConstructor
public class DistanceCreationRequest {
    private String fromCity;
    private String toCity;
    private Double distance;
}
