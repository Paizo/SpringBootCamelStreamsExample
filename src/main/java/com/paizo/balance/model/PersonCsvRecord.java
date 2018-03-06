package com.paizo.balance.model;

import lombok.Data;
import lombok.ToString;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Intermediate model used during conversion from CSV to person entity
 */
@Component
@CsvRecord(separator=",", skipFirstLine=true)
@Data
@ToString
public class PersonCsvRecord {

    @DataField(pos=1, required=true, trim = true)
    private String fullName;

    @DataField(pos=2, required=true, trim = true)
    private String address;

    @DataField(pos=3, required=true, trim = true)
    private String postcode;

    @DataField(pos=4, required=true, trim = true)
    private String phone;

    @DataField(pos=5, required=true, trim = true)
    private String balance;

    @DataField(pos=6, required=true, trim = true, pattern = "dd/MM/yyyy")
    private LocalDate birthday;

}