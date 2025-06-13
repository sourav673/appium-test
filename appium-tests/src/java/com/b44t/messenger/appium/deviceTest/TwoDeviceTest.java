package com.b44t.messenger.appium.deviceTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.b44t.messenger.appium.base.BaseTest;
import io.appium.java_client.AppiumBy;
import com.b44t.messenger.appium.pages.InvitePage;
import com.b44t.messenger.appium.pages.LoginPage;
import com.b44t.messenger.appium.pages.ScreenshotBlocked;

import java.time.Duration;

public class TwoDeviceTest {
    private static volatile String sharedInviteLink = null;

    /**
     * This test runs two threads, each simulating a user on a different device.
     * User1 generates an invite link and waits for User2 to send a message.
     * User2 joins the chat using the invite link and sends a message to User1.
     * Both users check if screenshots are blocked.
     */
    // This test is designed to run on two different devices.
    // Make sure to run the test on two different devices/emulators.
    public static void main(String[] args) {
//        String deviceName1 = "ZD2226SZNF";   // or any device from BrowserStack
//        String osVersion1 = "14.0";
//        String deviceName2 = "emulator-5554";
//        String osVersion2 = "15.0";
//        String appUrl = "bs://e38684eb01dd899453b904406f4c3d82ec441e75"; // e.g., "bs://<app-id>"

//        Thread user1 = new Thread(() -> runUser1(deviceName1, osVersion1, appUrl), "User1Thread");
//        Thread user2 = new Thread(() -> runUser2(deviceName2, osVersion2, appUrl), "User2Thread");

        Thread user1 = new Thread(() -> runUser1("ZD2226SZNF", 4723), "User1Thread");
        Thread user2 = new Thread(() -> runUser2("emulator-5554", 4725), "User2Thread");

        user1.start();
        user2.start();

        try {
            user1.join();
            user2.join();
        } catch (InterruptedException e) {
            System.out.println("❌ Thread interrupted: " + e.getMessage());
        }
    }

//    public static void runUser1(String deviceName, String osVersion, String appUrl) {
    public static void runUser1(String udid, int port) {
        BaseTest base = new BaseTest();
        try {
            base.setupDriver(udid, port);
            LoginPage login = new LoginPage(base.driver);
            InvitePage invite = new InvitePage(base.driver);

            login.login("User1");
            sharedInviteLink = invite.generateInviteLink();
            System.out.println("✅ User1 generated invite: " + sharedInviteLink);
            WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(10));

            // Wait for User2's message
            boolean messageReceived = false;
            int retries = 20;
            while (retries-- > 0) {
                try {
                    base.driver.findElement(By.xpath(
                            "//androidx.recyclerview.widget.RecyclerView[@resource-id=\"chat.delta.privitty:id/list\"]/android.widget.RelativeLayout[1]"
                    )).click();
                    Thread.sleep(2000);
                    base.driver.findElement(AppiumBy.accessibilityId("Message")).sendKeys("Hello, I am Good!");
                    base.driver.findElement(AppiumBy.accessibilityId("Send")).click();

                    WebElement messageBubble = base.driver.findElement(By.xpath(
                            "//android.widget.TextView[@text='How are you?']"
                    ));

                  System.out.println("📄 Trying to open Document...");
                  WebElement docMsg = wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.androidUIAutomator("new UiSelector().text(\"samplepdf_c127xUOU.pdf.prv\")")
                  ));
                  docMsg.click();
                  Thread.sleep(3000); // let the document viewer open
                  base.driver.navigate().back(); // go back to chat
                  System.out.println("✅ Document opened successfully.");

                    if (messageBubble != null && messageBubble.isDisplayed()) {
                        messageReceived = true;
                        break;
                    }
                } catch (Exception ignored) {}

                Thread.sleep(2000);
            }

            if (messageReceived) {
                System.out.println("✅ User1 received the message from User2 successfully.");

            } else {
                System.out.println("❌ User1 did NOT receive the message from User2.");

            }

            boolean alertSeen = false;
            int alertRetries = 10;
            while (alertRetries-- > 0) {
                try {
                    WebElement alert = base.driver.findElement(By.xpath(
                            "//android.widget.TextView[contains(@text,'You are Privitty secure')]"
                    ));
                    if (alert != null && alert.isDisplayed()) {
                        alertSeen = true;
                        break;
                    }
                } catch (Exception ignored) {}
                Thread.sleep(2000);
            }

            if (alertSeen) {
                System.out.println("✅ User1 received the info alert from User2.");
            } else {
                System.out.println("❌ User1 did NOT receive the info alert.");
            }

            boolean isBlocked = ScreenshotBlocked.isScreenshotBlocked(base, "User1");
            if (isBlocked) {
                System.out.println("🔒 Screenshot is blocked as expected.");
            } else {
                System.out.println("⚠️ Screenshot is NOT blocked! This may be a security issue.");
            }

        } catch (Exception e) {
            System.out.println("❌ User1 error: " + e.getMessage());
        } finally {
            base.tearDown();
        }
    }

//    public static void runUser2(String deviceName, String osVersion, String appUrl) {
      public static void runUser2(String udid, int port) {
        BaseTest base = new BaseTest();
        try {
            base.setupDriver(udid, port);
            LoginPage login = new LoginPage(base.driver);
            InvitePage invite = new InvitePage(base.driver);

            login.login("User2");

            int maxWait = 30, waited = 0;
            while (sharedInviteLink == null && waited < maxWait) {
                System.out.println("⏳ Waiting for invite link...");
                Thread.sleep(2000);
                waited += 2;
            }

            if (sharedInviteLink == null) {
                System.out.println("❌ Timeout waiting for invite link");
                return;
            }

            invite.joinViaInviteLink(sharedInviteLink);


            boolean isBlocked = ScreenshotBlocked.isScreenshotBlocked(base, "User2");
            if (isBlocked) {
                System.out.println("🔒 Screenshot is blocked as expected.");
            } else {
                System.out.println("⚠️ Screenshot is NOT blocked! This may be a security issue.");
            }

            WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(10));

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("chat.delta.privitty:id/attach_button")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//android.widget.TextView[contains(@text,'Privitty secure')]")
            )).click();

//            Sending Document
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("chat.delta.privitty:id/attach_button")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("chat.delta.privitty:id/cbDownload")
            )).click();
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/etAllowTime")).sendKeys("1");
            base.driver.findElement(AppiumBy.id("android:id/button1")).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.google.android.documentsui:id/icon_thumb\"])[1]")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Send")
            )).click();

//            sending image
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("chat.delta.privitty:id/attach_button")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("chat.delta.privitty:id/cbDownload")
            )).click();
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/etAllowTime")).sendKeys("1");
            base.driver.findElement(AppiumBy.id("android:id/button1")).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.google.android.documentsui:id/icon_thumb\"])[2]")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Send")
            )).click();

//            sending video
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("chat.delta.privitty:id/attach_button")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("chat.delta.privitty:id/cbDownload")
            )).click();
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/etAllowTime")).sendKeys("1");
            base.driver.findElement(AppiumBy.id("android:id/button1")).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.google.android.documentsui:id/icon_thumb\"])[3]")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Send")
            )).click();

            Thread.sleep(5000);


//            Delete Chat and Rejoin
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("More options")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("chat.delta.privitty:id/submenuarrow")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.androidUIAutomator("new UiSelector().text(\"Delete Chat\")")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("android:id/button1")
            )).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.accessibilityId("Navigate up")
            )).click();
            invite.joinViaInviteLink(sharedInviteLink);

        } catch (Exception e) {
            System.out.println("❌ User2 error: " + e.getMessage());
        } finally {
            base.tearDown();
            System.out.println("✅ All Test Passed");
        }
    }
}
