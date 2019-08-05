package org.onap.sdc.common.zusammen.persistence.impl;

import com.amdocs.zusammen.adaptor.inbound.api.health.HealthAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ElementAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemVersionAdaptorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZusammenAdaptorsConfig {

    @Bean
    public ItemAdaptorFactory itemAdaptorFactory() {
        return ItemAdaptorFactory.getInstance();
    }

    @Bean
    public ItemVersionAdaptorFactory itemVersionAdaptorFactory() {
        return ItemVersionAdaptorFactory.getInstance();
    }

    @Bean
    public ElementAdaptorFactory elementAdaptorFactory() {
        return ElementAdaptorFactory.getInstance();
    }

    @Bean
    public HealthAdaptorFactory healthAdaptorFactory() {
        return HealthAdaptorFactory.getInstance();
    }
}
