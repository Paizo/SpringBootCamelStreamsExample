package com.paizo.balance.service.impl;

import com.paizo.balance.entity.Person;
import com.paizo.balance.repository.PersonRepository;
import com.paizo.balance.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("personService")
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    @Transactional
    public void saveAll(List<Person> persons) {
        personRepository.save(persons);
    }

}
