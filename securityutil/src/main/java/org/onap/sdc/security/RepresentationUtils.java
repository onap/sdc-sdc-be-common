package org.onap.sdc.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.onap.sdc.security.logs.elements.ErrorLogOptionalData;
import org.onap.sdc.security.logs.elements.LogFieldsMdcHandler;
import org.onap.sdc.security.logs.enums.EcompLoggerErrorCode;
import org.onap.sdc.security.logs.wrappers.Logger;

import java.io.IOException;

public class RepresentationUtils {

    private static final Logger log = Logger.getLogger(RepresentationUtils.class.getName());

    /**
     * Build JSON Representation of given Object
     *
     * @param elementToRepresent
     * @return
     * @throws IOException
     */
    public static <T> String toRepresentation(T elementToRepresent) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(elementToRepresent);
    }

    /**
     * Convert JSON representation to given class
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromRepresentation(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T object = null;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            object = mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error(EcompLoggerErrorCode.BUSINESS_PROCESS_ERROR, LogFieldsMdcHandler.getInstance().getServiceName(),new ErrorLogOptionalData(),
                    "Error when parsing JSON of object of type {}", clazz.getSimpleName(), e);
        } // return null in case of exception

        return object;
    }
}
