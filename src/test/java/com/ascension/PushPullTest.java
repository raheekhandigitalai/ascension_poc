package com.ascension;

import com.experitest.appium.SeeTestClient;
import com.experitest.reporter.testng.Listener;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.openqa.selenium.By;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Listeners(Listener.class)
public class PushPullTest {

    private String accessKey = "eyJ4cC51Ijo3MzU0MjQsInhwLnAiOjIsInhwLm0iOiJNVFUzT0RZd016ZzFOek16TVEiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4OTM5NjM4NTcsImlzcyI6ImNvbS5leHBlcml0ZXN0In0.GP0hK0o0j2WEKt-J0aXsVbu1tmt-PhWUryqluokszJk";
    protected AndroidDriver<AndroidElement> driver = null;
    protected DesiredCapabilities dc = new DesiredCapabilities();

    protected String messageToSend = "PushNotificationTest";

    @BeforeMethod
    public void setUp(Method method) throws MalformedURLException {
        dc.setCapability("testName", method.getName());
        dc.setCapability("accessKey", accessKey);
//        dc.setCapability("deviceQuery", "@os='android' and @category='PHONE'");
        dc.setCapability("udid", "988b5c474a304e334b30");
        dc.setCapability("dontGoHomeOnQuit", true);
        driver = new AndroidDriver<>(new URL("https://uscloud.experitest.com/wd/hub"), dc);
    }

    @Test
    public void push_pull_scenario() throws InterruptedException {
        driver.executeScript("seetest:client.launch(\"com.google.android.apps.googlevoice/com.google.android.apps.voice.home.HomeActivity\", \"false\", \"true\")");

        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.id("og_apd_internal_image_view")));

        List<AndroidElement> items = driver.findElements(By.id("conversationlist_item_conversation_name"));

        for (AndroidElement item : items) {
            if (item.getAttribute("text").contains("(831) 704-6297")) {
                item.click();
                break;
            }
        }

        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.id("send_message_text")));
        driver.findElement(By.id("send_message_text")).sendKeys(messageToSend);
        driver.findElement(By.id("send_message_button")).click();

        driver.executeScript("seetest:client.deviceAction(\"Home\")");

        new WebDriverWait(driver, 20).pollingEvery(Duration.ofSeconds(2)).until(ExpectedConditions.elementToBeClickable(By.id("notification_messaging")));
        driver.findElement(By.id("notification_messaging")).click();

        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.id("send_message_text")));

        assertEquals(driver.findElement(By.xpath("(//*[@id='message_text'])[last()]")).getAttribute("text").trim(), messageToSend);

        Thread.sleep(15000);
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Report URL: "+ driver.getCapabilities().getCapability("reportUrl"));
        driver.quit();
    }
}
