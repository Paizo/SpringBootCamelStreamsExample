package com.paizo.balance.service;


import com.paizo.balance.entity.Person;

import java.util.List;

public interface PersonService {

    public void saveAll(List<Person> persons);
}
