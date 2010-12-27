package org.richfaces.component;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;
import org.richfaces.renderkit.html.MenuItemRendererBase;

@JsfComponent(
    family = AbstractDropDownMenu.COMPONENT_FAMILY, 
    type = AbstractMenuItem.COMPONENT_TYPE, 
    renderer=@JsfRenderer(type = MenuItemRendererBase.RENDERER_TYPE), 
    tag = @Tag(name="menuItem"),
    attributes = {"events-props.xml", "core-props.xml", "i18n-props.xml", "ajax-props.xml"})
public abstract class AbstractMenuItem extends AbstractActionComponent {

    public static final String COMPONENT_TYPE = "org.richfaces.MenuItem";
    
    @Attribute
    public abstract String getMode();

    @Attribute
    public abstract Object getLabel();

    @Attribute
    public abstract String getIcon();

    @Attribute
    public abstract String getIconDisabled();

    @Attribute
    public abstract boolean isDisabled();
    
    public enum Facets {
        icon, 
        iconDisabled
    }   
}