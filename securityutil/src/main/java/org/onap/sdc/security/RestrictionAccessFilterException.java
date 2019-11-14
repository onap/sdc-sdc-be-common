package org.onap.sdc.security;

public class RestrictionAccessFilterException extends RuntimeException {

    public RestrictionAccessFilterException(Exception exception) {
        super(exception);
    }
}
