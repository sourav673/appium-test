package com.b44t.messenger.appium.pages;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.clipboard.HasClipboard;

public class InvitePage {
  AndroidDriver driver;

  public InvitePage(AndroidDriver driver) {
    this.driver = driver;
  }

  public String generateInviteLink() {
    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.elementToBeClickable(
        AppiumBy.id("chat.delta.privitty:id/menu_qr")
      )).click();

      wait.until(ExpectedConditions.elementToBeClickable(
        AppiumBy.id("chat.delta.privitty:id/share_link_button")
      )).click();

      WebElement inviteCode = wait.until(ExpectedConditions.presenceOfElementLocated(
        AppiumBy.id("chat.delta.privitty:id/invite_link")
      ));
      return inviteCode.getText();
    } catch (Exception e) {
      throw new RuntimeException("❌ Failed to generate invite link: " + e.getMessage());
    }
  }

  public void joinViaInviteLink(String link) {
    if (link == null || link.trim().isEmpty()) {
      throw new IllegalArgumentException("❗ Invite link is null or empty.");
    }

    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

      wait.until(ExpectedConditions.elementToBeClickable(
        AppiumBy.id("chat.delta.privitty:id/menu_qr")
      )).click();
      wait.until(ExpectedConditions.elementToBeClickable(
        AppiumBy.accessibilityId("More options")
      )).click();
      System.out.println("📥 User2 got invite link: " + link);
      ((HasClipboard) driver).setClipboardText(link);

      wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator("new UiSelector().text(\"Paste from Clipboard\")"))).click();

      wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id("android:id/button1"))).click();

      // ========================================
      // Send message 1
      // ========================================
      wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Message"))).sendKeys("Hello");
      wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Send"))).click();
      // =========================================
      // Send message 2
      // =========================================
      wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Message"))).sendKeys("How are you?");
      wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Send"))).click();
      System.out.println("📨 User2 sent message to User1.");

    } catch (Exception e) {
      throw new RuntimeException("❌ Failed to join via invite link: " + e.getMessage());
    }
  }
}
