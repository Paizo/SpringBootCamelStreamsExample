package com.paizo.balance.integration.resource;

import com.paizo.balance.BalanceApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BalanceApplication.class)
@ActiveProfiles("test")
public class PersonResourceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public final void initMockMvc() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void verifyGetPersonListRespond() throws Exception {
        mockMvc.perform(
                get("/api/persons")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
    }

    @Test
    public void verifyCreatePersonSucceed() throws Exception {
        String personJson = "{\"firstName\" : \"John\",\n" +
                "\"lastName\" : \"Johnson\",\n" +
                "\"address\" : \"Voorstraat 32\",\n" +
                "\"postcode\" : \"3122gg\",\n" +
                "\"phone\" : \"020 3849381\",\n" +
                "\"balance\" : 1000000.00,\n" +
                "\"birthday\" : \"1987-01-01\"}";

        mockMvc.perform(
                post("/api/persons")
                .content(personJson)
                .accept("application/hal+json;charset=UTF-8")
            )
            .andExpect(status().isCreated());
    }
}
