package ru.sibdigital.addcovid.frontend;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrganizationPageTest {
    @Value("${local.server.port:8090}")
    String port;
    final String protocol = "http";
    final String host = "localhost";
    String url;
    @Value("${webdriver.chrome.driver}")
    String chromeDriverLocation; //Берем отсюда https://chromedriver.storage.googleapis.com/index.html?path=81.0.4044.69/

    @PostConstruct
    public void initUrl(){
        this.url = String.format("%s://%s:%d", this.protocol, this.host, this.port);
    }







    @Test
    public void testingScenarion(){
        System.setProperty("webdriver.chrome.driver",chromeDriverLocation);
        WebDriver driver = new ChromeDriver();
        driver.get(this.url+"/");
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();



    }





}
