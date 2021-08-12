package com.example.demo.repository;

import com.example.demo.dto.AreaCreationRequest;
import com.example.demo.dto.AreaUpdateRequest;
import com.example.demo.dto.CityFromAreasResponse;
import com.example.demo.dto.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Types;
import java.util.List;

@Repository
@Transactional
public class AreaRepositoryClass {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createArea(AreaCreationRequest areaCreationRequest, String jsonString) {
        entityManager.createNativeQuery("INSERT INTO area (name, polygon) VALUES (?1, ST_GeomFromGeoJSON(?2))")
                .setParameter(1, areaCreationRequest.getName())
                .setParameter(2, jsonString)
                .executeUpdate();
        String point = "POINT(city.latitude city.longitude)";
        entityManager.createNativeQuery("INSERT INTO POLYGONS_CONTAINS_CITY(city, polygon) SELECT a.name, b.name FROM city a, area b WHERE b.name = ?1 AND ST_Within( ST_GeomFromText(?2, 4326),  b.polygon)=1;")
                .setParameter(1, areaCreationRequest.getName())
                .setParameter(2, point)
                .executeUpdate();
    }

    public void deleteArea(Long id) {
        entityManager.createNativeQuery("DELETE FROM area WHERE id = ?1")
                .setParameter(1, id)
                .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM POLYGONS_CONTAINS_CITY AS p WHERE id>0 AND p.polygon NOT IN (SELECT a.name FROM area AS a);")
                .executeUpdate();
    }

    public void updateArea(AreaUpdateRequest areaUpdateRequest, String jsonString, Long id) {
        entityManager.createNativeQuery("UPDATE area SET name = ?1, polygon = ST_GeomFromGeoJSON(?2) WHERE id=?3;")
                .setParameter(1, areaUpdateRequest.getName())
                .setParameter(2, jsonString)
                .setParameter(3, id)
                .executeUpdate();
        entityManager.createNativeQuery("INSERT INTO POLYGONS_CONTAINS_CITY(city, polygon)\n" +
                "  SELECT a.name, b.name \n" +
                "  FROM city a, area b\n" +
                "  WHERE b.name = ?1 AND ST_Within( a.point, b.polygon)=1;")
                .setParameter(1, areaUpdateRequest.getName());
        entityManager.createNativeQuery("DELETE FROM POLYGONS_CONTAINS_CITY AS p WHERE id>0 AND p.polygon NOT IN (SELECT a.name FROM area AS a);")
                .executeUpdate();
    }

    public List<CityFromAreasResponse> getAreasContainsArea(String city) {
        String sql = "SELECT polygon FROM POLYGONS_CONTAINS_CITY WHERE city = ?;";
        return jdbcTemplate.query(sql, new Object[]{city}, new int[]{Types.VARCHAR}, new CityMapper());

    }
}
