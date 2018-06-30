package com.paizo.balance.integration;

import com.paizo.balance.BalanceApplication;
import com.paizo.balance.entity.Person;
import com.paizo.balance.repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BalanceApplication.class)
@WebAppConfiguration
public class FileUploadTest {

    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() {
        personRepository.deleteAll();
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void csvUploadShouldSuccessfullyStorePersonsInDb() throws Exception {
        InputStream inputStream = FileUploadTest.class.getClassLoader().getResourceAsStream("Sheet.csv");

        final MockMultipartFile csvFile = new MockMultipartFile("file", "Sheet.csv", "text/csv", inputStream);

        mockMvc.perform(fileUpload("/uploadCSV").file(csvFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "File upload completed [Sheet.csv]"))
                .andDo(print());

        List<Person> personList = personRepository.findAll();
        assertThat(personList).isNotNull();
        assertThat(personList.size()).isEqualTo(7);

        List<Person> personByAddress = personRepository.findByAddress("PiøveDißacco 610");
        assertThat(personByAddress.size()).isEqualTo(1);
        assertThat(personByAddress.get(0).getBalance()).isEqualTo(new BigDecimal("9999.34").setScale(2));
        assertThat(personByAddress.get(0).getBirthday()).isEqualTo(LocalDate.parse("1998-10-27"));
    }

    @Test
    public void fixedLengthUploadShouldSuccessfullyStorePersonsInDb() throws Exception {
        InputStream inputStream = FileUploadTest.class.getClassLoader().getResourceAsStream("Sheet.txt");

        final MockMultipartFile csvFile = new MockMultipartFile("file", "Sheet.txt", "application/octet-stream", inputStream);

        mockMvc.perform(fileUpload("/uploadPRN").file(csvFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uploadStatus"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "File upload completed [Sheet.txt]"))
                .andDo(print());

        List<Person> personList = personRepository.findAll();
        assertThat(personList).isNotNull();
        assertThat(personList.size()).isEqualTo(7);

        List<Person> personByAddress = personRepository.findByAddress("Javaplein 131");
        assertThat(personByAddress.size()).isEqualTo(1);
        assertThat(personByAddress.get(0).getBalance()).isEqualTo(new BigDecimal("50000").setScale(2));
        assertThat(personByAddress.get(0).getBirthday()).isEqualTo(LocalDate.parse("1944-10-01"));
    }

}