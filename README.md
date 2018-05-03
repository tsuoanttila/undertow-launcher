# Undertow Launcher for Vaadin

Undertow Launcher is an utility to help developers run and test their UIs.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to https://vaadin.com/directory/component/undertow-launcher-for-vaadin

## Building

git clone https://github.com/tsuoanttila/undertow-launcher.git
mvn clean install

## Release notes

### Version 1.0-SNAPSHOT
- `UndertowLauncher`, launcher class for `UI`s and `VaadinServlet`s
- `UndertowRule`, Junit 4 `TestRule` for automatic startup and teardown for test server.

## Roadmap

This component is developed as a hobby with no public roadmap or any guarantees of upcoming releases. That said, the following features are planned for upcoming releases:
- Junit 5 support

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create a `branch` for the change
- Make minimal changes to fix an issue or introduce a new feature
- Open a `Pull Request` to `master` of this repository
- Wait for the maintainer to have time to take a look at the change.

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

UndertowLauncher is written by Teemu Suo-Anttila

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on:

```
public class MyUI extends UI {
    public static void main(String[] args) {
        UndertowLauncher.withUI(MyUI.class).run();
    }

    /* Rest of UI code */
```

This will start the Undertow servlet container serving the given UI in port `8080`.

To use as part of a TestBench test:

```
public class MyTestClass extends TestBenchTestCase {
    @ClassRule
    public static UndertowRule serverRule = UndertowRule.withUI(MyUI.class);
    
    @Before
    public void openUrl() {
        getDriver().get(serverRule.getServer().getBaseUrl());
    }
    
    /* Test methods */
}
```

This will start a server in random port in range `[50000, 60000)` and execute the test.
Server will shut down after the all test methods in class have run.
