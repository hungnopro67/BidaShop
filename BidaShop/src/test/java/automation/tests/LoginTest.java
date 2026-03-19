package automation.tests;

import automation.base.BaseTest;
import automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void TC01_validPhone() {

        LoginPage login = new LoginPage(driver, wait);

        login.openHomePage();
        login.closePopupIfPresent();
        login.clickLogin();
        login.enterPhone("0901234777");
        login.clickContinue();

        String otpText = login.getOtpMessage();
        Assert.assertTrue(otpText.contains("Mã xác nhận đã được gửi"));
    }

    @Test
    public void TC02_lessThan10Digits() {

        LoginPage login = new LoginPage(driver, wait);

        login.openHomePage();
        login.closePopupIfPresent();
        login.clickLogin();
        login.enterPhone("09123");
        login.clickContinue();

        String error = login.getErrorMessage();
        Assert.assertEquals(error, "Số điện thoại không hợp lệ");
    }

    @Test
    public void TC03_containsLetter() {

        LoginPage login = new LoginPage(driver, wait);

        login.openHomePage();
        login.closePopupIfPresent();
        login.clickLogin();
        login.enterPhone("09abc12345");
        login.clickContinue();

        String error = login.getErrorMessage();
        Assert.assertEquals(error, "Số điện thoại không hợp lệ");
    }

    @Test
    public void TC04_emptyPhone() {

        LoginPage login = new LoginPage(driver, wait);

        login.openHomePage();
        login.closePopupIfPresent();
        login.clickLogin();
        login.enterPhone("");
        login.clickContinue();

        String error = login.getErrorMessage();
        Assert.assertEquals(error, "Vui lòng nhập số điện thoại");
    }

    @Test
    public void TC05_specialCharacter() {

        LoginPage login = new LoginPage(driver, wait);

        login.openHomePage();
        login.closePopupIfPresent();
        login.clickLogin();
        login.enterPhone("09012###89");
        login.clickContinue();

        String error = login.getErrorMessage();
        Assert.assertEquals(error, "Số điện thoại không hợp lệ");
    }
}