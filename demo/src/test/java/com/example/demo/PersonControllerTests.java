package com.example.demo;

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

        assertEquals(5, cities.size());
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
    void testCreateDistances() throws Exception {
        long count = distanceRepository.count();
        mockMvc.perform(
                post("/rest/distance")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("\n" +
                                "<ArrayOfDistanceCreationRequest xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.datacontract.org/2004/07/RestDataAccess\">\n" +
                                "\n" +
                                "<DistanceCreationRequest>\n" +
                                "<fromCity>Saratov</fromCity>\n" +
                                "<toCity>Volgograd</toCity>\n" +
                                "<distance>565</distance>\n" +
                                "</DistanceCreationRequest>\n" +
                                "\n" +
                                "<DistanceCreationRequest>\n" +
                                "<fromCity>Samara</fromCity>\n" +
                                "<toCity>Volgograd</toCity>\n" +
                                "<distance>656</distance>\n" +
                                "</DistanceCreationRequest>\n" +
                                "\n" +
                                "\n" +
                                "</ArrayOfDistanceCreationRequest>")
        ).andExpect(status().isOk()).andReturn();
        assertTrue(count + 2 == distanceRepository.count());
    }
}
