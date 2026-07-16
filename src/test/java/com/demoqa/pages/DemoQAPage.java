package com.demoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DemoQAPage {
    private final WebDriver driver;

    private final By firstNameInput = By.id("firstName");
    private final By lastNameInput = By.id("lastName");
    private final By emailInput = By.id("email");
    private final By ageInput = By.id("age");
    private final By countryDropdown = By.id("country");
    private final By skillsDropdown = By.id("skills");
    private final By newsletterCheckbox = By.id("newsletter");
    private final By agreeCheckbox = By.id("agree");
    private final By uploadInput = By.id("uploadFile");
    private final By submitButton = By.id("submitBtn");
    private final By statusButton = By.id("statusBtn");
    private final By resultMessage = By.id("result");

    public DemoQAPage(WebDriver driver) {
        this.driver = driver;
    }

    public void openApp() {
        Path appPath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "demo-app.html").toAbsolutePath().normalize();
        driver.get(appPath.toUri().toString());
    }

    public void fillForm(String firstName, String lastName, String email, String age, String country, List<String> skills) {
        driver.findElement(firstNameInput).sendKeys(firstName);
        driver.findElement(lastNameInput).sendKeys(lastName);
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(ageInput).sendKeys(age);
        Select countrySelect = new Select(driver.findElement(countryDropdown));
        countrySelect.selectByVisibleText(country);
        Select skillsSelect = new Select(driver.findElement(skillsDropdown));
        skillsSelect.deselectAll();
        for (String skill : skills) {
            skillsSelect.selectByVisibleText(skill);
        }
    }

    public void selectCheckBoxes(boolean subscribe, boolean agree) {
        if (subscribe) driver.findElement(newsletterCheckbox).click();
        if (agree) driver.findElement(agreeCheckbox).click();
    }

    public void uploadFile(String filePath) {
        driver.findElement(uploadInput).sendKeys(filePath);
    }

    public void submit() {
        driver.findElement(submitButton).click();
    }

    public void clickStatusButton() {
        driver.findElement(statusButton).click();
    }

    public String getResultText() {
        return driver.findElement(resultMessage).getText();
    }
}
