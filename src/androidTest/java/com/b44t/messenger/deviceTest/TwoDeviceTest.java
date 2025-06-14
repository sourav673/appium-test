package deviceTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.b44t.messenger.base.BaseTest;
import io.appium.java_client.AppiumBy;
import pages.InvitePage;
import pages.LoginPage;
import pages.ScreenshotBlocked;

public class TwoDeviceTest {
    private static volatile String sharedInviteLink = null;


    public static void main(String[] args) {
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

    public static void runUser1(String udid, int port) {
        BaseTest base = new BaseTest();
        try {
            base.setupDriver(udid, port);
            LoginPage login = new LoginPage(base.driver);
            InvitePage invite = new InvitePage(base.driver);

            login.login("User1");
            sharedInviteLink = invite.generateInviteLink();
            System.out.println("✅ User1 generated invite: " + sharedInviteLink);

            // Wait for User2's message
            boolean messageReceived = false;
            int retries = 20;
            while (retries-- > 0) {
                try {
                    base.driver.findElement(By.xpath(
                            "//androidx.recyclerview.widget.RecyclerView[@resource-id=\"chat.delta.privitty:id/list\"]/android.widget.RelativeLayout[1]"
                    )).click();
                    WebElement messageBubble = base.driver.findElement(By.xpath(
                            "//android.widget.TextView[@text='Hello from User2']"
                    ));
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
                            "//android.widget.TextView[contains(@text,'Privitty secure')]"
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
            base.teardownDriver();
        }
    }

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

            // Send a message to User1
            base.driver.findElement(AppiumBy.accessibilityId("Message")).sendKeys("Hello from User2");
            base.driver.findElement(AppiumBy.accessibilityId("Send")).click();
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/attach_button")).click();
            System.out.println("📨 User2 sent message to User1.");

            boolean isBlocked = ScreenshotBlocked.isScreenshotBlocked(base, "User2");
            if (isBlocked) {
                System.out.println("🔒 Screenshot is blocked as expected.");
            } else {
                System.out.println("⚠️ Screenshot is NOT blocked! This may be a security issue.");
            }

            Thread.sleep(8000);
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/attach_button")).click();
            Thread.sleep(2000);
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/cbDownload"));
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/etAllowTime")).sendKeys("1");
            base.driver.findElement(AppiumBy.id("android:id/button1")).click();
            Thread.sleep(1000);
            base.driver.findElement(AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.google.android.documentsui:id/icon_thumb\"])[1]")).click();
            Thread.sleep(1000);
            base.driver.findElement(AppiumBy.accessibilityId("Send")).click();

//            Delete Chat and Rejoin
            Thread.sleep(4000);
            base.driver.findElement(AppiumBy.accessibilityId("More options")).click();
            Thread.sleep(1000);
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/submenuarrow")).click();
            base.driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"Delete Chat\")")).click();
            base.driver.findElement(AppiumBy.id("android:id/button1")).click();
            base.driver.findElement(AppiumBy.accessibilityId("Navigate up")).click();
            Thread.sleep(2000);
            invite.joinViaInviteLink(sharedInviteLink);

            base.driver.findElement(AppiumBy.accessibilityId("More options")).click();
            Thread.sleep(1000);
            base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/submenuarrow")).click();
            base.driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\"Delete Chat\")")).click();
            base.driver.findElement(AppiumBy.id("android:id/button1")).click();
            base.driver.findElement(AppiumBy.accessibilityId("Navigate up")).click();
            Thread.sleep(2000);
            invite.joinViaInviteLink(sharedInviteLink);

        } catch (Exception e) {
            System.out.println("❌ User2 error: " + e.getMessage());
        } finally {
            base.teardownDriver();
        }
    }
}
