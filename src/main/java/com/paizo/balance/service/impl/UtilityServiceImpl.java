package com.paizo.balance.service.impl;

import com.paizo.balance.config.MoneyConfiguration;
import com.paizo.balance.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to provide default values and functions to the application
 */
@Service
@Scope(value = "singleton")
public class UtilityServiceImpl implements UtilityService {

    public static final Charset DEFAULT_INPUT_CHARSET = StandardCharsets.ISO_8859_1;

    @Autowired
    private MoneyConfiguration moneyCfg;

    @PostConstruct
    public void postConstruct() {
        if (moneyCfg == null) {
            throw new IllegalStateException("missing application configuration");
        }
    }

    @Override
    public BigDecimal createBigDecimalInstanceWithAppSettings(String value) {
        return new BigDecimal(value).setScale(moneyCfg.getScale(), moneyCfg.getRoundingMode());
    }
}
