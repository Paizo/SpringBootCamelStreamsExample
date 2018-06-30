package com.paizo.balance.integration;

import com.paizo.balance.BalanceApplication;
import com.paizo.balance.entity.Person;
import com.paizo.balance.repository.PersonRepository;
import com.paizo.balance.service.impl.UtilityServiceImpl;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BalanceApplication.class)
@ActiveProfiles("test")
public class CamelTest {

    @Autowired
    private PersonRepository personRepository;

    @Produce(uri = "direct:personCSVStream")
    protected ProducerTemplate template;

    @Test
    public void sendMessageShouldSucceed() {
        //GIVEN
        InputStream inputStream = CamelTest.class.getClassLoader().getResourceAsStream("Sheet.csv");
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, UtilityServiceImpl.DEFAULT_INPUT_CHARSET));

        //DO
        template.sendBody(in);

        //VERIFY
        List<Person> personList = personRepository.findAll();
        assertThat(personList).isNotNull();
        assertThat(personList.size()).isEqualTo(7);

        List<Person> personByAddress = personRepository.findByAddress("PiøveDißacco 610");
        assertThat(personByAddress.size()).isEqualTo(1);
        assertThat(personByAddress.get(0).getBalance()).isEqualTo(new BigDecimal("9999.34").setScale(2));
        assertThat(personByAddress.get(0).getBirthday()).isEqualTo(LocalDate.parse("1998-10-27"));
    }
}
