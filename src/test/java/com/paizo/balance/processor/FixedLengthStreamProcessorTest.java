package com.paizo.balance.processor;

import com.paizo.balance.entity.Person;
import com.paizo.balance.exception.FixedLengthValidationException;
import com.paizo.balance.service.UtilityService;
import com.paizo.balance.service.impl.UtilityServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

/**
 * tests for {@link FixedLengthStreamProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class FixedLengthStreamProcessorTest {

    private static final String SMALLER_SIZE_ROW =
            "Name                Address              Postcode Phone          Balance Birthday\n" +
            "Egg, Benedict    Waterlooplein 67     3211 AB  06-90367362    123     06071966";

    private static final String BIGGER_SIZE_ROW =
            "Name                Address              Postcode Phone          Balance Birthday\n" +
            "Egg, Benedict       Waterlooplein 67     3211 AB  06-90367362    123          19660706";

    private static final String INVALID_FULL_NAME_FORMAT =
            "Name                Address              Postcode Phone          Balance Birthday\n" +
            "Egg  Benedict       Waterlooplein 67     3211 AB  06-90367362    123     19660706";

    private static final String INVALID_DATE_FORMAT =
            "Name                Address              Postcode Phone          Balance Birthday  \n" +
            "Egg, Benedict       Waterlooplein 67     3211 AB  06-90367362    123     1966/01/01";

    private static final String INVALID_HEADER =
            "Name                Address              Postcode Phone          wrong header Birthday\n" +
            "Egg, Benedict       Waterlooplein 67     3211 AB  06-90367362    123          19660706";

    private static final String VALID_ROW =
            "Name                Address              Postcode Phone          Balance Birthday\n" +
            "Egg, Benedict       Waterlooplein 67     3211 AB  06-90367362    123     19660706";

    @InjectMocks
    private FixedLengthStreamProcessor processor = new FixedLengthStreamProcessor();

    @Mock
    private UtilityService utilityService = new UtilityServiceImpl();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void processorShouldThrowPRNValidationExceptionForInvalidSmallerRow() throws Exception {
        //GIVEN
        InputStream inputStream = IOUtils.toInputStream(SMALLER_SIZE_ROW, Charset.forName("ISO_8859_1"));

        //VERIFY
        expectedException.expect(FixedLengthValidationException.class);
        expectedException.expectMessage("invalid content, line [2] has length [78], expected [81]");
        expectedException.expectCause(equalTo(null));
        expectedException.reportMissingExceptionWithMessage("FixedLengthValidationException expected");

        //DO
        processor.unmarshal(null, inputStream);
    }

    @Test
    public void processorShouldThrowPRNValidationExceptionForInvalidBiggerRow() throws Exception {
        //GIVEN
        InputStream inputStream = IOUtils.toInputStream(BIGGER_SIZE_ROW, Charset.forName("ISO_8859_1"));

        //VERIFY
        expectedException.expect(FixedLengthValidationException.class);
        expectedException.expectMessage("invalid content, line [2] has length [86], expected [81]");
        expectedException.expectCause(equalTo(null));
        expectedException.reportMissingExceptionWithMessage("FixedLengthValidationException expected");

        //DO
        processor.unmarshal(null, inputStream);
    }

    @Test
    public void processorShouldThrowPRNValidationExceptionForMalformedFullName() throws Exception {
        //GIVEN
        InputStream inputStream = IOUtils.toInputStream(INVALID_FULL_NAME_FORMAT, Charset.forName("ISO_8859_1"));

        //VERIFY
        expectedException.expect(FixedLengthValidationException.class);
        expectedException.expectMessage("Invalid fullName format [Egg  Benedict]");
        expectedException.expectCause(equalTo(null));
        expectedException.reportMissingExceptionWithMessage("FixedLengthValidationException expected");

        //DO
        processor.unmarshal(null, inputStream);
    }

    @Test
    public void processorShouldThrowPRNValidationExceptionForMalformedDate() throws Exception {
        //GIVEN
        InputStream inputStream = IOUtils.toInputStream(INVALID_DATE_FORMAT, Charset.forName("ISO_8859_1"));

        //VERIFY
        expectedException.expect(FixedLengthValidationException.class);
        expectedException.expectMessage("Invalid date format [1966/01/01]");
        expectedException.expectCause(any(DateTimeParseException.class));
        expectedException.reportMissingExceptionWithMessage("FixedLengthValidationException expected");

        //DO
        processor.unmarshal(null, inputStream);
    }

    @Test
    public void processorShouldThrowPRNValidationExceptionForInvalidHeader() throws Exception {
        //GIVEN
        InputStream inputStream = IOUtils.toInputStream(INVALID_HEADER, Charset.forName("ISO_8859_1"));

        //VERIFY
        expectedException.expect(FixedLengthValidationException.class);
        expectedException.expectMessage("Header mismatch, cannot find column [Balance]");
        expectedException.expectCause(equalTo(null));
        expectedException.reportMissingExceptionWithMessage("FixedLengthValidationException expected");

        //DO
        processor.unmarshal(null, inputStream);
    }

    @Test
    public void processorShouldReturnAListOfPersonEntity() throws Exception {
        //GIVEN
        InputStream inputStream = IOUtils.toInputStream(VALID_ROW, Charset.forName("ISO_8859_1"));
        given(
            utilityService.createBigDecimalInstanceWithAppSettings(
                "123"
            )).willReturn(
            new BigDecimal("1000000").setScale(2)
        );

        //DO
        List result = (List) processor.unmarshal(null, inputStream);

        //VERIFY
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isInstanceOf(Person.class);
        Person person = (Person) result.get(0);
        assertThat(person).isNotNull();
        assertThat(person.getBirthday()).isNotNull();
        assertThat(person.getBalance()).isNotNull();
        assertThat(person.getAddress()).isNotNull();
        assertThat(person.getPostcode()).isNotNull();
        assertThat(person.getPhone()).isNotNull();
        assertThat(person.getFirstName()).isNotNull();
        assertThat(person.getLastName()).isNotNull();
        assertThat(person.getBirthday()).isEqualByComparingTo(LocalDate.parse("19660706", DateTimeFormatter.ofPattern("yyyyMMdd")));
        assertThat(person.getBalance().toString()).isEqualTo("1000000.00");
        assertThat(person.getAddress()).isEqualTo("Waterlooplein 67");
        assertThat(person.getPostcode()).isEqualTo("3211 AB");
        assertThat(person.getPhone()).isEqualTo("06-90367362");
        assertThat(person.getFirstName()).isEqualTo("Benedict");
        assertThat(person.getLastName()).isEqualTo("Egg");
    }
}
