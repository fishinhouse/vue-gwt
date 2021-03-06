package com.axellience.vuegwt.core.client.component.hooks;

import com.axellience.vuegwt.core.annotations.component.HookMethod;
import jsinterop.annotations.JsMethod;

/**
 * @author Adrien Baron
 */
public interface HasDeactivated
{
    @HookMethod
    @JsMethod
    void deactivated();
}
