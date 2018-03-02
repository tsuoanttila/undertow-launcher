package org.vaadin.teemusa.undertow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.VaadinServlet;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TestServlet {
    Class<? extends VaadinServlet> value();
}
