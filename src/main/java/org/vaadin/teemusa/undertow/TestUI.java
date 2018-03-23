package org.vaadin.teemusa.undertow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * Annotation to define the used {@link UI} for a test class. Used in
 * combination with {@link UndertowRule#create()}.
 * <p>
 * Mutually exclusive with {@link TestServlet}.
 * 
 * @see UndertowRule
 * @see TestServlet
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TestUI {
    Class<? extends UI> value();
}
