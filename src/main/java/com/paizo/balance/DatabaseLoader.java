package com.paizo.balance;

import com.paizo.balance.entity.Person;
import com.paizo.balance.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ONLY FOR DEVELOPMENT
 * Initialize the database at application bootstrap
 */
//@Component
@Slf4j
public class DatabaseLoader implements CommandLineRunner {

	private final PersonRepository repository;

	@Autowired
	public DatabaseLoader(PersonRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... strings) {

	    log.info("Loading sample data");
        // @formatter:off
		this.repository.save(
                Person
                    .builder()
                    .address("an address")
                    .birthday(LocalDate.now())
                    .firstName("name")
                    .lastName("lastname")
					.phone("+316123123123")
                    .postcode("1234AS")
                    .balance(new BigDecimal("123.3"))
                    .build()
        );
		// @formatter:on
        log.info("Loading sample data completed");
	}
}
