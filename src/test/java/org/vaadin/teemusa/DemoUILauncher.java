package org.vaadin.teemusa;

import org.vaadin.teemusa.undertow.UndertowLauncher;

public class DemoUILauncher {

    // Name of JVM property.
    private static final String SERVER_PORT = "server.port";

    public static void main(String[] args) {
        // JVM Property for changing port
        int port = System.getProperties().containsKey(SERVER_PORT)
                ? Integer.parseInt(System.getProperty(SERVER_PORT))
                : 9999;

        // With UI:
        UndertowLauncher.withUI(DemoUI.class, port).run();

        // Or with VaadinServlet class using default port [8080]:
        // UndertowLauncher.withServlet(DemoUI.Servlet.class).run();
    }
}
