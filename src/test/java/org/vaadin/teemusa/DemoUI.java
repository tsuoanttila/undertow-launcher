package org.vaadin.teemusa;

import javax.servlet.annotation.WebServlet;

import org.vaadin.teemusa.undertow.UndertowLauncher;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class DemoUI extends UI {

    private int clickCounter = 0;

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        Label counter = new Label("" + clickCounter);
        counter.setId("counter");
        layout.addComponents(
                new Button("Click me",
                        e -> counter.setValue("" + (++clickCounter))),
                new Label("Click Counter:"), counter);

        setContent(layout);
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    public static void main(String[] args) {
        // Set up server with UI and run in default port 8080
        UndertowLauncher.withUI(DemoUI.class).run();
    }
}
