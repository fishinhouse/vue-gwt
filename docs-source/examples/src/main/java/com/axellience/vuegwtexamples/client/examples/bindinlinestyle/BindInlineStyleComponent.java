package com.axellience.vuegwtexamples.client.examples.bindinlinestyle;

import com.axellience.vuegwt.client.component.VueComponent;
import com.axellience.vuegwt.jsr69.component.annotations.Component;
import jsinterop.annotations.JsProperty;

/**
 * @author Adrien Baron
 */
@Component
public class BindInlineStyleComponent extends VueComponent
{
    @JsProperty String activeColor;
    @JsProperty float fontSize;

    @Override
    public void created()
    {
        activeColor = "red";
        fontSize = 20;
    }
}
