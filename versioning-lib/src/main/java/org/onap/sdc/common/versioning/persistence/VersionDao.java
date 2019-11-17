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

package org.onap.sdc.common.versioning.persistence;

import java.util.List;
import java.util.Optional;
import org.onap.sdc.common.versioning.persistence.types.InternalVersion;
import org.onap.sdc.common.versioning.services.types.Revision;

public interface VersionDao {

    List<InternalVersion> list(String itemId);

    InternalVersion create(String itemId, InternalVersion version);

    void update(String itemId, InternalVersion version);

    Optional<InternalVersion> get(String itemId, String versionId);

    void delete(String itemId, String versionId);

    void publish(String itemId, String versionId, String message);

    void sync(String itemId, String versionId);

    void forceSync(String itemId, String versionId);

    void clean(String itemId, String versionId);

    void revert(String itemId, String versionId, String revisionId);

    List<Revision> listRevisions(String itemId, String versionId);
}
