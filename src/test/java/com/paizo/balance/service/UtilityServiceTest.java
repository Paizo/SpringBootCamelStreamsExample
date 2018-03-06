package com.paizo.balance.service;


import com.paizo.balance.config.MoneyConfiguration;
import com.paizo.balance.service.impl.UtilityServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * tests for {@link UtilityServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilityServiceTest {

    private static final String[] values = {"123", "123.1", "123.12", "123.123", "123.9999"};
    private static final String[] expectedValues = {"123.00", "123.10", "123.12", "123.12", "123.99"};

    @InjectMocks
    private UtilityService utilityService = new UtilityServiceImpl();

    @Mock
    private MoneyConfiguration moneyCfg;

    @Test
    public void createBigDecimalInstanceWithAppSettingsShouldReturnBigDecimalsWithCorrectFormat() throws Exception {

        when(moneyCfg.getScale()).thenReturn(2);
        when(moneyCfg.getRoundingMode()).thenReturn(RoundingMode.FLOOR);

        for (int i=0; i < values.length; i++) {
            assertThat(
                    utilityService.createBigDecimalInstanceWithAppSettings(values[i])
            ).isEqualTo(expectedValues[i]);
        }
    }
}
