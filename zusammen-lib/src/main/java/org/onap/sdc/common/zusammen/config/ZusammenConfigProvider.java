package org.onap.sdc.common.zusammen.config;

public interface ZusammenConfigProvider {

    String getCassandraAddresses();

    String getCassandraPort();

    String getCassandraAuth();

    String getCassandraUser();

    String getCassandraPassword();

    String getCassandraSSL();

    String getCassandraTrustStorePath();

    String getCassandraTrustStorePassword();
}
