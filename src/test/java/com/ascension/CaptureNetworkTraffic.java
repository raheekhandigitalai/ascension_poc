package com.ascension;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class CaptureNetworkTraffic {

    private String accessKey = "";
    protected AppiumDriver driver = null;
    DesiredCapabilities dc = new DesiredCapabilities();

    String platformName = "iOS";

    @BeforeMethod
    public void setUp() throws MalformedURLException {

        dc.setCapability("accessKey", accessKey);

        if (platformName.equalsIgnoreCase("iOS")) {

            dc.setCapability("deviceQuery", "@os='android' and @category='PHONE' and contains(@version, '9')");
            dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank/.LoginActivity");
            dc.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.experitest.ExperiBank");
            dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".LoginActivity");
            driver = new AndroidDriver<>(new URL("https://uscloud.experitest.com/wd/hub"), dc);

        } else if (platformName.equalsIgnoreCase("Android")) {

            dc.setCapability("deviceQuery", "@os='ios' and @category='PHONE'");
            dc.setCapability("udid", "f58b0eeba77f6e50aeb15d60b420604a94f0dec9");
            dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
            dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
            driver = new AndroidDriver<>(new URL("https://uscloud.experitest.com/wd/hub"), dc);

        }

    }

    @Test
    public void testing_01() throws Exception {

        // https://docs.experitest.com/display/TE/StartPerformanceTransaction
        driver.executeScript("seetest:client.startPerformanceTransaction(\"NONE\")");

        driver.findElement(By.id("usernameTextField")).sendKeys("company");
        driver.findElement(By.id("passwordTextField")).sendKeys("company");
        driver.findElement(By.id("loginButton")).click();

        By makePaymentButton = By.id("makePaymentButton");
        new WebDriverWait(driver, 10).pollingEvery(Duration.ofSeconds(2)).until(ExpectedConditions.elementToBeClickable(makePaymentButton));

        // https://docs.experitest.com/display/TE/EndPerformanceTransaction
        Object o = driver.executeScript("seetest:client.endPerformanceTransaction(\"Login_ExperiBank_Rahee\")");
        System.out.println(o.toString());

        String[] array = o.toString().split(",");

        String[] transactionId = {};

        transactionId = array[15].split(":");
        System.out.println(transactionId[1]);

        // https://docs.experitest.com/display/TE/Rest+API+-+Transactions#RestAPITransactions-DownloadHARfile
        downloadHarFile(Long.parseLong(transactionId[1]));
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    public void downloadHarFile(long transactionId) throws Exception {
        String url = "https://uscloud.experitest.com/api/transactions/%d/har";

        url = String.format(url, transactionId);
        HttpResponse<InputStream> response = Unirest.get(url)
                .queryString("token", accessKey)
                .asBinary();
        int status = response.getStatus();
        Assert.assertEquals(200, status);
        if (status == 200) {
            List<String> disposition = response.getHeaders().get("Content-Disposition");
            String fileName = "harfile.json";
            if (disposition != null && !disposition.isEmpty()) {
                fileName = disposition.get(0).split("=")[1];
            }
//            fileName = "/tmp/" + fileName;
            fileName = System.getProperty("user.dir") + "\\har\\" + fileName;
            saveToFile(response.getBody(), fileName);
            Assert.assertTrue(new File(fileName).exists());
        }
    }

    static void saveToFile(InputStream in, String fileName) throws IOException {
        try (OutputStream out = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int readCount;
            while ((readCount = in.read(buffer)) != -1) {
                out.write(buffer, 0, readCount);
            }
        }
    }

}
