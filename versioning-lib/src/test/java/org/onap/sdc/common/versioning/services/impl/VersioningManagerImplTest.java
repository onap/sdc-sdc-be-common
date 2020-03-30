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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.sdc.common.versioning.persistence.ItemDao;
import org.onap.sdc.common.versioning.persistence.VersionDao;
import org.onap.sdc.common.versioning.persistence.types.InternalVersion;
import org.onap.sdc.common.versioning.services.VersioningManager;
import org.onap.sdc.common.versioning.services.types.Version;

@RunWith(MockitoJUnitRunner.class)
public class VersioningManagerImplTest {

    @Mock
    private ItemDao itemDao;
    @Mock
    private VersionDao versionDao;
    @Mock
    private VersionCalculator versionCalculator;
    @InjectMocks
    private VersioningManagerImpl versioningManager;

    @Test
    public void testList() {
        String blaBla = "blaBla";
        InternalVersion internalVersion = new InternalVersion();
        internalVersion.setId(blaBla);
        when(versionDao.list(blaBla)).thenReturn(ImmutableList.of(internalVersion));
        List<Version> retVal = versioningManager.list(blaBla);
        assertTrue(retVal.iterator().hasNext());
        Version version = retVal.iterator().next();
        assertEquals(blaBla, version.getId());
    }

    @Test
    public void testGetVersion() {
        String blaBla = "blaBla";
        InternalVersion internalVersion = new InternalVersion();
        internalVersion.setId(blaBla);
        when(versionDao.get(blaBla, blaBla)).thenReturn(Optional.of(internalVersion));
        Version version = versioningManager.get(blaBla, blaBla);
        assertNotNull(version);
        assertEquals(blaBla, version.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetVersionEmpty() {
        String blaBla = "blaBla";
        when(versionDao.get(blaBla, blaBla)).thenReturn(Optional.empty());
        Version version = versioningManager.get(blaBla, blaBla);
    }

    @Test
    public void testUpdate() {
        String itemId = "itemId";
        String versionId = "versionId";
        InternalVersion internalVersion = new InternalVersion();
        internalVersion.setId(versionId);

        when(versionDao.get(itemId, versionId)).thenReturn(Optional.of(internalVersion));

        Version version = new Version();
        version.setId(versionId);
        version.setDescription("version desc");
        Version updatedVersion = versioningManager.update(itemId, versionId, version);

        assertNotNull(updatedVersion);
        assertEquals(versionId, updatedVersion.getId());
        assertEquals(version.getDescription(), updatedVersion.getDescription());
    }
}
