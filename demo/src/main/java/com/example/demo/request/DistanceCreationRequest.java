package com.example.demo.request;

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
