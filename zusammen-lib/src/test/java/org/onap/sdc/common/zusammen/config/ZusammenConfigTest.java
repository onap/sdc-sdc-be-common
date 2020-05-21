/*
 * Copyright Â© 2019 European Support Limited
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

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ZusammenConfigTest {

    private static final String a = "a";
    private static final String emptyStr = "";

    @Test
    public void TestConfig() {
        ZusammenConfigProvider provider = Mockito.spy(getZusammenConfigProvider(a));
        ZusammenConfig zusammenConfig = new ZusammenConfig(provider);
        zusammenConfig.init();
        Mockito.verify(provider).getCassandraAddresses();
        Assert.assertEquals(a, System.getProperty("cassandra.nodes"));
    }

    @AfterEach
    public void cleanUp() {
        System.setProperty("cassandra.nodes", emptyStr);
        System.setProperty("cassandra.port", emptyStr);
        System.setProperty("cassandra.keyspace", emptyStr);

        System.setProperty("cassandra.authenticate", emptyStr);
        System.setProperty("cassandra.user", emptyStr);
        System.setProperty("cassandra.password", emptyStr);

        System.setProperty("cassandra.ssl", emptyStr);
        System.setProperty("cassandra.truststore", emptyStr);
        System.setProperty("cassandra.truststore.password", emptyStr);
    }

    private ZusammenConfigProvider getZusammenConfigProvider(String a) {
        return new ZusammenConfigProvider() {
            @Override
            public String getCassandraAddresses() {
                return a;
            }

            @Override
            public String getCassandraPort() {
                return a;
            }

            @Override
            public String getCassandraAuth() {
                return Boolean.FALSE.toString();
            }

            @Override
            public String getCassandraUser() {
                return a;
            }

            @Override
            public String getCassandraPassword() {
                return a;
            }

            @Override
            public String getCassandraSSL() {
                return Boolean.FALSE.toString();
            }

            @Override
            public String getCassandraTrustStorePath() {
                return a;
            }

            @Override
            public String getCassandraTrustStorePassword() {
                return a;
            }
        };
    }


}
