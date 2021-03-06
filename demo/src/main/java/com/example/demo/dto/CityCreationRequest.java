package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityCreationRequest {
    private String name;
    private Double latitude;
    private Double longitude;
}
