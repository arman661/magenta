package com.example.demo.dto;

import lombok.Data;

@Data
public class Crs {
    private String type;
    private Properties properties;

    public Crs() {
        type = "name";
        properties = new Properties();
    }
}
