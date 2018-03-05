package org.vaadin.teemusa.undertow;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * Test rule for spining up Undertow servlet containers for tests. Used with
 * {@link ClassRule} to run one instance per test class, or {@link Rule} to run
 * one instance per test method.
 * 
 * @see TestServlet
 * @see TestUI
 * @see UndertowServer
 */
public class UndertowRule extends ExternalResource {

    private final UndertowServer server;

    protected UndertowRule(UndertowServer undertowServer) {
        this.server = undertowServer;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        // Determine the servlet
        Class<?> cls = description.getTestClass();
        if (!server.isInitialized()) {
            if (cls.isAnnotationPresent(TestServlet.class)) {
                server.initServlet(
                        cls.getAnnotation(TestServlet.class).value());
            } else if (cls.isAnnotationPresent(TestUI.class)) {
                server.initUI(cls.getAnnotation(TestUI.class).value());
            } else {
                throw new IllegalStateException(
                        "Cannot start Undertow server. Missing @TestServlet or @TestUI annotation");
            }
        }

        return super.apply(base, description);
    }

    @Override
    protected void before() throws Throwable {
        server.start();
    }

    @Override
    protected void after() {
        server.stop();
    }

    public UndertowServer getServer() {
        return server;
    }

    /**
     * Creates a new undertow server without configuring any UI or Servlet.
     * Should be used as a {@link ClassRule} accompanied with
     * {@link TestServlet} or {@link TestUI}.
     * 
     * @return the undertow server
     */
    public static UndertowRule create() {
        return new UndertowRule(new UndertowServer());
    }

    /**
     * Creates a new undertow server for given Servlet using random port.
     * 
     * @param servletClass
     *            the servlet to deploy
     * @return the undertow server
     */
    public static UndertowRule withServlet(
            Class<? extends VaadinServlet> servletClass) {
        return withServlet(servletClass, NetworkUtil.getRandomPort());
    }

    /**
     * Creates a new undertow server for given Servlet using given port.
     * 
     * @param servletClass
     *            the servlet to deploy
     * @param port
     *            the port to use
     * @return the undertow server
     */
    public static UndertowRule withServlet(
            Class<? extends VaadinServlet> servletClass, int port) {
        return new UndertowRule(UndertowServer.withServlet(servletClass, port));
    }

    /**
     * Creates a new undertow server for given UI using random port.
     * 
     * @param uiClass
     *            the ui to deploy
     * @return the undertow server
     */
    public static UndertowRule withUI(Class<? extends UI> uiClass) {
        return withUI(uiClass, NetworkUtil.getRandomPort());
    }

    /**
     * Creates a new undertow server for given UI using given port.
     * 
     * @param uiClass
     *            the ui to deploy
     * @param port
     *            the port to use
     * @return the undertow server
     */
    public static UndertowRule withUI(Class<? extends UI> uiClass, int port) {
        return new UndertowRule(UndertowServer.withUI(uiClass, port));
    }
}
