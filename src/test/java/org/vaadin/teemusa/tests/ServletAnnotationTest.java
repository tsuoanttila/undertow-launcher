package org.vaadin.teemusa.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.ClassRule;
import org.junit.Test;
import org.vaadin.teemusa.DemoUI;
import org.vaadin.teemusa.undertow.TestServlet;
import org.vaadin.teemusa.undertow.UndertowRule;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.ParallelTest;

@TestServlet(DemoUI.Servlet.class)
@RunLocally(Browser.PHANTOMJS)
public class ServletAnnotationTest extends ParallelTest {

    @ClassRule
    public static UndertowRule serverRule = UndertowRule.create();

    @Test
    public void testSingleClick() throws IOException {
        getDriver().get(serverRule.getServer().getBaseURL());
        assertThat($(LabelElement.class).id("counter").getText(), is("0"));
        $(ButtonElement.class).first().click();
        assertThat($(LabelElement.class).id("counter").getText(), is("1"));
    }

    @Test
    public void testFiveClicks() throws IOException {
        getDriver().get(serverRule.getServer().getBaseURL());
        assertThat($(LabelElement.class).id("counter").getText(), is("0"));
        IntStream.range(0, 5)
                .forEach(i -> $(ButtonElement.class).first().click());
        assertThat($(LabelElement.class).id("counter").getText(), is("5"));
    }
}
