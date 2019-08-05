package org.onap.sdc.common.versioning.services.impl;

import java.util.HashSet;
import java.util.Set;
import org.onap.sdc.common.versioning.services.types.Version;
import org.onap.sdc.common.versioning.services.types.VersionCreationMethod;
import org.onap.sdc.common.versioning.services.types.VersionStatus;
import org.springframework.stereotype.Service;

@Service
public class MajorVersionCalculatorImpl implements VersionCalculator {

    private static final String INITIAL_VERSION = "1.0";
    private static final String VERSION_STRING_VIOLATION_MSG =
            "Version string must be in the format of: {integer}.{integer}";

    @Override
    public String calculate(String baseVersion, VersionCreationMethod creationMethod) {

        if (baseVersion == null) {
            return INITIAL_VERSION;
        }

        String[] versionLevels = baseVersion.split("\\.");
        if (versionLevels.length != 2) {
            throw new IllegalArgumentException(VERSION_STRING_VIOLATION_MSG);
        }

        int index = Integer.parseInt(versionLevels[0]);
        index++;

        return index + ".0";
    }

    @Override
    public void injectAdditionalInfo(Version version, Set<String> existingVersions) {
        Set<VersionCreationMethod> optionalCreationMethods = new HashSet<>();
        if (version.getStatus().equals(VersionStatus.Certified)) {
            String nextVersion = calculate(version.getName(), VersionCreationMethod.major);
            if (!existingVersions.contains(nextVersion)) {
                optionalCreationMethods.add(VersionCreationMethod.major);
            }
        }
        version.addProperty("OptionalCreationMethods", optionalCreationMethods);
    }
}
