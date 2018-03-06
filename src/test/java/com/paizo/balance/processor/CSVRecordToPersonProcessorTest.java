package com.paizo.balance.processor;

import com.paizo.balance.entity.Person;
import com.paizo.balance.exception.CSVValidationException;
import com.paizo.balance.model.PersonCsvRecord;
import com.paizo.balance.service.UtilityService;
import com.paizo.balance.service.impl.UtilityServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

/**
 * tests for {@link CSVRecordToPersonProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CSVRecordToPersonProcessorTest {

    @InjectMocks
    private CSVRecordToPersonProcessor processor = new CSVRecordToPersonProcessor();

    @Mock
    private UtilityService utilityService = new UtilityServiceImpl();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void CSVRecordToPersonProcessorShouldVerifyThatAllFieldsArePresent() throws CSVValidationException {
        //GIVEN
        PersonCsvRecord personCsvRecord = new PersonCsvRecord();

        //VERIFY
        expectedException.expect(CSVValidationException.class);
        expectedException.expectMessage("All fields are mandatory");
        expectedException.expectCause(equalTo(null));
        expectedException.reportMissingExceptionWithMessage("CSVValidationException expected");

        //DO
        processor.personCSVRecord2Person(personCsvRecord);
    }

    @Test
    public void CSVRecordToPersonProcessorShouldThrowExceptionForInvalidFullName() throws CSVValidationException {
        //GIVEN
        PersonCsvRecord personCsvRecord = getPersonCsvRecord();
        personCsvRecord.setFullName("invalid");

        //VERIFY
        expectedException.expect(CSVValidationException.class);
        expectedException.expectMessage("Invalid format for full name [invalid]");
        expectedException.expectCause(equalTo(null));
        expectedException.reportMissingExceptionWithMessage("CSVValidationException expected");

        //DO
        processor.personCSVRecord2Person(personCsvRecord);
    }

    @Test
    public void CSVRecordToPersonProcessorShouldReturnAPersonEntity() throws CSVValidationException {
        //GIVEN
        PersonCsvRecord personCsvRecord = getPersonCsvRecord();
        given(
            utilityService.createBigDecimalInstanceWithAppSettings(
                personCsvRecord.getBalance()
            )).willReturn(
                new BigDecimal(personCsvRecord.getBalance()).setScale(2)
        );

        //DO
        Person person = processor.personCSVRecord2Person(personCsvRecord);

        //VERIFY
        assertThat(person).isNotNull();
        assertThat(person.getBirthday()).isEqualByComparingTo(personCsvRecord.getBirthday());
        assertThat(person.getBalance().toString()).isEqualTo(personCsvRecord.getBalance() + ".00");
        assertThat(person.getAddress()).isEqualTo(personCsvRecord.getAddress());
        assertThat(person.getPostcode()).isEqualTo(personCsvRecord.getPostcode());
        assertThat(person.getPhone()).isEqualTo(personCsvRecord.getPhone());
        assertThat(personCsvRecord.getFullName()).contains(person.getFirstName());
        assertThat(personCsvRecord.getFullName()).contains(person.getLastName());
    }

    private PersonCsvRecord getPersonCsvRecord(){
        PersonCsvRecord personCsvRecord = new PersonCsvRecord();
        personCsvRecord.setFullName(RandomStringUtils.random(5) + ", " + RandomStringUtils.random(5));
        personCsvRecord.setAddress(RandomStringUtils.random(10));
        personCsvRecord.setBalance("1" + RandomStringUtils.random(5, false, true));
        personCsvRecord.setPostcode(RandomStringUtils.random(5));
        personCsvRecord.setPhone(RandomStringUtils.random(10));
        personCsvRecord.setBirthday(LocalDate.now());
        return personCsvRecord;
    }
}