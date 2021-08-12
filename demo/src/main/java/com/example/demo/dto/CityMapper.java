package com.example.demo.dto;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CityMapper implements RowMapper<CityFromAreasResponse> {
    @Override
    public CityFromAreasResponse mapRow(ResultSet rs, int i) throws SQLException {
        CityFromAreasResponse city = new CityFromAreasResponse();
        city.setName(rs.getString("polygon"));
        return city;
    }
}
