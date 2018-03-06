package com.paizo.balance.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Define the batch size for camel routes during aggregation
 */
public class BatchSizePredicate implements Predicate {

    public int size;

    public BatchSizePredicate(int size) {
        this.size = size;
    }
    
    @Override
    public boolean matches(Exchange exchange) {
        if (exchange != null) {
            List<Object> list = exchange.getIn().getBody(ArrayList.class);
            if (!CollectionUtils.isEmpty(list) && list.size() == size) {
                return true;
            }
        }
        return false;
    }

}