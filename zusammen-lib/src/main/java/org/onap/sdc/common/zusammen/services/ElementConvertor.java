package org.onap.sdc.common.zusammen.services;


import com.amdocs.zusammen.adaptor.inbound.api.types.item.Element;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.ElementInfo;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.ZusammenElement;

public interface ElementConvertor<T> {

    void toElement(T source, ZusammenElement target);

    T fromElement(Element element);

    T fromElementInfo(ElementInfo element);
}
