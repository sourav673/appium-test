package com.b44t.messenger.appium.base;

import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

public class BaseTest {
    public AndroidDriver driver;

    public void setupDriver(String deviceName, String osVersion, String appUrl) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        
        caps.setCapability("browserstack.user", "swa_TtVXzOfwV54");
        caps.setCapability("browserstack.key", "TgSazzF4ms6zyc1Cv7M7");
        caps.setCapability("device", deviceName);
        caps.setCapability("os_version", osVersion);
        caps.setCapability("platformName", "Android");
        caps.setCapability("app", appUrl);

        driver = new AndroidDriver(new URL("https://hub.browserstack.com/wd/hub"), caps);
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
