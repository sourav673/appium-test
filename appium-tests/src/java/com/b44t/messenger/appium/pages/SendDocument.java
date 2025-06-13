//package com.b44t.messenger.appium.pages;
//
//import com.b44t.messenger.appium.base.BaseTest;
//
//import org.openqa.selenium.OutputType;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//
//public class SendFile {
//
//  public static boolean sendFile() {
//    try {
//      wait.until(ExpectedConditions.elementToBeClickable(
//        By.id("chat.delta.privitty:id/attach_button")
//      )).click();
//      wait.until(ExpectedConditions.elementToBeClickable(
//        AppiumBy.id("chat.delta.privitty:id/cbDownload")
//      )).click();
//      base.driver.findElement(AppiumBy.id("chat.delta.privitty:id/etAllowTime")).sendKeys("1");
//      base.driver.findElement(AppiumBy.id("android:id/button1")).click();
//      wait.until(ExpectedConditions.elementToBeClickable(
//        AppiumBy.xpath("(//android.widget.ImageView[@resource-id=\"com.google.android.documentsui:id/icon_thumb\"])[2]")
//      )).click();
//      wait.until(ExpectedConditions.elementToBeClickable(
//        AppiumBy.accessibilityId("Send")
//      )).click();
//    } catch (Exception e) {
//      System.out.println("❌ Screenshot blocked for " + user + " due to exception: " + e.getMessage());
//      return true;
//    }
//  }
//}
