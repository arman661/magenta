package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.awt.*;

@Entity
@Data
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Lob
    private byte[] polygon;

    public Area(String name, byte[] polygon) {
        this.name = name;
        this.polygon = polygon;
    }

    public Area() {

    }

}
