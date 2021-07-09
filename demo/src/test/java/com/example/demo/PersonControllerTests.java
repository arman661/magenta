package com.example.demo;

import com.example.demo.repository.CityRepository;
import com.example.demo.repository.DistanceRepository;
import com.example.demo.response.CalculationResponse;
import com.example.demo.response.CityResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CityDistanceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class PersonControllerTests {

    @Autowired
    private MockMvc mockMvc;  //запросы к контроллерам

    private ObjectMapper objectMapper = new ObjectMapper();  //считывает строки и получить объект

    @Autowired
    private DistanceRepository distanceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void testGetCities() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/rest/city")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<CityResponse> cities = objectMapper.readValue(contentAsString, new TypeReference<List<CityResponse>>() {
        });

        assertTrue(cities.size() > 0);
        CityResponse city = cities.get(0);
        assertEquals("Samara", city.getName());
        assertTrue(null != city.getId());
    }

    @Test
    void testCalculateDistance() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/rest/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("calculationType", "ALL")
                        .param("fromCities", "Samara")
                        .param("toCities", "Togliatti")
        ).andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<CalculationResponse> calculationResponses = objectMapper.readValue(contentAsString, new TypeReference<List<CalculationResponse>>() {
        });

        assertEquals(2, calculationResponses.size());
        CalculationResponse calculationResponse = calculationResponses.get(0);
        assertEquals("Samara", calculationResponse.getFromCity());
        assertEquals("Togliatti", calculationResponse.getToCity());
        assertEquals((Double) 100.0, calculationResponse.getDistance());
        assertEquals(CalculationType.DISTANCE_MATRIX, calculationResponse.getCalculationType());
    }

    @Test
    void testCalculateDistance2() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/rest/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("calculationType", "CROWFLIGHT")
                        .param("fromCities", "Samara, Saratov, Volgograd")
                        .param("toCities", "Togliatti, Samara")
        ).andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<CalculationResponse> calculationResponses = objectMapper.readValue(contentAsString, new TypeReference<List<CalculationResponse>>() {
        });

        assertEquals(6, calculationResponses.size());
        Optional<CalculationResponse> fromSamaraToSaratov = calculationResponses.stream()
                .filter(cr -> cr.getFromCity().equals("Saratov") && cr.getToCity().equals("Samara"))
                .findFirst();
        Double distance = fromSamaraToSaratov.get().getDistance();
        assertTrue(distance < 337);
        assertTrue(distance > 336);
    }

    @Test
    public void testCreateCitiesAndDistances() throws Exception {
        long distanceCount = distanceRepository.count();
        long citiesCount = cityRepository.count();
        mockMvc.perform(
                post("/rest/distance")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<CityAndDistanceCreationRequest>\n" +
                                "    <cityCreationRequests>\n" +
                                "        <CityCreationRequest>\n" +
                                "            <name>Moscow</name>\n" +
                                "            <latitude>42.51</latitude>\n" +
                                "            <longitude>63.12</longitude>\n" +
                                "        </CityCreationRequest>\n" +
                                "    </cityCreationRequests>\n" +
                                "\n" +
                                "    <distanceCreationRequests>\n" +
                                "        <DistanceCreationRequest>\n" +
                                "            <fromCity>Saratov</fromCity>\n" +
                                "            <toCity>Volgograd</toCity>\n" +
                                "            <distance>565</distance>\n" +
                                "        </DistanceCreationRequest>\n" +
                                "        \n" +
                                "        <DistanceCreationRequest>\n" +
                                "            <fromCity>Samara</fromCity>\n" +
                                "            <toCity>Volgograd</toCity>\n" +
                                "            <distance>656</distance>\n" +
                                "        </DistanceCreationRequest>\n" +
                                "    </distanceCreationRequests>\n" +
                                "</CityAndDistanceCreationRequest>")
        ).andExpect(status().isOk()).andReturn();
        assertTrue(distanceCount + 2 == distanceRepository.count());
        assertTrue(citiesCount + 1 == cityRepository.count());
    }
}
