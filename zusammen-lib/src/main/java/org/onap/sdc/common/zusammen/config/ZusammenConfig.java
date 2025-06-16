/*
 * Copyright Â© 2018 European Support Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.sdc.common.zusammen.config;

import com.datastax.oss.driver.api.core.ConsistencyLevel;

import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZusammenConfig {

    private static final String KEYSPACE = "zusammen";

    private final ZusammenConfigProvider provider;


    public ZusammenConfig(ZusammenConfigProvider provider) {
        this.provider = provider;
    }

    @PostConstruct
    public void init() {
        System.setProperty("cassandra.consistency.level", ConsistencyLevel.LOCAL_QUORUM.name());
        System.setProperty("cassandra.nodes", provider.getCassandraAddresses());
        System.setProperty("cassandra.port", provider.getCassandraPort());
        System.setProperty("cassandra.keyspace", KEYSPACE);

        System.setProperty("cassandra.authenticate", Boolean.toString(Boolean.valueOf(provider.getCassandraAuth())));
        System.setProperty("cassandra.user", provider.getCassandraUser());
        System.setProperty("cassandra.password", provider.getCassandraPassword());

        System.setProperty("cassandra.truststore", provider.getCassandraTrustStorePath());
        System.setProperty("cassandra.truststore.password", provider.getCassandraTrustStorePassword());
    }
}
