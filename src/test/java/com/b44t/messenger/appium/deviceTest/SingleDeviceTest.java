package deviceTest;

import base.BaseTest;
import pages.GroupPage;
import pages.LoginPage;


public class SingleDeviceTest extends BaseTest {
    public void setup() throws Exception {
        // Replace with your device's UDID and Appium port (4723 for default)
        setupDriver("emulator-5554", 4725);
    }

    public void testGroupCreationAndMessaging() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("User1");

        GroupPage groupPage = new GroupPage(driver);
        groupPage.createGroup("Test Group");

        String[] messages = {"Hello group!", "Testing automation"};
        groupPage.sendMessages(messages);

        groupPage.navigateBackToMain();
    }

    public void tearDownTest() {
        tearDown();
    }
}