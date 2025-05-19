package com.b44t.messenger.appium.pages;

import com.b44t.messenger.appium.base.BaseTest;

import org.openqa.selenium.OutputType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScreenshotBlocked {

    public static boolean isScreenshotBlocked(BaseTest base, String user) {
        try {
            // Take screenshot as file
            File screenshot = base.driver.getScreenshotAs(OutputType.FILE);

            // Decode to BufferedImage (JVM alternative to Android Bitmap)
            BufferedImage image = ImageIO.read(screenshot);

            if (image == null || image.getWidth() == 0 || image.getHeight() == 0) {
                System.out.println("❌ Screenshot blocked for " + user + " (image is null or empty)");
                return true;
            }

            boolean isBlack = false;
            int width = image.getWidth();
            int height = image.getHeight();
            outerLoop:
            for (int x = 0; x < width; x += 10) {
                for (int y = 0; y < height; y += 10) {
                    if ((image.getRGB(x, y) & 0xFFFFFF) != 0) {
                        isBlack = true;
                        break outerLoop;
                    }
                }
            }

            if (isBlack) {
                System.out.println("❌ Screenshot blocked for " + user + " (image is all black)");
                return true;
            }

            System.out.println("✅ Screenshot captured successfully for " + user);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Screenshot blocked for " + user + " due to exception: " + e.getMessage());
            return true;
        }
    }
}
