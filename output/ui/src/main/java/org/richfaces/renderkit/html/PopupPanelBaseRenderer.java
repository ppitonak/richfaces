package org.richfaces.renderkit.html;

import static org.richfaces.renderkit.RenderKitUtils.addToScriptHash;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.ajax4jsf.javascript.ScriptUtils;
import org.richfaces.component.AbstractPopupPanel;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONMap;
import org.richfaces.renderkit.RendererBase;

//TODO nick - JSF have concept of library, it should be used instead of '/' in resource names
@ResourceDependencies( { 
    @ResourceDependency(library = "org.richfaces", name = "base-component.reslib"), 
    @ResourceDependency(library = "org.richfaces", name = "popupPanel.js"),
    @ResourceDependency(library = "org.richfaces", name = "popupPanelBorders.js"), 
    @ResourceDependency(library = "org.richfaces", name = "popupPanelSizer.js"),
    @ResourceDependency(library = "org.richfaces", name = "popupPanel.ecss")

})
public class PopupPanelBaseRenderer extends RendererBase {

    private static final String CONTROLS_FACET = "controls";
    private static final String HEADER_FACET = "header";
    private static final int SIZE = 10;
    private static final String STATE_OPTION_SUFFIX = "StateOption_";
    
    //TODO nick - use enums
    private static final Set<String> ALLOWED_ATTACHMENT_OPTIONS = new HashSet<String>();
    static {
        ALLOWED_ATTACHMENT_OPTIONS.add("body");
        ALLOWED_ATTACHMENT_OPTIONS.add("parent");
        ALLOWED_ATTACHMENT_OPTIONS.add("form");
    }

    public void renderHeaderFacet(FacesContext context, UIComponent component) throws IOException {
        renderFacet(context, component, HEADER_FACET);
    }

    public void renderControlsFacet(FacesContext context, UIComponent component) throws IOException {
        renderFacet(context, component, CONTROLS_FACET);
    }

    private void renderFacet(FacesContext context, UIComponent component, String facet) throws IOException {
        UIComponent headerFacet = component.getFacet(facet);
        headerFacet.encodeAll(context);
    }

    @SuppressWarnings("unchecked")
    protected void doDecode(FacesContext context, UIComponent component) {
        super.doDecode(context, component);

        AbstractPopupPanel panel = (AbstractPopupPanel) component;
        ExternalContext exCtx = context.getExternalContext();
        Map<String, String> rqMap = exCtx.getRequestParameterMap();
        Object panelOpenState = rqMap.get(panel.getClientId(context) + "OpenedState");

        if (panel.isKeepVisualState()) {
            if (null != panelOpenState) {
                // Bug https://jira.jboss.org/jira/browse/RF-2466
                // Incorrect old:
                // panel.setShowWhenRendered(Boolean.parseBoolean((String) clnId));
                // ShowWhenRendered can be settled separately with modal panel "showWhenRendered" attribute
                // so we should combine ShowWhenRendered || KeepVisualState && (OpenedState==TRUE) against rewriting
                boolean show = panel.isShow() || Boolean.parseBoolean((String) panelOpenState);
                panel.setShow(show);

                Map<String, Object> visualOptions = (Map<String, Object>) getHandledVisualOptions(panel);
                Iterator<Entry<String, String>> it = rqMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    int suffixPos = entry.getKey().toString().indexOf(STATE_OPTION_SUFFIX);
                    if (-1 != suffixPos) {
                        String key = entry.getKey().toString().substring(suffixPos + STATE_OPTION_SUFFIX.length());
                        visualOptions.put(key, entry.getValue());
                    }
                }
            }
        }
    }

    protected Class getComponentClass() {
        return AbstractPopupPanel.class;
    }

    public void checkOptions(FacesContext context, UIComponent component) {
    	AbstractPopupPanel panel = (AbstractPopupPanel) component;
        if (panel.isAutosized() && panel.isResizeable()) {
            throw new IllegalArgumentException("Autosized modal panel can't be resizeable.");
        }

        String domElementAttachment = panel.getDomElementAttachment();
        if (domElementAttachment != null && domElementAttachment.trim().length() != 0) {
            if (!ALLOWED_ATTACHMENT_OPTIONS.contains(domElementAttachment)) {
                throw new IllegalArgumentException("Value '" + domElementAttachment
                    + "' of domElementAttachment attribute is illegal. " + "Allowed values are: "
                    + ALLOWED_ATTACHMENT_OPTIONS);
            }
        }

        if (panel.getMinHeight() != -1) {
            if (panel.getMinHeight() < SIZE) {
                throw new FacesException("Attribbute minWidth should be greater then 10px");
            }

        }

        if (panel.getMinWidth() != -1) {
            if (panel.getMinWidth() < SIZE) {
                throw new FacesException("Attribbute minHeight should be greater then 10px");
            }

        }
    }

    public boolean getRendersChildren() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public String buildShowScript(FacesContext context, UIComponent component) {
    	AbstractPopupPanel panel = (AbstractPopupPanel) component;
        StringBuilder result = new StringBuilder();

        // Bug https://jira.jboss.org/jira/browse/RF-2466
        // We are already processed KeepVisualState and current open state in
        // doDecode, so no need to check panel.isKeepVisualState() here.
        if (panel.isShow()) {
            result.append("RichFaces.ui.PopupPanel.showPopupPanel('" + panel.getClientId(context) + "', {");

            //TODO nick - use ScriptUtils.toScript
            Iterator<Map.Entry<String, Object>> it = ((Map<String, Object>) getHandledVisualOptions(panel)).entrySet()
                .iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();

                result.append(entry.getKey() + ": '" + entry.getValue() + "'");
                if (it.hasNext()) {
                    result.append(", ");
                }
            }

            result.append("});");
        }
        if (result.length() > 0) {
            return result.toString();
        }
        return null;
    }

    public String getStyleIfTrimmed(UIComponent panel){
    	if (panel.getAttributes().get("trimOverlayedElements").equals(Boolean.TRUE)) {
    	    return "position: relative; z-index : 0;";
    	}
    	return "";
    }
    
    public String buildScript(FacesContext context, UIComponent component) throws IOException {
    	AbstractPopupPanel panel = (AbstractPopupPanel) component;
        StringBuilder result = new StringBuilder();
        result.append("new RichFaces.ui.PopupPanel('");
        result.append(panel.getClientId());
        result.append("',");
        Map<String, Object> attributes = component.getAttributes();
        Map<String, Object> options = new HashMap<String, Object>();
        addToScriptHash(options, "width", panel.getWidth(), "-1");
        addToScriptHash(options, "height", panel.getHeight(), "-1");
        addToScriptHash(options, "minWidth", panel.getMinWidth(), "-1");
        addToScriptHash(options, "minHeight", panel.getMinHeight(), "-1");
        addToScriptHash(options, "maxWidth", panel.getMaxWidth(), "" +Integer.MAX_VALUE);
        addToScriptHash(options, "maxHeight", panel.getMaxHeight(), "" +Integer.MAX_VALUE);
        addToScriptHash(options, "moveable", panel.isMoveable(), "true");
        addToScriptHash(options, "followByScroll", panel.isFollowByScroll(), "true");
        addToScriptHash(options, "left", panel.getLeft(), "auto");
        addToScriptHash(options, "top", panel.getTop(), "auto");
        addToScriptHash(options, "zindex", panel.getZindex(), "100");
        addToScriptHash(options, "shadowDepth", panel.getShadowDepth(), "2");
        addToScriptHash(options, "shadowOpacity", panel.getShadowOpacity(), "0.1");
        addToScriptHash(options, "domElementAttachment", panel.getDomElementAttachment());
        
        addToScriptHash(options, "keepVisualState", panel.isKeepVisualState(), "false");
        addToScriptHash(options, "show", panel.isShow(), "false");
        addToScriptHash(options, "modal", panel.isModal(), "true");
        addToScriptHash(options, "autosized", panel.isAutosized(), "false");
        addToScriptHash(options, "resizeable", panel.isResizeable(), "false");
        addToScriptHash(options, "overlapEmbedObjects", panel.isOverlapEmbedObjects(), "false");
        addToScriptHash(options, "visualOptions", writeVisualOptions(context, panel));
        addToScriptHash(options, "onresize", buildEventFunction(attributes.get("onresize")));
        addToScriptHash(options, "onmove", buildEventFunction(attributes.get("onmove")));
        addToScriptHash(options, "onshow", buildEventFunction(attributes.get("onshow")));
        addToScriptHash(options, "onhide", buildEventFunction(attributes.get("onhide")));
        addToScriptHash(options, "onbeforeshow", buildEventFunction(attributes.get("onbeforeshow")));
        addToScriptHash(options, "onbeforehide", buildEventFunction(attributes.get("onbeforehide")));

        ScriptUtils.appendScript(result, options);
        result.append(");");
        return result.toString();
    }

    private Object buildEventFunction(Object eventFunction) {
        if(eventFunction != null && eventFunction.toString().length() > 0) {
            return "new Function(\"" + eventFunction.toString() + "\");";
        }
        return null;
    }

    public Map<String, Object> getHandledVisualOptions(AbstractPopupPanel panel) {
        String options = panel.getVisualOptions();
        Map<String, Object> result;
        result = prepareVisualOptions(options, panel);

        if (null == result) {
            result = new HashMap<String, Object>();
        }
        return result;
    }
    
    private String writeVisualOptions(FacesContext context, AbstractPopupPanel panel) throws IOException {
        StringBuffer result = new StringBuffer();

        Iterator<Map.Entry<String, Object>> it = ((Map<String, Object>) getHandledVisualOptions(panel)).entrySet()
            .iterator();
        if (it.hasNext()) {
            result.append(",\n");
        }
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();

            result.append(entry.getKey() + ": '" + entry.getValue() + "'");
            if (it.hasNext()) {
                result.append(",");
            }
        }
        return result.toString();
    }
    
    private Map<String, Object> prepareVisualOptions(Object value, AbstractPopupPanel panel) {
        if (null == value) {
            return new HashMap<String, Object>();
        } else if (value instanceof Map) {
            return (Map<String, Object>) value;
        } else if (value instanceof String) {
            String s = (String) value;
            if (!s.startsWith("{")) {
                s = "{" + s + "}";
            }
            try {
                return new HashMap<String, Object>(new JSONMap(s));
            } catch (JSONException e) {
                throw new FacesException(e);
            }
        } else {
            throw new FacesException("Attribute visualOptions of component [" + panel.getClientId(FacesContext.getCurrentInstance())
                + "] must be instance of Map or String, but its type is " + value.getClass().getSimpleName());
        }
    }
}