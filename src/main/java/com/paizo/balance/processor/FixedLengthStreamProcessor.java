package com.paizo.balance.processor;

import com.paizo.balance.entity.Person;
import com.paizo.balance.exception.FixedLengthValidationException;
import com.paizo.balance.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.support.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.lang.String.format;

/**
 * Process a Fixed Length CSV input stream to a list of Person entities
 * The headers are mandatory and they are used to calculate the columns size
 */
@Component
@Slf4j
public class FixedLengthStreamProcessor extends ServiceSupport implements DataFormat {

    public enum Columns {
        NAME(0),
        ADDRESS(1),
        POSTCODE(2),
        PHONE(3),
        BALANCE(4),
        BIRTHDAY(5);

        private int value;

        Columns(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public static final String[] DEFAULT_PBR_HEADER_COLUMNS = {
            "Name",
            "Address",
            "Postcode",
            "Phone",
            "Balance",
            "Birthday"
    };

    public static final DateTimeFormatter PRN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private UtilityService utilityService;

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        throw new UnsupportedOperationException("PRN generation");
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        return prnInputStream2Person(stream, DEFAULT_PBR_HEADER_COLUMNS);
    }

    @Override
    protected void doStart() throws Exception {
        //noop
    }

    @Override
    protected void doStop() throws Exception {
        //noop
    }

    /**
     * Read the stream line by line creating instances of Person entity
     * If a given record in the stream is invalid a FixedLengthValidationException is thrown
     * validity checks:
     *          -all lines must have the same length
     *          -fullname column must contain [,] to separate surname and name
     *          -dates must be in the format [yyyyMMdd]
     * @param in FixedLength PRN input stream
     * @param headerColumnsNames The text of the headers used to define columns size
     * @return a List of Person entity
     * @throws IOException
     * @throws FixedLengthValidationException
     */
    private Object prnInputStream2Person(InputStream in, String... headerColumnsNames) throws IOException, FixedLengthValidationException {
        log.debug("Started reading PRN stream");

        try (   InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(inputStreamReader)
            ) {

            String firstLine = reader.readLine();

            if (firstLine == null) {
                throw new FixedLengthValidationException("Stream is empty");
            }

            log.trace(format("PRN header: [%s]", firstLine));

            int[] headerColumnsStartIndex = new int[headerColumnsNames.length - 1];
            detectColumnsLength(firstLine, headerColumnsStartIndex, headerColumnsNames);

            Map<Integer, String> columnsValueByPosition = new HashMap<>();
            List<Person> parsedEntities = new ArrayList<>();
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.length() != firstLine.length()) {
                    throw new FixedLengthValidationException(format("invalid content, line [%d] has length [%d], expected [%d]", lineNumber, line.length(), firstLine.length()));
                }

                int j = 0;
                for (int columnIndex = 0; columnIndex < headerColumnsStartIndex.length; columnIndex++) {
                    columnsValueByPosition.put(columnIndex, line.substring(j, headerColumnsStartIndex[columnIndex]).trim());
                    j = headerColumnsStartIndex[columnIndex];
                }
                columnsValueByPosition.put(
                        headerColumnsStartIndex.length,
                        line.substring(headerColumnsStartIndex[headerColumnsStartIndex.length - 1]).trim()
                );
                parsedEntities.add(validateAndCreateBean(columnsValueByPosition));
            }

            log.debug(format("PRN stream reading completed ([%d] lines)", lineNumber));
            return parsedEntities;
        }
    }

    /**
     * Generate a Person bean from the parsed text
     * @param resultFieldsMap column number to field value map
     * @return a Person entity
     * @throws FixedLengthValidationException
     */
    private Person validateAndCreateBean(Map<Integer, String> resultFieldsMap) throws FixedLengthValidationException {

        StringTokenizer fullNameTokenizer = new StringTokenizer(resultFieldsMap.get(Columns.NAME.getValue()), ",");
        if (fullNameTokenizer.countTokens() != 2) {
            throw new FixedLengthValidationException(format("Invalid fullName format [%s]", resultFieldsMap.get(Columns.NAME.getValue())));
        }

        try {
            return Person
                    .builder()
                    .lastName(((String) fullNameTokenizer.nextElement()).trim())
                    .firstName(((String) fullNameTokenizer.nextElement()).trim())
                    .address(resultFieldsMap.get(Columns.ADDRESS.getValue()))
                    .postcode(resultFieldsMap.get(Columns.POSTCODE.getValue()))
                    .phone(resultFieldsMap.get(Columns.PHONE.getValue()))
                    .balance(
                            utilityService.createBigDecimalInstanceWithAppSettings(
                                    resultFieldsMap.get(Columns.BALANCE.getValue())
                            )
                    )
                    .birthday(
                            LocalDate.parse(
                                    resultFieldsMap.get(Columns.BIRTHDAY.getValue()),
                                    PRN_DATE_FORMAT)
                    )
                    .build();
        } catch (DateTimeParseException dateTimeParseException) {
            throw new FixedLengthValidationException(format("Invalid date format [%s]", resultFieldsMap.get(Columns.BIRTHDAY.getValue())), dateTimeParseException);
        }
    }

    /**
     * Detect the starting index of each header column to be used for parsing the PRN file body
     * @param firstLine The first line of the FixedLength PRN file containing the headers
     * @param headerColumnsStartIndex Resulting array with the starting index for each column
     * @param headerColumnsNames The headers to be used to verify the given line
     * @throws FixedLengthValidationException
     */
    private void detectColumnsLength(String firstLine, int[] headerColumnsStartIndex, String[] headerColumnsNames) throws FixedLengthValidationException {
        for (int i = 1; i < headerColumnsNames.length; i++) {
            int currentHeaderIndex = firstLine.indexOf(headerColumnsNames[i]);
            if (currentHeaderIndex == -1) {
                throw new FixedLengthValidationException(format("Header mismatch, cannot find column [%s]", headerColumnsNames[i]));
            }
            headerColumnsStartIndex[i - 1] = currentHeaderIndex;
        }
    }

}
