package com.b44t.messenger.appium.pages;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class GroupPage {
    AndroidDriver driver;

    public GroupPage(AndroidDriver driver) {
        this.driver = driver;
    }

    public void createGroup(String groupName) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id("chat.delta.privitty:id/fab"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().text(\"New Group\")"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id("chat.delta.privitty:id/group_name"))).sendKeys(groupName);

        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Create Group"))).click();
    }

    public void sendMessages(String[] messages) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    for (String msg : messages) {
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Message"))).sendKeys(msg);
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Send"))).click();
        // Optional: wait for message to appear (can be enhanced)
    }
    }

    public void navigateBackToMain() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id("chat.delta.privitty:id/up_button"))).click();
    }
}
