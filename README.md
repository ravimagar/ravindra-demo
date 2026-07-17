# Selenium TestNG Automation Framework

## Overview
This project demonstrates a Selenium + TestNG + Java automation framework using the Page Object Model (POM), Maven, and Extent Reports.

## Features
- Page Object Model structure
- TestNG-based test execution
- ExtentReports integration
- Excel-based manual test case data placeholder
- Easy Maven-based execution from the command line

## Prerequisites
- Java 11 or higher
- Maven installed and available in your PATH
- Google Chrome installed for browser-based test execution

## Run tests
Use the following commands from the project root:

```bash
mvn clean test
```

Run the suite using the TestNG XML file:

```bash
mvn test -DsuiteXmlFile=testng.xml
```

Run a single test class:

```bash
mvn -Dtest=DemoQATest test
```

To run the browser in visible mode instead of headless mode:

```bash
mvn test -Dheadless=false
```

## Report location
After execution, the HTML report is generated at:

```text
target/extent-reports/extent-report.html
```
