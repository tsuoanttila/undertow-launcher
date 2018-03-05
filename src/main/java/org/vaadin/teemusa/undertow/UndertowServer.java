package org.vaadin.teemusa.undertow;

import java.util.Optional;

import javax.servlet.annotation.WebServlet;

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
 * Class for spining up Undertow servlet containers.
 */
public class UndertowServer {

    private Undertow serverInstance;
    private int port;
    private String serverAddress;

    protected UndertowServer() {
    }

    protected void initUI(Class<? extends UI> uiClass) {
        initUI(uiClass, NetworkUtil.getRandomPort());
    }

    protected void initUI(Class<? extends UI> uiClass, int port) {
        ServletInfo servlet = Servlets
                .servlet(uiClass.getSimpleName(), VaadinServlet.class)
                .addInitParam("ui", uiClass.getCanonicalName())
                .addMappings("/*");

        init(servlet, uiClass.getClassLoader(), port);
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
                .addMappings(webServlet.value())
                .addMappings(webServlet.urlPatterns());

        init(servlet, servletClass.getClassLoader(), port);
    }

    protected void init(ServletInfo servlet, ClassLoader classLoader,
            int port) {
        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(classLoader).setContextPath("/")
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
            serverInstance = Undertow.builder().addHttpListener(port, "0.0.0.0")
                    .setHandler(path).build();

            serverInstance.getListenerInfo().forEach(System.out::println);
        } catch (Exception e) {
        }
    }

    /**
     * Returns whether the UndertowServer has been initialized.
     * 
     * @return {@code true} if initialized; {@code false} if not
     */
    public boolean isInitialized() {
        return serverInstance != null;
    }

    protected void start() {
        if (serverAddress == null) {
            // Find out the address where the browser can access the server.
            serverAddress = NetworkUtil.getDeploymentHostname();
        }
        Optional.ofNullable(serverInstance).ifPresent(e -> e.start());
    }

    protected void stop() {
        Optional.ofNullable(serverInstance).ifPresent(e -> e.stop());
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

    /**
     * Creates a new undertow server for given Servlet using random port.
     * 
     * @param servletClass
     *            the servlet to deploy
     * @return the undertow server
     */
    protected static UndertowServer withServlet(
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
    protected static UndertowServer withServlet(
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
    protected static UndertowServer withUI(Class<? extends UI> uiClass) {
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
    protected static UndertowServer withUI(Class<? extends UI> uiClass,
            int port) {
        UndertowServer server = new UndertowServer();
        server.initUI(uiClass, port);
        return server;
    }
}
