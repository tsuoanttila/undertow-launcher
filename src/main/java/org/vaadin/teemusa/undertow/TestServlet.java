package org.vaadin.teemusa.undertow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.VaadinServlet;

/**
 * Annotation to define the used {@link VaadinServlet} for a test class. Used in
 * combination with {@link UndertowRule#create()}.
 * <p>
 * Mutually exclusive with {@link TestUI}.
 * 
 * @see UndertowRule
 * @see TestUI
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TestServlet {
    Class<? extends VaadinServlet> value();
}
