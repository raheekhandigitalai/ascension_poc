package com.ascension;

import com.experitest.appium.SeeTestClient;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.testng.annotations.Listeners;
import com.experitest.reporter.testng.Listener;

@Listeners(Listener.class)
public class ReporterListenerTest {

    private String accessKey = "";
    protected IOSDriver<IOSElement> driver = null;
    DesiredCapabilities dc = new DesiredCapabilities();
    SeeTestClient client;

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        dc.setCapability("accessKey", accessKey);
        dc.setCapability("deviceQuery", "@os='ios' and @category='PHONE'");
        dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
        dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
        driver = new IOSDriver<>(new URL("https://uscloud.experitest.com/wd/hub"), dc);
        client = new SeeTestClient(driver);
    }

    @Test
    public void quickStartiOSNativeDemo() {
        driver.rotate(ScreenOrientation.PORTRAIT);
        driver.findElement(By.xpath("//*[@id='usernameTextField']")).sendKeys("company");
        driver.findElement(By.xpath("//*[@id='passwordTextField']")).sendKeys("company");
        driver.findElement(By.xpath("//*[@id='loginButton']")).click();

        By makePaymentButton = By.id("makePayment");

        new WebDriverWait(driver, 10).pollingEvery(Duration.ofSeconds(2)).until(ExpectedConditions.elementToBeClickable(makePaymentButton));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {

//        if (result.isSuccess()) {
//            driver.executeScript("seetest:client.setReportStatus(\"Passed\", \"Test Passed\")");
//        } else if (!result.isSuccess()) {
//            driver.executeScript("seetest:client.setReportStatus(\"Failed\", \"Test Failed\")");
//        }

        driver.quit();
    }

}
