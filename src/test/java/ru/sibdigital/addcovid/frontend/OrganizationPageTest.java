package ru.sibdigital.addcovid.frontend;


import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
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

    final private String INN_ERROR_TEXT = "Должен состоять из 10 или 12 цифр";
    final private String OGRN_ERROR_TEXT = "Должен состоять из 13 или 15 цифр";
    final private String EMPTY_ERROR_TEXT = "Поле не может быть пустым";

    @Autowired
    CertificateUtil certificateUtil;


    List<Character> rusChars = new ArrayList<>(70);
    List<Character> engChars = new ArrayList<>(70);



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
        for (int i = 'А'; i <= 'Я'; i++) {
            rusChars.add((char) i);
        }
        rusChars.add(' ');
        rusChars.add('Ё');
        rusChars.add('ё');
        for (int i = 'а'; i <= 'я'; i++) {
            rusChars.add((char) i);
        }

        for (int i = 'A'; i <= 'Z'; i++) {
            engChars.add((char) i);
        }
        for (int i = 'a'; i <= 'z'; i++) {
            engChars.add((char) i);
        }



    }

    @PostConstruct
    public void initUrl(){
        this.baseUrl = String.format("%s://%s:%d", this.protocol, this.host, this.port);
        //System.setProperty("webdriver.chrome.driver",chromeDriverLocation);
        // start the proxy
        proxy = new BrowserMobProxyServer();
        proxy.setMitmManager(certificateUtil.getCertificate());
        proxy.setTrustAllServers(true);
        //proxy.setMitmManager(ImpersonatingMitmManager.builder().trustAllServers(true).build());
        /*proxy.addRequestFilter((httpRequest, httpMessageContents, httpMessageInfo) -> {
            log.info(httpRequest.method().name());
            log.info(httpRequest.uri());
            log.info("addRequestFilter");
            return null;
        });*/

        proxy.addResponseFilter((httpResponse, httpMessageContents, httpMessageInfo) -> {
            log.info(String.valueOf(httpResponse.status().code()));
            log.info(httpMessageContents.isText() ? httpMessageContents.getTextContents() : httpMessageContents.getContentType());
            log.info(httpMessageInfo.getUrl());
            log.info("addResponseFilter");
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

        int sizeOfSequence = this.getRandomNumber(1,maximumNumberOfChars);

        StringBuilder word = new StringBuilder();

        for (int i = 0; i < sizeOfSequence; i++) {
            word.append(rusChars.get(this.getRandomNumber(0,this.rusChars.size()-1)));
        }

        return word.toString();
    }

    private String generateEmail(int sizeOfSequence){


        StringBuilder word = new StringBuilder();

        for (int i = 0; i < sizeOfSequence; i++) {
            if(i == sizeOfSequence/2) {
                word.append("@");
            } else if(i == sizeOfSequence-3){
                word.append(".");
            } else {
                word.append(engChars.get(this.getRandomNumber(0,this.engChars.size()-1)));
            }
        }

        return word.toString();
    }

    private int getRandomNumber(int min, int max) {
        return min + (int) (Math.random() * max-1);
    }

    private String generateNumberSequence(int sizeOfSequence){
        StringBuilder sequence = new StringBuilder();

        for (int i = 0; i < sizeOfSequence; i++) {
            sequence.append(this.getRandomNumber(0,9));
        }
        return sequence.toString();
    }

    @Test
    public void testingScenario(){

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

        Assertions.assertEquals(EMPTY_ERROR_TEXT, organizationAddPage.getOrganizationName().getErrorText());
        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getOrganizationShortName().getErrorText());

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            organizationAddPage.getOrganizationInn().setText(stringBuilder.toString());
            organizationAddPage.getSendBtn().submit();
            if(stringBuilder.length() == 12 || stringBuilder.length() == 10){
                Assertions.assertEquals("", organizationAddPage.getOrganizationInn().getErrorText());
            } else {
                Assertions.assertEquals(INN_ERROR_TEXT, organizationAddPage.getOrganizationInn().getErrorText());
            }
            stringBuilder.append(i);
        }


        stringBuilder = new StringBuilder();
        for (int i = 0; i < 17; i++) {
            organizationAddPage.getOrganizationOgrn().setText(stringBuilder.toString());
            organizationAddPage.getSendBtn().submit();
            if(stringBuilder.length() == 13 || stringBuilder.length() == 15){
                Assertions.assertEquals("", organizationAddPage.getOrganizationOgrn().getErrorText());
            } else {
                Assertions.assertEquals(OGRN_ERROR_TEXT, organizationAddPage.getOrganizationOgrn().getErrorText());
            }
            stringBuilder.append(i);
        }
        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getOrganizationPhone().getErrorText());
        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getOrganizationEmail().getErrorText());
        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getOrganizationOkved().getErrorText());

        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getReqBasis().getErrorText());


        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getPersonOfficeCnt().getErrorText());
        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getPersonRemoteCnt().getErrorText());
        Assertions.assertEquals(EMPTY_ERROR_TEXT,organizationAddPage.getPersonSlrySaveCnt().getErrorText());





        //Добавить на проверку на ответ от сервера

        organizationAddPage.getOrganizationName().setText(generateWord(99));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationName().getErrorText());



        organizationAddPage.getOrganizationShortName().setText(generateWord(99));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationShortName().getErrorText());


        organizationAddPage.getOrganizationInn().setText(generateNumberSequence(12));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationInn().getErrorText());


        organizationAddPage.getOrganizationOgrn().setText(generateNumberSequence(15));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationOgrn().getErrorText());

        organizationAddPage.getOrganizationPhone().setText(generateNumberSequence(11));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationPhone().getErrorText());

        organizationAddPage.getOrganizationEmail().setText(generateEmail(30));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationEmail().getErrorText());

        organizationAddPage.getOrganizationOkved().setText(generateWord(99));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationOkved().getErrorText());

        organizationAddPage.getOrganizationOkvedAdd().setText(generateWord(99));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationOkvedAdd().getErrorText());

        List<WebElement> availableValues = organizationAddPage.getDepartmentId().getAvailableValues();
        availableValues.get(getRandomNumber(0, availableValues.size())).click();


        organizationAddPage.getOrganizationAddressJur().setText(generateWord(200));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("", organizationAddPage.getOrganizationName().getErrorText());


        Assertions.assertEquals(0, organizationAddPage.getAddressTable().getRowsSize());
        organizationAddPage.getAddressFact().setText(generateWord(99));
        organizationAddPage.getPersonOfficeFactCnt().setText(String.valueOf(getRandomNumber(1,255)));
        organizationAddPage.getButtonAddrAdd().submit();


        organizationAddPage.getReqBasis().setText(generateWord(99));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("",organizationAddPage.getReqBasis().getErrorText());


        organizationAddPage.getPersonOfficeCnt().setText(String.valueOf(getRandomNumber(1,255)));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("",organizationAddPage.getPersonOfficeCnt().getErrorText());

        organizationAddPage.getPersonRemoteCnt().setText(String.valueOf(getRandomNumber(1,255)));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("",organizationAddPage.getPersonRemoteCnt().getErrorText());




        organizationAddPage.getPersonSlrySaveCnt().setText(String.valueOf(getRandomNumber(1,255)));
        organizationAddPage.getSendBtn().submit();
        Assertions.assertEquals("",organizationAddPage.getPersonSlrySaveCnt().getErrorText());









        Har har = proxy.getHar();



      // har.getLog().getPages().stream().forEach(harPage ->  log.info(harPage.getTitle()));

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.driver.quit();
        this.proxy.stop();








    }





}
