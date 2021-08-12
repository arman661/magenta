package com.example.demo.dto;

import lombok.Data;

@Data
public class Properties {
    private String name;

    public Properties() {
        name = "EPSG:4326";
    }
}
