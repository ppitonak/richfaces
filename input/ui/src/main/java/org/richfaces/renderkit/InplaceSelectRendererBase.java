/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.richfaces.renderkit;

import java.io.IOException;
import java.util.List;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.richfaces.component.AbstractInplaceSelect;
import org.richfaces.component.InplaceComponent;

/**
 * @author Anton Belevich
 * 
 */

@ResourceDependencies({
        @ResourceDependency(library = "javax.faces", name = "jsf.js"),
        @ResourceDependency(name = "jquery.js"),
        @ResourceDependency(name = "richfaces.js"),
        @ResourceDependency(name = "jquery.position.js"),
        @ResourceDependency(name = "richfaces-event.js"),
        @ResourceDependency(name = "richfaces-base-component.js"),
        @ResourceDependency(name = "richfaces-selection.js"),
        @ResourceDependency(library = "org.richfaces", name = "inputBase.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceBase.js"),
        @ResourceDependency(library = "org.richfaces", name = "popup.js"),
        @ResourceDependency(library = "org.richfaces", name = "popupList.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceInput.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceSelect.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceSelect.ecss") })
public class InplaceSelectRendererBase extends InplaceInputRendererBase {
    
    public static final String OPTIONS_OPEN_ON_EDIT = "openOnEdit";
    
    public static final String OPTIONS_SAVE_ON_SELECT = "saveOnSelect";
    
    public static final String ITEM_CSS = "rf-is-opt"; 
    
    public static final String SELECT_ITEM_CSS = "rf-is-sel";    

    public static final String LIST_CSS = "rf-is-lst-cord";

    public List<ClientSelectItem> getConvertedSelectItems(FacesContext facesContext, UIComponent component) {
        return SelectHelper.getConvertedSelectItems(facesContext, component);
    }
    
    public void encodeItems(FacesContext facesContext, UIComponent component, List<ClientSelectItem> clientSelectItems) throws IOException {
        SelectHelper.encodeItems(facesContext, component, clientSelectItems, HtmlConstants.SPAN_ELEM, ITEM_CSS);
    }
    
    public void renderListHandlers(FacesContext facesContext, UIComponent component) throws IOException {
        RenderKitUtils.renderPassThroughAttributesOptimized(facesContext, component, SelectHelper.SELECT_LIST_HANDLER_ATTRIBUTES);
    }
    
    @Override
    public void renderInputHandlers(FacesContext facesContext, UIComponent component) throws IOException {
        RenderKitUtils.renderPassThroughAttributesOptimized(facesContext, component, INPLACE_INPUT_HANDLER_ATTRIBUTES);
    }
    
    public String getSelectInputLabel(FacesContext facesContext, UIComponent component) {
        return SelectHelper.getSelectInputLabel(facesContext, component);
    }
    
    public String getListWidth(UIComponent component) {
        AbstractInplaceSelect select = (AbstractInplaceSelect)component;
        String width = select.getListWidth();
        return (width != null && width.trim().length() != 0) ? ("width: " + width) : "";
    }
    
    public String getListHeight(UIComponent component) {
        AbstractInplaceSelect select = (AbstractInplaceSelect)component;
        String height = select.getListHeight();
        return (height != null && height.trim().length() != 0) ? ("height: " + height) : "";
    }
    
    public String getSelectLabel(FacesContext facesContext, UIComponent component) {
        AbstractInplaceSelect select = (AbstractInplaceSelect) component;
        String label = getSelectInputLabel(facesContext, select);
        if (!select.isDisabled() && (label == null)) {
            label = select.getDefaultLabel();
        }
        return label;
    }
    
    public String getReadyStateCss(InplaceComponent component) {
        String css = component.getReadyStateClass();
        return concatClasses("rf-is-d-s", css);
    }

    public String getEditStateCss(InplaceComponent component) {
        String css = component.getEditStateClass();
        return concatClasses("rf-is-e-s", css);
    }

    public String getChangedStateCss(InplaceComponent component) {
        String css = component.getChangedStateClass();
        return concatClasses("rf-is-c-s", css);
    }

    public String getDisableStateCss(InplaceComponent component) {
        String css = component.getDisabledStateClass();
        return concatClasses("rf-is-dis-s", css);
    }
    
    public String getEditCss(InplaceComponent component) {
        String css = component.getEditClass();
        return concatClasses("rf-is-edit", css);
    }

    public String getNoneCss(InplaceComponent component) {
        String css = component.getNoneClass();
        return concatClasses("rf-is-none", css);
    }
    
    public String getListCss(UIComponent component) {
        AbstractInplaceSelect inplaceSelect = (AbstractInplaceSelect)component;
        String css = inplaceSelect.getListClass();
        css = (css != null) ? concatClasses("rf-is-lst-cord", css) : "rf-is-lst-cord";
        return css; 
    }
}