package org.onap.sdc.common.versioning.services.convertors;

import org.onap.sdc.common.versioning.services.types.Version;

public interface VersionConvertor<T> {

    String getItemType();

    //Version toVersion(T model);

    void toVersion(T source, Version target);

    //void fromVersion(Version source, T target);

    T fromVersion(Version version);
}
