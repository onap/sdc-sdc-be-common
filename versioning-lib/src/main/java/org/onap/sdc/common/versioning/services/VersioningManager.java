/*
 * Copyright © 2016-2018 European Support Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.sdc.common.versioning.services;

import java.util.List;
import org.onap.sdc.common.versioning.services.types.Revision;
import org.onap.sdc.common.versioning.services.types.Version;
import org.onap.sdc.common.versioning.services.types.VersionCreationMethod;
import org.onap.sdc.common.versioning.services.types.VersionStatus;

public interface VersioningManager {

    List<Version> list(String itemId); // TODO: 5/24/2017 filter (by status for example)

    Version get(String itemId, String versionId);

    Version create(String itemId, String baseVersionId, Version version, VersionCreationMethod creationMethod);

    Version update(String itemId, String versionId, Version version);

    void updateStatus(String itemId, String versionId, VersionStatus status, String message);

    void publish(String itemId, String versionId, String message);

    void sync(String itemId, String versionId);

    void forceSync(String itemId, String versionId);

    void revert(String itemId, String versionId, String revisionId);

    List<Revision> listRevisions(String itemId, String versionId);

    void clean(String itemId, String versionId);
}
