package com.axellience.vuegwt.core.client.tools;

import com.axellience.vuegwt.core.client.component.ComponentJavaPrototype;
import com.axellience.vuegwt.core.client.component.VueComponent;
import com.axellience.vuegwt.core.client.directive.VueDirective;
import com.axellience.vuegwt.core.client.vue.VueJsConstructor;
import com.google.gwt.regexp.shared.RegExp;
import elemental2.core.Function;
import jsinterop.annotations.JsFunction;
import jsinterop.base.JsPropertyMap;

/**
 * This object provides utils methods for VueGWT internal processing
 * @author Adrien Baron
 */
public class VueGWTTools
{
    private static RegExp camelCasePattern = RegExp.compile("([a-z])([A-Z]+)", "g");
    private static RegExp componentEnd = RegExp.compile("Component$");
    private static RegExp directiveEnd = RegExp.compile("Directive$");

    /**
     * Return the default name to register a component based on it's class name.
     * The name of the tag is the name of the component converted to kebab-case.
     * If the component class ends with "Component", this part is ignored.
     * @param componentClassName The class name of the {@link VueComponent}
     * @return The name of the component
     */
    public static String componentToTagName(String componentClassName)
    {
        // Drop "Component" at the end of the class name
        componentClassName = componentEnd.replace(componentClassName, "");
        // Convert from CamelCase to kebab-case
        return camelCasePattern.replace(componentClassName, "$1-$2").toLowerCase();
    }

    /**
     * Return the default name to register a directive based on it's class name
     * The name of the tag is the name of the component converted to kebab-case
     * If the component class ends with "Directive", this part is ignored
     * @param directiveClassName The class name of the {@link VueDirective}
     * @return The name of the directive
     */
    public static String directiveToTagName(String directiveClassName)
    {
        // Drop "Component" at the end of the class name
        directiveClassName = directiveEnd.replace(directiveClassName, "");
        // Convert from CamelCase to kebab-case
        return camelCasePattern.replace(directiveClassName, "$1-$2").toLowerCase();
    }

    /**
     * Copy a Java class prototype to a VueComponent declaration.
     * This allows VueComponent created by Vue to pass as an instance of the VueComponent class
     * they implement.
     * @param extendedVueJsConstructor The Vue.js constructor function to extend
     * @param componentJavaPrototype The VueComponent class JS prototype to extend with
     * @param <T> The type of the VueComponent
     */
    public static <T extends VueComponent> void extendVueConstructorWithJavaPrototype(
        VueJsConstructor<T> extendedVueJsConstructor,
        ComponentJavaPrototype<T> componentJavaPrototype)
    {
        JsPropertyMap vueProto =
            (JsPropertyMap) ((JsPropertyMap) extendedVueJsConstructor).get("prototype");
        componentJavaPrototype.forEach(protoProp -> {
            if (!vueProto.has(protoProp))
                vueProto.set(protoProp, componentJavaPrototype.get(protoProp));
        });
    }

    /**
     * Proxy a method call to be warned when it called.
     * This requires the function to be JsInterop (name shouldn't change at runtime).
     * This used to observe Java Collections/Map.
     * It won't be necessary in future versions of Vue.js based on ES6 proxies.
     * @param object The object to observe
     * @param methodName The name of the method to proxify
     * @param afterMethodCall A callback called each time after the method has been executed
     * @param <T> Type of the object the we Proxy
     */
    public static <T> void wrapMethod(T object, String methodName,
        AfterMethodCall<T> afterMethodCall)
    {
        Function method = (Function) ((JsPropertyMap) object).get(methodName);

        WrappingFunction wrappingFunction = args -> {
            Object result = method.apply(object, args);
            afterMethodCall.execute(object, methodName, result, args);
            return result;
        };

        ((JsPropertyMap) object).set(methodName, wrappingFunction);
    }

    @FunctionalInterface
    @JsFunction
    private interface WrappingFunction
    {
        Object call(Object... args);
    }

    /**
     * Return a "deep" value in a given object by following an expression in the form:
     * "parent.child.property".
     * This only works if all the chain is exposed using JsInterop.
     * @param object The root object to get on
     * @param path The path to follow
     * @param <T> The type of object we get in return
     * @return The object at the end of the chain
     */
    public static <T> T getDeepValue(Object object, String path)
    {
        JsPropertyMap objectMap = (JsPropertyMap) object;
        String[] pathSplit = path.split("\\.");
        for (String s : pathSplit)
        {
            objectMap = (JsPropertyMap) objectMap.get(s);
        }
        return (T) objectMap;
    }
}