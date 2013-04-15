/**
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and individual contributors
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
package org.richfaces.ui.placeholder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.support.FindBy;
import org.richfaces.deployment.FrameworkDeployment;
import org.richfaces.shrinkwrap.descriptor.FaceletAsset;

/**
 * @author <a href="mailto:jstefek@redhat.com">Jiri Stefek</a>
 */
public class ITPlaceholderCalendar extends AbstractPlaceholderTest {

    @FindBy(css = INPUT_SELECTOR + " > span > input")
    private Input firstInput;

    @Deployment
    public static WebArchive createDeployment() {
        FrameworkDeployment deployment = new FrameworkDeployment(ITPlaceholderCalendar.class);

        deployment.archive().addClasses(PlaceHolderValueConverter.class, PlaceHolderValue.class);

        FaceletAsset p;
        p = deployment.baseFacelet("index.xhtml");
        p.body("<r:calendar id='input' enableManualInput='true' datePattern='MMM d, yyyy' timezone='UTC' >");
        p.body("    <r:placeholder id='placeholderID' styleClass='#{param.styleClass}' value='Placeholder Text' />");
        p.body("</r:calendar>");

        p = deployment.baseFacelet("selector.xhtml");
        p.body("<r:calendar id='input' />");
        p.body("<r:placeholder id='placeholderID' value='Placeholder Text' selector='[id=input]' />");

        p = deployment.baseFacelet("rendered.xhtml");
        p.body("<r:calendar id='input' >");
        p.body("    <r:placeholder id='placeholderID' value='Placeholder Text' rendered='false' />");
        p.body("</r:calendar>");

        p = deployment.baseFacelet("converter.xhtml");
        p.body("<r:calendar id='input' >");
        p.body("    <r:placeholder id='placeholderID' converter='placeHolderValueConverter' value='#{placeHolderValue}' />");
        p.body("</r:calendar>");

        p = deployment.baseFacelet("submit.xhtml");
        p.form("<r:calendar id='input' value='#{placeHolderValue.value3}' enableManualInput='true' datePattern='MMM d, yyyy' timeZone='#{placeHolderValue.timeZone}' >");
        p.form("    <r:placeholder id='placeholderID' value='Placeholder Text' />");
        p.form("</r:calendar>");
        p.form("<br />");
        p.form("<r:commandButton id='ajaxSubmit' value='ajax submit' execute='@form' render='output' />");
        p.form("<h:commandButton id='httpSubmit' value='http submit' />");
        p.form("<br />");
        p.form("<h:outputText id='output' value='#{placeHolderValue.value3}' >");
        p.form("    <f:convertDateTime timeZone='UTC' pattern='EEE MMM dd HH:mm:ss z yyyy'/>");
        p.form("</h:outputText>");

        return deployment.getFinalArchive();
    }

    @Override
    Input input() {
        return firstInput;
    }

    @Override
    protected String getTestedValue() {
        return "Dec 12, 2012";
    }

    @Override
    protected String getTestedValueResponse() {
        return "Wed Dec 12 00:00:00 UTC 2012";
    }

    @Ignore("calendar date conversion problem")
    @Test
    public void testSubmitTextValue() {
        super.testSubmitTextValue();
    }
}
