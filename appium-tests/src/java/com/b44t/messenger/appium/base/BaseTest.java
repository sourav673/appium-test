package com.b44t.messenger.appium.base;

import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

public class BaseTest {
    public AndroidDriver driver;

//    public void setupDriver(String deviceName, String osVersion, String appUrl) throws Exception {
//        DesiredCapabilities caps = new DesiredCapabilities();
//
//        caps.setCapability("browserstack.user", "swa_TtVXzOfwV54");
//        caps.setCapability("browserstack.key", "TgSazzF4ms6zyc1Cv7M7");
//        caps.setCapability("device", deviceName);
//        caps.setCapability("os_version", osVersion);
//        caps.setCapability("platformName", "Android");
//        caps.setCapability("app", appUrl);
//
//        driver = new AndroidDriver(new URL("https://hub.browserstack.com/wd/hub"), caps);
//    }
public void setupDriver(String udid, int port) throws Exception {
  DesiredCapabilities cap = new DesiredCapabilities();
  cap.setCapability("appium:autoGrantPermissions", true);
  cap.setCapability("platformName", "Android");
  cap.setCapability("appium:udid", udid);
  cap.setCapability("appium:deviceName", udid);

  // Assign unique systemPort based on Appium server port
  int systemPort;
  switch (port) {
    case 4723:
      systemPort = 8200;
      break;
    case 4725:
      systemPort = 8201;
      break;
    case 4727:
      systemPort = 8202;
      break;
    default:
      throw new IllegalArgumentException("Unsupported port: " + port);
  }
  cap.setCapability("appium:systemPort", port == 4723 ? 8200 : 8201);

  cap.setCapability("appium:automationName", "UiAutomator2");
  cap.setCapability("appium:appPackage", "chat.delta.privitty");
  cap.setCapability("appium:appActivity", "org.thoughtcrime.securesms.RoutingActivity");

  driver = new AndroidDriver(new URL("http://127.0.0.1:" + port + "/"), cap);
}

    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error while quitting driver: " + e.getMessage());
            }
        }
    }

    public void teardownDriver() {

    }
}
