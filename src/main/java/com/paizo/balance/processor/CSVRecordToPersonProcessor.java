package com.paizo.balance.processor;

import com.paizo.balance.entity.Person;
import com.paizo.balance.exception.CSVValidationException;
import com.paizo.balance.model.PersonCsvRecord;
import com.paizo.balance.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

import static java.lang.String.format;

/**
 * Process the intermediate PersonCsvRecord bean to the Person entity
 * during CSV import
 */
@Component
@Slf4j
public class CSVRecordToPersonProcessor {

    @Autowired
    private UtilityService utilityService;

    /**
     * Transform a PersonCsvRecord to a person entity
     * if the input is invalid a CSVValidationException is thrown
     * @param csvRecord
     * @return the resulting Person bean
     * @throws CSVValidationException
     */
    public Person personCSVRecord2Person(PersonCsvRecord csvRecord) throws CSVValidationException {
        validateCSVRecord(csvRecord);

        StringTokenizer fullNameTokenizer = new StringTokenizer(csvRecord.getFullName(), ",");
        if (fullNameTokenizer.countTokens() != 2) {
            throw new CSVValidationException(format("Invalid fullName format [%s]", csvRecord.getFullName()));
        }

        Person person = Person
                .builder()
                .lastName(((String) fullNameTokenizer.nextElement()).trim())
                .firstName(((String) fullNameTokenizer.nextElement()).trim())
                .address(csvRecord.getAddress())
                .postcode(csvRecord.getPostcode())
                .phone(csvRecord.getPhone())
                .balance(utilityService.createBigDecimalInstanceWithAppSettings(csvRecord.getBalance()))
                .birthday(csvRecord.getBirthday())
                .build();

        log.info(format("converted to [%s]", person));
        return person;
    }


    /**
     * Validate a PersonCsvRecord instance
     * - fields must be not null
     * - FullName field must contain [,] separator
     * @param csvRecord
     * @throws CSVValidationException
     */
    private void validateCSVRecord(PersonCsvRecord csvRecord) throws CSVValidationException {
        if (csvRecord == null ||
                csvRecord.getAddress() == null ||
                csvRecord.getFullName() == null ||
                csvRecord.getBalance() == null ||
                csvRecord.getPhone() == null ||
                csvRecord.getPostcode() == null ||
                csvRecord.getBirthday() == null
                ) {
            throw new CSVValidationException("All fields are mandatory");
        }
        if (csvRecord.getFullName().indexOf(", ") == -1) {
            throw new CSVValidationException(format("Invalid format for full name [%s]", csvRecord.getFullName()));
        }
    }
}