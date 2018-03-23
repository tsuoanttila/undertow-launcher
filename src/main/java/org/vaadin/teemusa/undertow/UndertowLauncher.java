package org.vaadin.teemusa.undertow;

import java.util.logging.Logger;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * Class for launching Undertow servlet container for simple application
 * deployment.
 * 
 * @see UndertowServer
 */
public class UndertowLauncher implements Runnable {

    private final UndertowServer server;

    protected UndertowLauncher(UndertowServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        getServer().start();
        Logger.getLogger(UndertowLauncher.class.getSimpleName())
                .info("Server started at " + getServer().getBaseURL());
    }

    /**
     * Gets the server instance for this UndertowLauncher.
     * 
     * @return the undertow server instance
     */
    public UndertowServer getServer() {
        return server;
    }

    /**
     * Returns a new {@link UndertowLauncher} using the given {@link UI} with
     * default port 8080.
     * 
     * @param uiClass
     *            the ui to run
     * @return launcher
     */
    public static UndertowLauncher withUI(Class<? extends UI> uiClass) {
        return withUI(uiClass, 8080);
    }

    /**
     * Returns a new {@link UndertowLauncher} using the given {@link UI} with
     * given port.
     * 
     * @param uiClass
     *            the ui to run
     * @param port
     *            the port to use
     * @return launcher
     */
    public static UndertowLauncher withUI(Class<? extends UI> uiClass,
            int port) {
        return new UndertowLauncher(UndertowServer.withUI(uiClass, port));
    }

    /**
     * Returns a new {@link UndertowLauncher} using the given
     * {@link VaadinServlet} with default port 8080.
     * 
     * @param servletClass
     *            the VaadinServlet to run
     * @return launcher
     */
    public static UndertowLauncher withServlet(
            Class<? extends VaadinServlet> servletClass) {
        return withServlet(servletClass, 8080);
    }

    /**
     * Returns a new {@link UndertowLauncher} using the given
     * {@link VaadinServlet} with given port.
     * 
     * @param servletClass
     *            the VaadinServlet to run
     * @param port
     *            the port to use
     * @return launcher
     */
    public static UndertowLauncher withServlet(
            Class<? extends VaadinServlet> servletClass, int port) {
        return new UndertowLauncher(
                UndertowServer.withServlet(servletClass, port));
    }
}
