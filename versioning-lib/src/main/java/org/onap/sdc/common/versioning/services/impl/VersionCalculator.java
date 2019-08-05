package org.onap.sdc.common.versioning.services.impl;

import java.util.Set;
import org.onap.sdc.common.versioning.services.types.Version;
import org.onap.sdc.common.versioning.services.types.VersionCreationMethod;

public interface VersionCalculator {

    String calculate(String baseVersion, VersionCreationMethod creationMethod);

    void injectAdditionalInfo(Version version, Set<String> existingVersions);
}
