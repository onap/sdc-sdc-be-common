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

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.RemoteEndpointAwareJdkSSLOptions;
import com.datastax.driver.core.SSLOptions;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.cassandra.ClusterBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZusammenConfig {

    private static final String[] CIPHER_SUITES = {"TLS_RSA_WITH_AES_128_CBC_SHA"};
    private static final String KEYSTORE_TYPE = "JKS";
    private static final String SECURE_SOCKET_PROTOCOL = "SSL";
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

        System.setProperty("cassandra.ssl", Boolean.toString(Boolean.valueOf(provider.getCassandraSSL())));
        System.setProperty("cassandra.truststore", provider.getCassandraTrustStorePath());
        System.setProperty("cassandra.truststore.password", provider.getCassandraTrustStorePassword());
    }

    @Bean
    @ConditionalOnProperty("cassandra.ssl")
    ClusterBuilderCustomizer clusterBuilderCustomizer() {
        SSLOptions sslOptions = RemoteEndpointAwareJdkSSLOptions
                                        .builder()
                                        .withSSLContext(getSslContext())
                                        .withCipherSuites(CIPHER_SUITES).build();
        return builder -> builder.withSSL(sslOptions);
    }

    private SSLContext getSslContext() {
        try (FileInputStream tsf = new FileInputStream(provider.getCassandraTrustStorePath())) {
            SSLContext ctx = SSLContext.getInstance(SECURE_SOCKET_PROTOCOL);
            KeyStore ts = KeyStore.getInstance(KEYSTORE_TYPE);
            ts.load(tsf, provider.getCassandraTrustStorePassword().toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
            ctx.init(null, tmf.getTrustManagers(), new SecureRandom());
            return ctx;
        } catch (Exception ex) {
            throw new BeanCreationException(ex.getMessage(), ex);
        }
    }
}