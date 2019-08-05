package org.onap.sdc.common.versioning.services.types;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Revision {

    private String id;
    private String message;
    private Date time;
    private String user;
}
