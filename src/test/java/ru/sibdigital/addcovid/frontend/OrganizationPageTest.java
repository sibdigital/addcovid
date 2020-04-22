package ru.sibdigital.addcovid.frontend;


import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import net.lightbody.bmp.proxy.CaptureType;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.sibdigital.addcovid.frontend.pages.OrganizationAddPage;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
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
    BrowserMobProxy proxy;

    public OrganizationPageTest() {
        for (int i = 'А'; i < 'Я'; i++) {
            chars.add((char) i);
        }
        chars.add(' ');
        for (int i = 'а'; i < 'я'; i++) {
            chars.add((char) i);
        }
    }

    @PostConstruct
    public void initUrl(){
        this.baseUrl = String.format("%s://%s:%d", this.protocol, this.host, this.port);
        //System.setProperty("webdriver.chrome.driver",chromeDriverLocation);
        // start the proxy
        proxy = new BrowserMobProxyServer();
        proxy.setMitmManager(ImpersonatingMitmManager.builder().trustAllServers(true).build());
        proxy.addRequestFilter((httpRequest, httpMessageContents, httpMessageInfo) -> {
            log.info(httpMessageInfo.getOriginalRequest().method().name());
            return null;
        });

        proxy.start(0);



        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        try {
            String hostIp = Inet4Address.getLocalHost().getHostAddress();
            log.info(hostIp);
            seleniumProxy.setHttpProxy(hostIp + ":" + proxy.getPort());
            seleniumProxy.setSslProxy(hostIp + ":" + proxy.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // configure it as a desired capability
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

        ChromeOptions options = new ChromeOptions();
        options.merge(capabilities);

        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(chromeDriverLocation))
                .usingAnyFreePort()
                .build();
        this.driver = new ChromeDriver(service, options);
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

    private String generateNumberSequence(int sizeOfSequence){
        StringBuilder sequence = new StringBuilder();

        for (int i = 0; i < sizeOfSequence; i++) {
            int random_number = 1 + (int) (Math.random() * 10);

            sequence.append(random_number);
        }
        return sequence.toString();
    }

    @Test
    public void testingScenarion(){

        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        OrganizationAddPage organizationAddPage = new OrganizationAddPage(driver, proxy, baseUrl);

        Assertions.assertTrue(organizationAddPage.getSendBtn().isDisabled());




        organizationAddPage.getFirstname().setText(generateWord(99));
        organizationAddPage.getLastname().setText(generateWord(99));
        organizationAddPage.getPatronymic().setText(generateWord(99));
        organizationAddPage.getAddPersonsBtn().submit();

        Assertions.assertTrue(organizationAddPage.getSendBtn().isDisabled());

        organizationAddPage.getIsAgree().setChecked(true);

        Assertions.assertTrue(organizationAddPage.getSendBtn().isDisabled());

        organizationAddPage.getIsProtect().setChecked(true);

        Assertions.assertFalse(organizationAddPage.getSendBtn().isDisabled());
        organizationAddPage.getSendBtn().submit();

        //Добавить на проверку на ответ от сервера

        organizationAddPage.getOrganizationName().setText(generateWord(99));
        organizationAddPage.getOrganizationShortName().setText(generateWord(99));
        organizationAddPage.getOrganizationInn().setText(generateNumberSequence(12));
        organizationAddPage.getOrganizationOgrn().setText(generateNumberSequence(15));
        organizationAddPage.getOrganizationOkved().setText(generateWord(99));
        organizationAddPage.getOrganizationOkvedAdd().setText(generateWord(99));




        Har har = proxy.getHar();



       har.getLog().getPages().stream().forEach(harPage ->  log.info(harPage.getTitle()));

        //this.driver.quit();
        this.proxy.stop();








    }





}
