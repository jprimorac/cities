package com.porsche.digital.cities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porsche.digital.cities.model.City;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = CitiesApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CitiesApplicationTests
{
    private static final String CLIENT_ID = "cities";
    private static final String CLIENT_SECRET = "secret";
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String EMAIL = "ayrton.senna@gmail.com";
    private static final String PASSWORD = "Brasil12$$";
    private static final String NAME = "Test City";
    private static final String DESCIPTION = "This is a test city";
    private static final int POPULATION = 123456;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                                      .addFilter(springSecurityFilterChain).build();
    }

    @ParameterizedTest
    @Order(1)
    @CsvFileSource(resources = "/users.csv", numLinesToSkip = 1)
    public void register(String email, String password) throws Exception
    {
        String registerString = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        mockMvc.perform(post("/api/v1/register")
                .contentType(CONTENT_TYPE)
                .content(registerString)
                .accept(CONTENT_TYPE))
               .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @Order(2)
    @CsvFileSource(resources = "/cities.csv", numLinesToSkip = 1)
    public void create10Cities(String name, String description, int population) throws Exception
    {
        String accessToken = obtainAccessToken(EMAIL, PASSWORD);

        String cityString = "{\"name\":\"" + name + "\",\"description\":\"" + description + "\",\"population\":" + population + "}";

        mockMvc.perform(post("/api/v1/cities")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(CONTENT_TYPE)
                .content(cityString)
                .accept(CONTENT_TYPE))
               .andExpect(status().isCreated());
    }

    @Test
    @Order(3)
    public void get10Cities() throws Exception
    {
        List<City> cities = getAllCities();

        assertThat(cities, hasSize(10));
        assertEquals("Stuttgart", cities.get(0).getName());
        assertEquals("Varazdin", cities.get(6).getName());
    }

    @ParameterizedTest
    @Order(4)
    @CsvFileSource(resources = "/users.csv", numLinesToSkip = 1)
    public void addToFavourites(String email, String password, int first, int second, int third, int fourth)
            throws Exception
    {
        String accessToken = obtainAccessToken(email, password);
        List<City> cities = getAllCities();
        City city = cities.get(first);
        addToFavourites(city.getId(), accessToken);
        city = cities.get(second);
        addToFavourites(city.getId(), accessToken);
        city = cities.get(third);
        addToFavourites(city.getId(), accessToken);
        city = cities.get(fourth);
        addToFavourites(city.getId(), accessToken);

        List<City> favouriteCities = getFavouriteCities(accessToken);
        assertThat(favouriteCities, hasSize(4));
    }

    @Test
    @Order(5)
    public void get10CitiesSortedByFavourites() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/api/v1/cities?sort=favourites")
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                                  .andExpect(status().is2xxSuccessful())
                                  .andReturn();

        List<City> cities = getCitiesFromJson(result.getResponse().getContentAsString());
        assertThat(cities, hasSize(10));
        assertEquals(3, cities.get(0).getFavouritesCount());
        assertEquals(2, cities.get(2).getFavouritesCount());
        assertEquals(0, cities.get(9).getFavouritesCount());
    }

    @ParameterizedTest
    @Order(6)
    @CsvFileSource(resources = "/removeFavourites.csv", numLinesToSkip = 1)
    public void removeFromFavourites(String email, String password, int deleteCount) throws Exception
    {
        String accessToken = obtainAccessToken(email, password);

        List<City> favouriteCities = getFavouriteCities(accessToken);
        for (int i = 0; i < deleteCount; i++)
        {
            City city = favouriteCities.get(i);
            mockMvc.perform(delete("/api/v1/cities/favourites/" + city.getId())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(CONTENT_TYPE)
                    .accept(CONTENT_TYPE))
                   .andExpect(status().is2xxSuccessful());
        }

        List<City> newFavouriteCities = getFavouriteCities(accessToken);
        assertThat(newFavouriteCities, hasSize(favouriteCities.size() - deleteCount));
    }

    private String obtainAccessToken(String username, String password) throws Exception
    {
        MvcResult userResult = mockMvc.perform(post("/api/v1/register/validateEmail?email=" + username)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                                      .andExpect(status().is2xxSuccessful())
                                      .andReturn();

        if (userResult.getResponse().getContentAsString().equals("false"))
        {
            register(username, password);
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .accept("application/json;charset=UTF-8"))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    private List<City> getFavouriteCities(String accessToken) throws Exception
    {
        MvcResult result = mockMvc.perform(get("/api/v1/cities/favourites")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                                  .andExpect(status().is2xxSuccessful())
                                  .andReturn();

        return getCitiesFromJson(result.getResponse().getContentAsString());
    }

    private void addToFavourites(String id, String accessToken) throws Exception
    {
        mockMvc.perform(put("/api/v1/cities/favourites/" + id)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
               .andExpect(status().is2xxSuccessful());
    }

    private List<City> getCitiesFromJson(String jsonString) throws JsonProcessingException
    {
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        Object content = jsonParser.parseMap(jsonString).get("content");

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(content, new TypeReference<List<City>>() {});
    }

    private List<City> getAllCities() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/api/v1/cities")
                .contentType(CONTENT_TYPE)
                .accept(CONTENT_TYPE))
                                  .andExpect(status().is2xxSuccessful())
                                  .andReturn();

        return getCitiesFromJson(result.getResponse().getContentAsString());
    }
}
