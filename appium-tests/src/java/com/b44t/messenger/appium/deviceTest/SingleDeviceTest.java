 package com.b44t.messenger.appium.deviceTest;

 import org.openqa.selenium.By;
 import org.openqa.selenium.WebElement;
 import org.openqa.selenium.support.ui.ExpectedConditions;
 import org.openqa.selenium.support.ui.WebDriverWait;

 import com.b44t.messenger.appium.base.BaseTest;
 import io.appium.java_client.AppiumBy;
 import com.b44t.messenger.appium.pages.InvitePage;
 import com.b44t.messenger.appium.pages.LoginPage;
 import com.b44t.messenger.appium.pages.GroupPage;
 import com.b44t.messenger.appium.pages.ScreenshotBlocked;

 import java.time.Duration;


 public class SingleDeviceTest extends BaseTest {
     public static void main(String[] args) {
         Thread user1 = new Thread(() -> runUser1("ZD2226SZNF", 4723), "User1Thread");

         user1.start();

         try {
             user1.join();
         } catch (InterruptedException e) {
             System.out.println("❌ Thread interrupted: " + e.getMessage());
         }
     }

     public static void runUser1(String udid, int port) {
         BaseTest base = new BaseTest();
         try {
             base.setupDriver(udid, port);
             LoginPage login = new LoginPage(base.driver);
             login.login("User1");
    
             String[] groups = {"Group 1", "Group 2"};
             String[] messages = {
                 "Hello Group Members",
                 "Hello Everyone Group Members",
                 "Hello.. How are you Group Members",
                 "Hello.. I Hope Everyone is fine Group Members"
             };
    
             GroupPage groupPage = new GroupPage(base.driver);
    
             for (String group : groups) {
                 groupPage.createGroup(group);
                 groupPage.sendMessages(messages);
                 groupPage.navigateBackToMain();
             }
    
             System.out.println("✅ All groups created and messages sent.");
    
         } catch (Exception e) {
             System.out.println("❌ User1 error: " + e.getMessage());
         } finally {
             base.teardownDriver();
         }
     }

 }
