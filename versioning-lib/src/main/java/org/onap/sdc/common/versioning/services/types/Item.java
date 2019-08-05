/*
 *
 *  Copyright © 2017-2018 European Support Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.onap.sdc.common.versioning.services.types;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Item {

    protected String id;
    protected Date creationTime;
    protected Date modificationTime;
    protected ItemStatus status;
    @Setter
    private String type;
    @Setter
    private String name;
    @Setter
    private String owner;
    @Setter
    private String description;
    private Map<String, Object> properties = new HashMap<>();

    public boolean isNew() {
        return id == null;
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }
}
