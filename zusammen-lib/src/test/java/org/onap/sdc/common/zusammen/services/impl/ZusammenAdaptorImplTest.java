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

package org.onap.sdc.common.zusammen.services.impl;

import com.amdocs.zusammen.adaptor.inbound.api.types.item.Element;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.ElementInfo;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.ZusammenElement;
import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.item.Action;
import com.amdocs.zusammen.datatypes.item.ElementContext;
import com.amdocs.zusammen.datatypes.item.Info;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.sdc.common.zusammen.persistence.ZusammenConnector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class ZusammenAdaptorImplTest {

    private static final SessionContext CONTEXT = new SessionContext();
    private static final ElementContext ELEMENT_CONTEXT = new ElementContext();

    private static final String NOT_EXISTING_ELEMENT_NAME = "nonExistingName";
    private static final String ELEMENT_NAME = "element2";
    private static final Id ELEMENT_ID = new Id("elementId 0");
    private static final List<ElementInfo> ELEMENTS =
            Arrays.asList(createElementInfo("elementId1", "element1"), createElementInfo("elementId2",
                    ELEMENT_NAME), createElementInfo("elementId3", "element3"));

    private final ZusammenConnector connector = Mockito.mock(ZusammenConnector.class);
    @InjectMocks
    private ZusammenAdaptorImpl zusammenAdaptor;
    @Captor
    private ArgumentCaptor<Element> savedElementArg;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getEmptyWhenElementNameNotExist() {
        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        Optional<Element> element =
                zusammenAdaptor.getElementByName(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID, NOT_EXISTING_ELEMENT_NAME);

        assertFalse(element.isPresent());
    }

    @Test
    public void getEmptyInfoWhenElementNameNotExist() {
        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        Optional<ElementInfo> elementInfo =
                zusammenAdaptor.getElementInfoByName(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID, NOT_EXISTING_ELEMENT_NAME);

        assertFalse(elementInfo.isPresent());
    }

    @Test
    public void getElementWhenItsNameExist() {
        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);
        ZusammenElement returnedElement = new ZusammenElement();
        doReturn(returnedElement).when(connector).getElement(CONTEXT, ELEMENT_CONTEXT, ELEMENTS.get(1).getId());

        Optional<Element> element = zusammenAdaptor.getElementByName(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID,
                ELEMENT_NAME);

        assertTrue(element.isPresent());
        assertEquals(returnedElement, element.get());
    }

    @Test
    public void getElementInfoWhenItsNameExist() {
        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        Optional<ElementInfo> elementInfo =
                zusammenAdaptor.getElementInfoByName(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID, ELEMENT_NAME);

        assertTrue(elementInfo.isPresent());
        assertEquals(ELEMENTS.get(1), elementInfo.get());

    }

    @Test
    public void listElementsWhenTheirParentIdExist() {
        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        List<ZusammenElement> returnedElements =
            Arrays.asList(new ZusammenElement(), new ZusammenElement(), new ZusammenElement());
        doReturn(returnedElements.get(0)).when(connector).getElement(CONTEXT, ELEMENT_CONTEXT, ELEMENTS.get(0).getId());
        doReturn(returnedElements.get(1)).when(connector).getElement(CONTEXT, ELEMENT_CONTEXT, ELEMENTS.get(1).getId());
        doReturn(returnedElements.get(2)).when(connector).getElement(CONTEXT, ELEMENT_CONTEXT, ELEMENTS.get(2).getId());

        Collection<Element> elements = zusammenAdaptor.listElementData(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        assertEquals(returnedElements, elements);
    }

    @Test
    public void getEmptyListWhenParentElementNameNotExist() {
        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        Collection<ElementInfo> elements =
                zusammenAdaptor.listElementsByName(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID, NOT_EXISTING_ELEMENT_NAME);

        assertTrue(elements.isEmpty());
    }

    @Test
    public void listElementsInfoWhenTheirParentElementNameExist() {
        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        List<ElementInfo> returnedElements = Arrays.asList(new ElementInfo(), new ElementInfo());
        doReturn(returnedElements).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENTS.get(1).getId());

        Collection<ElementInfo> elements =
                zusammenAdaptor.listElementsByName(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID, ELEMENT_NAME);

        assertEquals(returnedElements, elements);
    }

    @Test
    public void failWhenSavingElementWithoutIdNameOrAction() {
        assertThrows(
            IllegalArgumentException.class,
            () -> zusammenAdaptor.saveElement(CONTEXT, ELEMENT_CONTEXT, new ZusammenElement(), "Illegal element save")
        );
    }

    @Test
    public void saveElementAsRootWhenParentIdNotSupplied() {
        String message = "Create new element tree";
        ZusammenElement element = new ZusammenElement();
        element.setAction(Action.CREATE);

        ZusammenElement subElement = new ZusammenElement();
        subElement.setAction(Action.CREATE);
        element.addSubElement(subElement);

        testSaveElement(message, element);

        verify(connector).saveElement(CONTEXT, ELEMENT_CONTEXT, element, message);
    }

    @Test
    public void saveElementAsSubWhenParentIdSupplied() {
        String message = "Create sub element";
        ZusammenElement element = new ZusammenElement();
        element.setAction(Action.IGNORE);
        element.setElementId(ELEMENT_ID);

        ZusammenElement subElement = new ZusammenElement();
        subElement.setAction(Action.CREATE);
        element.addSubElement(subElement);

        testSaveElement(message, element);

        verify(connector).saveElement(CONTEXT, ELEMENT_CONTEXT, element, message);
    }

    @Test
    public void saveElementWhenItsIdSupplied() {
        String message = "Update element";
        ZusammenElement element = new ZusammenElement();
        element.setAction(Action.UPDATE);
        element.setElementId(new Id("id"));

        testSaveElement(message, element);

        verify(connector).saveElement(CONTEXT, ELEMENT_CONTEXT, element, message);
    }

    @Test
    public void saveRootElementWhenItsNameSupplied() {
        String message = "Update element";
        ZusammenElement element = new ZusammenElement();
        element.setAction(Action.UPDATE);
        element.setInfo(ELEMENTS.get(1).getInfo());

        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, null);

        testSaveElement(message, element);

        verify(connector).saveElement(eq(CONTEXT), eq(ELEMENT_CONTEXT), savedElementArg.capture(), eq(message));

        Element savedElement = savedElementArg.getValue();
        assertEquals(element, savedElement);
        assertNotNull(savedElement.getElementId());
    }

    @Test
    public void saveElementWhenItsNameAndParentIdSupplied() {
        String message = "Update existing element";
        ZusammenElement element = new ZusammenElement();
        element.setAction(Action.IGNORE);
        element.setElementId(ELEMENT_ID);

        ZusammenElement existingSub = new ZusammenElement();
        existingSub.setAction(Action.UPDATE);
        existingSub.setInfo(ELEMENTS.get(2).getInfo());
        element.addSubElement(existingSub);

        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        testSaveElement(message, element);

        verify(connector).saveElement(eq(CONTEXT), eq(ELEMENT_CONTEXT), savedElementArg.capture(), eq(message));

        Element savedElement = savedElementArg.getValue();
        assertEquals(element, savedElement);

        Element updated = savedElement.getSubElements().iterator().next();
        assertNotNull(updated.getElementId());
        assertEquals(Action.UPDATE, updated.getAction());
    }

    @Test
    public void saveElementWithCreateActionInsteadOfUpdateWhenItDoesNotExist() {
        String message = "Create new element";
        ZusammenElement element = new ZusammenElement();
        element.setAction(Action.IGNORE);
        element.setElementId(ELEMENT_ID);

        ZusammenElement nonExistingSub = new ZusammenElement();
        nonExistingSub.setAction(Action.UPDATE);
        Info info = new Info();
        info.setName(NOT_EXISTING_ELEMENT_NAME);
        nonExistingSub.setInfo(info);

        element.addSubElement(nonExistingSub);

        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        testSaveElement(message, element);

        verify(connector).saveElement(eq(CONTEXT), eq(ELEMENT_CONTEXT), savedElementArg.capture(), eq(message));

        Element savedElement = savedElementArg.getValue();
        assertEquals(element, savedElement);

        Element created = savedElement.getSubElements().iterator().next();
        assertNull(created.getElementId());
        assertEquals(Action.CREATE, created.getAction());
    }

    @Test
    public void saveElementWithIgnoreActionWhenItExistAndActionNotSupplied() {
        String message = "save existing element";
        ZusammenElement element = new ZusammenElement();
        element.setAction(Action.IGNORE);
        element.setElementId(ELEMENT_ID);

        ZusammenElement existingSub = new ZusammenElement();
        existingSub.setInfo(ELEMENTS.get(2).getInfo());
        element.addSubElement(existingSub);

        doReturn(ELEMENTS).when(connector).listElements(CONTEXT, ELEMENT_CONTEXT, ELEMENT_ID);

        testSaveElement(message, element);

        verify(connector).saveElement(eq(CONTEXT), eq(ELEMENT_CONTEXT), savedElementArg.capture(), eq(message));

        Element savedElement = savedElementArg.getValue();
        assertEquals(element, savedElement);

        Element ignored = savedElement.getSubElements().iterator().next();
        assertNotNull(ignored.getElementId());
        assertEquals(Action.IGNORE, ignored.getAction());
    }

    private void testSaveElement(String message, ZusammenElement element) {
        ZusammenElement returnedElement = new ZusammenElement();
        doReturn(returnedElement).when(connector).saveElement(CONTEXT, ELEMENT_CONTEXT, element, message);

        Element saved = zusammenAdaptor.saveElement(CONTEXT, ELEMENT_CONTEXT, element, message);

        assertEquals(returnedElement, saved);
    }

    private static ElementInfo createElementInfo(String id, String name) {
        ElementInfo elementInfo = new ElementInfo();
        elementInfo.setId(new Id(id));
        Info info = new Info();
        info.setName(name);
        elementInfo.setInfo(info);
        return elementInfo;
    }
}