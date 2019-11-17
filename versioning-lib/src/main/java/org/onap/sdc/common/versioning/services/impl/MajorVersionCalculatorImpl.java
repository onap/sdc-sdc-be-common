/*
 * Copyright © 2019 European Support Limited
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

package org.onap.sdc.common.versioning.services.impl;

import org.onap.sdc.common.versioning.services.types.Version;
import org.onap.sdc.common.versioning.services.types.VersionCreationMethod;
import org.onap.sdc.common.versioning.services.types.VersionStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MajorVersionCalculatorImpl implements VersionCalculator {

    private static final String INITIAL_VERSION = "0.0";
    private static final String VERSION_STRING_VIOLATION_MSG =
            "Version string must be in the format of: {integer}.{integer}";

    @Override
    public String calculate(String baseVersion, VersionCreationMethod creationMethod) {

        if (baseVersion == null) {
            baseVersion = INITIAL_VERSION;
        }

        String[] versionLevels = baseVersion.split("\\.");
        if (versionLevels.length != 2) {
            throw new IllegalArgumentException(VERSION_STRING_VIOLATION_MSG);
        }

        int majorIndex = Integer.parseInt(versionLevels[0]);
        int minorIndex = Integer.parseInt(versionLevels[1]);
        if (creationMethod.equals(VersionCreationMethod.major)) {
            majorIndex++;
            minorIndex = 0;
        } else {
            minorIndex++;
        }

        return majorIndex + "." + minorIndex;
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
