package com.example.demo.service;

import com.example.demo.*;
import com.example.demo.dto.*;
import com.example.demo.entity.Area;
import com.example.demo.entity.City;
import com.example.demo.entity.Distance;
import com.example.demo.repository.AreaRepository;
import com.example.demo.repository.AreaRepositoryClass;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.DistanceRepository;
import com.example.demo.utils.HaversineCalculation;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.demo.CalculationType.*;

@Service
@Transactional
public class CityDistanceService {
    private final CityRepository cityRepository;
    private final DistanceRepository distanceRepository;
    private final AreaRepository areaRepository;
    private final AreaRepositoryClass areaRepositoryClass;
    private final Gson gson;

    public CityDistanceService(CityRepository cityRepository, DistanceRepository distanceRepository, AreaRepository areaRepository, AreaRepositoryClass areaRepositoryClass, Gson gson) {
        this.cityRepository = cityRepository;
        this.distanceRepository = distanceRepository;
        this.areaRepository = areaRepository;
        this.areaRepositoryClass = areaRepositoryClass;
        this.gson = gson;
    }


    public List<CityResponse> getAllCities() {
        return StreamSupport
                .stream(cityRepository.findAll().spliterator(), false)
                .map(city -> new CityResponse(city.getId(), city.getName()))
                .collect(Collectors.toList());
    }

    public List<CalculationResponse> calculateDistance(CalculationType calculationType, List<String> fromCitiesNames
            , List<String> toCitiesName) throws CannotBeCalculatedException {
        List<CalculationResponse> result = new ArrayList<>();
        List<City> fromCitiesList = findCities(fromCitiesNames);
        List<City> toCitiesList = findCities(toCitiesName);

        for (City from : fromCitiesList) {
            for (City to : toCitiesList) {
                if (calculationType == DISTANCE_MATRIX || calculationType == ALL) {
                    result.add(calculateViaDistanceTable(from, to));
                }
                if (calculationType == CROWFLIGHT || calculationType == ALL) {
                    result.add(calculateHaversineFunction(from, to));
                }
            }
        }

        return result;
    }

    private CalculationResponse calculateHaversineFunction(City from, City to) {
        return new CalculationResponse(
                CROWFLIGHT,
                from.getName(),
                to.getName(),
                HaversineCalculation.calculateDistance(from, to));
    }

    private CalculationResponse calculateViaDistanceTable(City from, City to) throws CannotBeCalculatedException {
        Optional<Distance> distanceOptional = distanceRepository.findByFromCityIdAndToCityId(from.getId(), to.getId());
        if (!distanceOptional.isPresent()) {
            throw new CannotBeCalculatedException("Not found distance for fromCity " + from.getName() + " and toCity " + to.getName());
        }
        return new CalculationResponse(
                DISTANCE_MATRIX,
                from.getName(),
                to.getName(),
                distanceOptional.get().getDistance());
    }

    private List<City> findCities(List<String> fromCities) throws CannotBeCalculatedException {
        List<City> resultList = new ArrayList<>();
        for (String fromCity : fromCities) {
            Optional<City> byName = cityRepository.findByName(fromCity);
            if (byName.isPresent()) {
                resultList.add(byName.get());
            } else {
                throw new CannotBeCalculatedException(fromCity);
            }
        }
        return resultList;
    }

    public Optional<Long> createDistance(DistanceCreationRequest distanceCreationRequest) {
        Distance distance = new Distance();
        Optional<City> fromCityOptional = cityRepository.findByName(distanceCreationRequest.getFromCity());
        if (fromCityOptional.isPresent()) {
            distance.setFromCityId(fromCityOptional.get().getId());
        } else {
            return Optional.empty();
        }
        Optional<City> toCityOptional = cityRepository.findByName(distanceCreationRequest.getToCity());
        if (toCityOptional.isPresent()) {
            distance.setToCityId(toCityOptional.get().getId());
        } else {
            return Optional.empty();
        }

        distance.setDistance(distanceCreationRequest.getDistance());

        Distance save = distanceRepository.save(distance);
        return Optional.ofNullable(save.getId());
    }


    public void createCitiesAndDistancesList(CityAndDistanceCreationRequest cityAndDistanceCreationRequest) {
        for (CityCreationRequest cityCreationRequest : cityAndDistanceCreationRequest.getCityCreationRequests()) {
            City city = new City(
                    cityCreationRequest.getName(),
                    cityCreationRequest.getLatitude(),
                    cityCreationRequest.getLongitude()
            );
            cityRepository.save(city);
        }
        for (DistanceCreationRequest distanceCreationRequest : cityAndDistanceCreationRequest.getDistanceCreationRequests()) {
            createDistance(distanceCreationRequest);
        }
    }
//    public ResponseEntity createCity(CityCreationRequest cityCreationRequest) {
//        Point point = new Point();
//        point.addCityPoint(cityCreationRequest.getLongitude(), cityCreationRequest.getLatitude());
//
//        Gson gson = new Gson();
//        String jsonString = gson.toJson(point);
//        jdbcTemplate.update("INSERT INTO city (name, point) VALUES (?, ST_GeomFromGeoJSON(?));", cityCreationRequest.getName(), jsonString);
//        jdbcTemplate.update("INSERT INTO POLYGONS_CONTAINS_CITY(city, polygon)\n" +
//                "\tSELECT a.name, b.name \n" +
//                "  FROM city a, area b\n" +
//                "  WHERE a.name=? AND ST_Within( a.point, b.polygon)=1;", cityCreationRequest.getName());
//        return new ResponseEntity(HttpStatus.OK);
//    }

    public ResponseEntity createArea(AreaCreationRequest areaCreationRequest) {
        Polygon polygon = new Polygon();
        for (GeoPoint point : areaCreationRequest.getPoints()) {
            polygon.addPoint(point.getLongitude(), point.getLatitude());
        }
        String jsonString = gson.toJson(polygon);
        areaRepositoryClass.createArea(areaCreationRequest, jsonString);
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity<Area> deleteArea(Long id) {
        if (!areaRepository.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        areaRepositoryClass.deleteArea(id);
        return new ResponseEntity<Area>(HttpStatus.OK);
    }

    public ResponseEntity<Area> updateArea(Long id, AreaUpdateRequest areaUpdateRequest) {
        if (!areaRepository.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Polygon polygon = new Polygon();
        for (GeoPoint point : areaUpdateRequest.getPoints()) {
            polygon.addPoint(point.getLongitude(), point.getLatitude());
        }

        String jsonString = gson.toJson(polygon);
        areaRepositoryClass.updateArea(areaUpdateRequest, jsonString, id);
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity<List<CityFromAreasResponse>> getAreasContainsArea(String city) {
        return ResponseEntity.ok(areaRepositoryClass.getAreasContainsArea(city));

    }


}
