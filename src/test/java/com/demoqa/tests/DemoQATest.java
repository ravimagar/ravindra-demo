package com.demoqa.tests;

import com.demoqa.pages.DemoQAPage;
import com.demoqa.utilities.BaseTest;
import com.demoqa.utilities.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DemoQATest extends BaseTest {

    @DataProvider(name = "excelData")
    public Object[][] excelData() throws IOException {
        List<String[]> rows = ExcelUtils.readExcelData(Paths.get("src/test/resources/manual_test_cases.xlsx").toString());
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i);
        }
        return data;
    }

    @Test(priority = 1)
    public void verifyFormSubmissionWithTextAndNumber() {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        page.fillForm("Ravi", "Magar", "ravi@example.com", "34", "India", Arrays.asList("Java", "Selenium"));
        page.selectCheckBoxes(true, true);
        page.uploadFile(Paths.get("src/test/resources/sample-image.png").toAbsolutePath().normalize().toString());
        page.submit();
        Assert.assertTrue(page.getResultText().contains("Submitted"), "Form submission result should appear");
    }

    @Test(priority = 2, dataProvider = "excelData")
    public void verifyDropdownSelection(String[] testCase) {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        List<String> skills = Arrays.stream(testCase[5].split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        page.fillForm(testCase[0], testCase[1], testCase[2], testCase[3], testCase[4], skills);
        page.selectCheckBoxes(Boolean.parseBoolean(testCase[6]), Boolean.parseBoolean(testCase[7]));
        page.submit();
        Assert.assertTrue(page.getResultText().contains("Submitted"), "Excel-driven submission should be completed");
    }

    @Test(priority = 3)
    public void verifyMultiSelectSkills() {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        page.fillForm("Kiran", "Sharma", "kiran@example.com", "31", "UK", Arrays.asList("Java", "Python", "Selenium"));
        page.submit();
        Assert.assertTrue(page.getResultText().contains("Java") && page.getResultText().contains("Selenium"), "All selected skills should appear");
    }

    @Test(priority = 4)
    public void verifyCheckboxSelection() {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        page.selectCheckBoxes(true, false);
        Assert.assertTrue(page.getResultText().equals("Ready"), "Checkboxes should not change result until submit");
    }

    @Test(priority = 5)
    public void verifyFileUploadField() {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        page.uploadFile(Paths.get("src/test/resources/sample-image.png").toAbsolutePath().normalize().toString());
        Assert.assertTrue(page.getResultText().contains("Ready"), "Upload field should be present");
    }

    @Test(priority = 6)
    public void verifyStatusButton() {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        page.clickStatusButton();
        Assert.assertTrue(page.getResultText().contains("Status"), "Status button should update the result text");
    }

    @Test(priority = 7)
    public void verifyManualTestCaseSheetLoads() throws IOException {
        List<String[]> rows = ExcelUtils.readExcelData(Paths.get("src/test/resources/manual_test_cases.xlsx").toString());
        Assert.assertTrue(rows.size() >= 1, "Manual test case sheet should contain at least one row");
    }

    @Test(priority = 8)
    public void verifyExcelDataProviderWorks() throws IOException {
        Object[][] data = excelData();
        Assert.assertTrue(data.length >= 1, "Excel data should be loaded");
    }

    @Test(priority = 9)
    public void verifyFormIsLoaded() {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        Assert.assertTrue(page.getResultText().contains("Ready"), "App should load successfully");
    }

    @Test(priority = 10)
    public void verifySubmitButtonExists() {
        DemoQAPage page = new DemoQAPage(driver);
        page.openApp();
        Assert.assertTrue(driver.findElement(org.openqa.selenium.By.id("submitBtn")).isDisplayed(), "Submit button should be visible");
    }
}
