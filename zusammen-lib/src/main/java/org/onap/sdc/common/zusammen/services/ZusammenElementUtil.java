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
