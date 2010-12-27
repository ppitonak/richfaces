/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat, Inc. and individual contributors
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

package org.richfaces.component;

import javax.faces.component.UIComponentBase;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;
import org.richfaces.cdk.annotations.TagType;
import org.richfaces.event.DropEvent;
import org.richfaces.event.DropListener;

/**
 * @author abelevich
 *
 */

@JsfComponent(
        type = AbstractDropTarget.COMPONENT_TYPE,
        family = AbstractDropTarget.COMPONENT_FAMILY, 
        generate = "org.richfaces.component.UIDropTarget",
        renderer = @JsfRenderer(type = "org.richfaces.DropTargetRenderer"),
        tag = @Tag(name="dropTarget" ,handler="org.richfaces.view.facelets.DropHandler", type = TagType.Facelets)
)
public abstract class AbstractDropTarget extends UIComponentBase {
    
    public static final String COMPONENT_TYPE = "org.richfaces.DropTarget";

    public static final String COMPONENT_FAMILY = "org.richfaces.DropTarget";    
    
    
    @Attribute
    public abstract Object getDropValue();
    
    public abstract Object getAcceptedTypes();
    
    public void addDropListener(DropListener listener) {
        addFacesListener(listener);
    }
    
    public void removeDropListener(DropListener listener) {
        removeFacesListener(listener);
    }
    
    @Override
    public void queueEvent(FacesEvent event) {
        if(event instanceof DropEvent) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
        }
        super.queueEvent(event);
    }
}