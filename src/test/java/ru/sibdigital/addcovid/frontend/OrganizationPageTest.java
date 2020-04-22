package ru.sibdigital.addcovid.frontend;


import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.sibdigital.addcovid.frontend.pages.OrganizationAddPage;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "server.port=8091", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OrganizationPageTest {

    List<Character> chars = new ArrayList<>(70);



    @LocalServerPort
    private int port;
    final String protocol = "http";
    final String host = "localhost";
    String baseUrl;
    @Value("${webdriver.chrome.driver}")
    String chromeDriverLocation; //Берем отсюда https://chromedriver.storage.googleapis.com/index.html?path=81.0.4044.69/
    WebDriver driver;

    public OrganizationPageTest() {
        for (int i = 'А'; i < 'Я'; i++) {
            chars.add((char) i);
        }

        for (int i = 'а'; i < 'я'; i++) {
            chars.add((char) i);
        }
    }

    @PostConstruct
    public void initUrl(){
        this.baseUrl = String.format("%s://%s:%d", this.protocol, this.host, this.port);
        System.setProperty("webdriver.chrome.driver",chromeDriverLocation);
        this.driver = new ChromeDriver();
    }

    private String generateWord(int maximumNumberOfChars){

        int random_number1 = 1 + (int) (Math.random() * maximumNumberOfChars);

        StringBuilder word = new StringBuilder();

        for (int i = 0; i < random_number1; i++) {
            int random_char_index = 0 + (int) (Math.random() * this.chars.size()-1);

            word.append(chars.get(random_char_index));
        }

        return word.toString();
    }

    @Test
    public void testingScenarion(){
        OrganizationAddPage organizationAddPage = new OrganizationAddPage(driver, baseUrl);

        Assertions.assertTrue(organizationAddPage.getSendBtn().isDisabled());




        organizationAddPage.getFirstname().setText(generateWord(99));
        organizationAddPage.getLastname().setText(generateWord(99));
        organizationAddPage.getPatronymic().setText(generateWord(99));
        organizationAddPage.getAddPersonsBtn().submit();

        Assertions.assertTrue(organizationAddPage.getSendBtn().isDisabled());

        organizationAddPage.getIsAgree().setChecked(true);

        Assertions.assertTrue(organizationAddPage.getSendBtn().isDisabled());

        organizationAddPage.getIsProtect().setChecked(true);

        Assertions.assertTrue(organizationAddPage.getSendBtn().isDisabled()); // По правилу -  кнопка отправить должна стать доступной

        //Так как предыдущий Assert не прошел проверку, нижний код уже исполняться не будет

        organizationAddPage.getFirstname().setText(generateWord(99));
        organizationAddPage.getLastname().setText(generateWord(99));
        organizationAddPage.getPatronymic().setText(generateWord(99));
        organizationAddPage.getAddPersonsBtn().submit();

        organizationAddPage.getFirstname().setText(generateWord(99));
        organizationAddPage.getLastname().setText(generateWord(99));
        organizationAddPage.getPatronymic().setText(generateWord(99));
        organizationAddPage.getAddPersonsBtn().submit();










    }


    @After
    public void closeWebBrowserAfterTests() {
        //
        // this.driver.quit();
    }


}
