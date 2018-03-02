package org.vaadin.teemusa.undertow;

import java.util.Optional;

import javax.servlet.annotation.WebServlet;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

/**
 * Test rule for spining up Undertow servlet containers for tests. Used with
 * {@link ClassRule} to run one instance per test class, or {@link Rule} to run
 * one instance per test method.
 * 
 * @see TestServlet
 * @see TestUI
 */
public class UndertowServer extends ExternalResource {

    private Undertow undertowServer;
    private int port;
    private String serverAddress;

    public UndertowServer() {
    }

    protected void initUI(Class<? extends UI> uiClass) {
        initUI(uiClass, NetworkUtil.getRandomPort());
    }

    protected void initUI(Class<? extends UI> uiClass, int port) {
        ServletInfo servlet = Servlets
                .servlet(uiClass.getSimpleName(), VaadinServlet.class)
                .addInitParam("ui", uiClass.getCanonicalName())
                .addMappings("/*");

        init(servlet, port);
    }

    protected void initServlet(Class<? extends VaadinServlet> servletClass) {
        initServlet(servletClass, NetworkUtil.getRandomPort());
    }

    protected void initServlet(Class<? extends VaadinServlet> servletClass,
            int port) {
        assert servletClass.isAnnotationPresent(
                WebServlet.class) : "No WebServlet annotation present.";

        WebServlet webServlet = servletClass.getAnnotation(WebServlet.class);
        ServletInfo servlet = Servlets
                .servlet(servletClass.getSimpleName(), servletClass)
                .addMappings(webServlet.value());

        init(servlet, port);
    }

    protected void init(ServletInfo servlet, int port) {
        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(getClass().getClassLoader()).setContextPath("/")
                .setDeploymentName("ROOT.war").setDefaultEncoding("UTF-8")
                .addServlets(servlet);

        DeploymentManager manager = Servlets.defaultContainer()
                .addDeployment(servletBuilder);

        manager.deploy();

        try {
            HttpHandler httpHandler = manager.start();
            PathHandler path = Handlers.path(Handlers.redirect("/"))
                    .addPrefixPath("/", httpHandler);

            this.port = port;
            undertowServer = Undertow.builder().addHttpListener(port, "0.0.0.0")
                    .setHandler(path).build();

            undertowServer.getListenerInfo().forEach(System.out::println);
        } catch (Exception e) {
        }
    }

    void start() {
        if (serverAddress == null) {
            // Find out the address where the browser can access the server.
            serverAddress = NetworkUtil.getDeploymentHostname();
        }
        Optional.ofNullable(undertowServer).ifPresent(e -> e.start());
    }

    void stop() {
        Optional.ofNullable(undertowServer).ifPresent(e -> e.stop());
    }

    /**
     * Gets the port where the server is running.
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the base URL for the deployment with a following slash.
     * 
     * @return base URL
     */
    public String getBaseURL() {
        return "http://" + serverAddress + ":" + getPort() + "/";
    }

    @Override
    public Statement apply(Statement base, Description description) {
        // Determine the servlet
        Class<?> cls = description.getTestClass();
        if (undertowServer == null) {
            if (cls.isAnnotationPresent(TestServlet.class)) {
                initServlet(cls.getAnnotation(TestServlet.class).value());
            } else if (cls.isAnnotationPresent(TestUI.class)) {
                initUI(cls.getAnnotation(TestUI.class).value());
            } else {
                throw new IllegalStateException(
                        "Cannot start Undertow server. Missing @TestServlet annotation");
            }
        }

        return super.apply(base, description);
    }

    @Override
    protected void before() throws Throwable {
        start();
    }

    @Override
    protected void after() {
        stop();
    }

    /**
     * Creates a new undertow server for given Servlet using random port.
     * 
     * @param servletClass
     *            the servlet to deploy
     * @return the undertow server
     */
    public static UndertowServer withServlet(
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
    public static UndertowServer withServlet(
            Class<? extends VaadinServlet> servletClass, int port) {
        UndertowServer server = new UndertowServer();
        server.initServlet(servletClass, port);
        return server;
    }

    /**
     * Creates a new undertow server for given UI using random port.
     * 
     * @param uiClass
     *            the ui to deploy
     * @return the undertow server
     */
    public static UndertowServer withUI(Class<? extends UI> uiClass) {
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
    public static UndertowServer withUI(Class<? extends UI> uiClass, int port) {
        UndertowServer server = new UndertowServer();
        server.initUI(uiClass, port);
        return server;
    }

    /**
     * Creates a new undertow server without configuring any UI or Servlet.
     * Should be used as a {@link ClassRule} accompanied with
     * {@link TestServlet} or {@link TestUI}.
     * 
     * @return the undertow server
     */
    public static UndertowServer create() {
        return new UndertowServer();
    }
}
