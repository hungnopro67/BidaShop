package automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // ===== Popup quảng cáo =====
    private By popupCloseBtn = By.cssSelector("div.icon-close-popup");

    // ===== Nút Đăng nhập trên header =====
    private By btnLogin = By.cssSelector("a[href='/lich-su-mua-hang']");

    // ===== Form đăng nhập =====
    private By txtPhone = By.cssSelector("input[type='tel']");
    private By btnContinue = By.xpath("//button[contains(text(),'Tiếp tục')]");

    private By errorMessage = By.xpath("//*[contains(text(),'Số điện thoại') or contains(text(),'Vui lòng')]");
    private By otpMessage = By.xpath("//*[contains(text(),'Mã xác nhận')]");

    public void openHomePage() {
        driver.get("https://www.dienmayxanh.com/");
    }

    public void closePopupIfPresent() {
        try {
            WebElement closeBtn = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(popupCloseBtn)
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
        } catch (Exception ignored) {
        }
    }

    public void clickLogin() {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(btnLogin)
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void enterPhone(String phone) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(txtPhone)).clear();
        driver.findElement(txtPhone).sendKeys(phone);
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(btnContinue)).click();
    }

    public String getErrorMessage() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(errorMessage)
        ).getText();
    }

    public String getOtpMessage() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(otpMessage)
        ).getText();
    }
}