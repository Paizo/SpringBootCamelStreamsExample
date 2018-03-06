package com.paizo.balance.router;

import com.paizo.balance.config.CamelConfiguration;
import com.paizo.balance.model.PersonCsvRecord;
import com.paizo.balance.predicate.BatchSizePredicate;
import com.paizo.balance.processor.CSVRecordToPersonProcessor;
import com.paizo.balance.processor.FixedLengthStreamProcessor;
import com.paizo.balance.service.PersonService;
import com.paizo.balance.service.impl.UtilityServiceImpl;
import com.paizo.balance.strategy.ArrayListAggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Define all the Camel routes in the application
 */
@Component
public class ApplicationRoutesBuilder extends RouteBuilder {

    @Autowired
    private CSVRecordToPersonProcessor csvMapper;

    @Autowired
    private FixedLengthStreamProcessor prnMapper;

    @Autowired
    private PersonService personService;

    @Autowired
    private CamelConfiguration cfg;

    @Override
    public void configure() {
        BindyCsvDataFormat bindyCsvDataFormat = new BindyCsvDataFormat(PersonCsvRecord.class);
        bindyCsvDataFormat.setLocale("default");

        //CSV streaming route to DB
        from("direct:personCSVStream")
            .log("Started importing balances from CSV stream")
            .transacted()
            /* removing charset to prevent double transformation
               see bug https://issues.apache.org/jira/browse/CAMEL-10053
            */
            .removeProperty(Exchange.CHARSET_NAME)
            .unmarshal(bindyCsvDataFormat)
            .split(body())
            .streaming()
            .bean(csvMapper, "personCSVRecord2Person")
            .aggregate(constant(true), new ArrayListAggregationStrategy())
            .completionPredicate(new BatchSizePredicate(cfg.getBatch().getMaxRecords()))
            .completionTimeout(cfg.getBatch().getBatchTimeout())
            .bean(personService, "saveAll")
            .log("CSV import completed")
            .end();

        //FixedLength PRN streaming route to DB
        from("direct:personPRNStream")
            .log("Started importing balances from PRN stream")
            .transacted()
            /* removing charset to prevent double transformation
               see bug https://issues.apache.org/jira/browse/CAMEL-10053
            */
            .removeProperty(Exchange.CHARSET_NAME)
            .unmarshal(prnMapper)
            .bean(personService, "saveAll")
            .log("PRN import completed")
            .end();

        //CSV file route to stream
        from(buildCsvFileUrl())
            .autoStartup(cfg.getFile().getCsv().isEnable())
            .log("reading CSV file from directory")
            .transacted()
            .to("direct:personCSVStream")
            .log("CSV file import completed")
            .end();

        //FixedLength PRN file route to stream
        from(buildPrnFileUrl())
            .autoStartup(cfg.getFile().getPrn().isEnable())
            .log("reading PRN file from directory")
            .transacted()
            .to("direct:personPRNStream")
            .log("PRN file import completed")
            .end();

    }

    /**
     * Generate the CSV File url for the Camel's route according to the application configuration
     * @return The CSV file url to be used in the Camel's route
     */
    private String buildCsvFileUrl(){
        return new StringBuilder("file:")
            .append(cfg.getFile().getCsv().getDir())
            .append("?noop=")
            .append(cfg.getFile().getCsv().isNoop())
            .append("&recursive=")
            .append(cfg.getFile().getCsv().isRecursive())
            .append("&include=")
            .append(cfg.getFile().getCsv().getType())
            .append("&delay=")
            .append(cfg.getFile().getCsv().getDelay())
            .append("&charset=")
            .append(UtilityServiceImpl.DEFAULT_INPUT_CHARSET)
            .toString();
    }

    /**
     * Generate the FixedLength PRN File url for the Camel's route according to the application configuration
     * @return The FixedLength PRN file url to be used in the Camel's route
     */
    private String buildPrnFileUrl(){
        return new StringBuilder("file:")
            .append(cfg.getFile().getPrn().getDir())
            .append("?noop=")
            .append(cfg.getFile().getPrn().isNoop())
            .append("&recursive=")
            .append(cfg.getFile().getPrn().isRecursive())
            .append("&include=")
            .append(cfg.getFile().getPrn().getType())
            .append("&delay=")
            .append(cfg.getFile().getPrn().getDelay())
            .append("&charset=")
            .append(UtilityServiceImpl.DEFAULT_INPUT_CHARSET)
            .toString();
    }
}