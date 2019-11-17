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

package org.onap.sdc.common.zusammen.services;

import com.amdocs.zusammen.adaptor.inbound.api.types.item.ZusammenElement;
import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.item.Action;
import com.amdocs.zusammen.datatypes.item.Info;

public class ZusammenElementUtil {

    public static final String ELEMENT_TYPE_PROPERTY = "elementType";

    public static ZusammenElement buildStructuralElement(String elementType, Action action) {
        ZusammenElement element = buildElement(null, action);
        Info info = new Info();
        info.setName(elementType);
        info.addProperty(ELEMENT_TYPE_PROPERTY, elementType);
        element.setInfo(info);
        return element;
    }

    public static ZusammenElement buildElement(Id elementId, Action action) {
        ZusammenElement element = new ZusammenElement();
        element.setElementId(elementId);
        element.setAction(action);
        return element;
    }
}
