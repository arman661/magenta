package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AreaCreationRequestList {
    private Double latitude;
    private Double longitude;
}
