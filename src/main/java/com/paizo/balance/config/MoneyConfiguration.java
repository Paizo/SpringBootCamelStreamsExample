package com.paizo.balance.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.RoundingMode;

/**
 * Application configuration from properties related to the balance field;
 * define the scale and rounding to be used in money related operation
 */
@Configuration
@ConfigurationProperties(prefix="paizo.balance.money")
@Data
@ToString
public class MoneyConfiguration {
    private int scale;
    private RoundingMode roundingMode;
}
