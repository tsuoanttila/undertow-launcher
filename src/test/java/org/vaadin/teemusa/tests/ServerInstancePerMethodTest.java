package org.vaadin.teemusa.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.vaadin.teemusa.DemoUI;
import org.vaadin.teemusa.undertow.UndertowServer;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.ParallelTest;

@RunLocally(Browser.PHANTOMJS)
public class ServerInstancePerMethodTest extends ParallelTest {

    private static ConcurrentHashMap<String, UndertowServer> usedServers = new ConcurrentHashMap<>();

    @Rule
    public UndertowServer server = UndertowServer.withUI(DemoUI.class);

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testSingleClick() throws IOException {
        getDriver().get(server.getBaseURL());
        assertThat($(LabelElement.class).id("counter").getText(), is("0"));
        $(ButtonElement.class).first().click();
        assertThat($(LabelElement.class).id("counter").getText(), is("1"));
    }

    @Test
    public void testFiveClicks() throws IOException {
        getDriver().get(server.getBaseURL());
        assertThat($(LabelElement.class).id("counter").getText(), is("0"));
        IntStream.range(0, 5)
                .forEach(i -> $(ButtonElement.class).first().click());
        assertThat($(LabelElement.class).id("counter").getText(), is("5"));
    }

    @After
    public void tearDown() {
        assertThat(usedServers.containsKey(testName.getMethodName()),
                is(false));
        usedServers.put(testName.getMethodName(), server);
    }

    @AfterClass
    public static void checkServersExecuted() {
        assertThat(usedServers.size(), is(2));
        for (String methodName : new HashSet<>(usedServers.keySet())) {
            UndertowServer server = usedServers.remove(methodName);
            assertThat("Servers should not contain duplicates.",
                    usedServers.values().contains(server), is(false));
            assertThat("Servers should not contain re-used ports.",
                    usedServers.values().stream().map(UndertowServer::getPort)
                            .anyMatch(i -> i == server.getPort()),
                    is(false));
        }
    }
}
