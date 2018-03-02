package org.vaadin.teemusa.undertow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.ui.UI;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TestUI {
    Class<? extends UI> value();
}
