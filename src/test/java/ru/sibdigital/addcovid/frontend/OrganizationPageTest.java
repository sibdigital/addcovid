package ru.sibdigital.addcovid.frontend;


import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.sibdigital.addcovid.frontend.components.HTMLFinalConfirmationModalWindow;
import ru.sibdigital.addcovid.service.RequestService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "server.port=8091", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OrganizationPageTest {

    final private String INN_ERROR_TEXT = "Должен состоять из 10 или 12 цифр";
    final private String OGRN_ERROR_TEXT = "Должен состоять из 13 или 15 цифр";
    final private String EMPTY_ERROR_TEXT = "Поле не может быть пустым";
    final private String JUR_ADDRESS_ERROR_TEXT = "Должен содержать от 1 до 255 сиволов";
    final private String TEST_ORGANIZATION_NAME = "Тестовая организация";
    final private String REQUEST_ACCEPTED_MESSAGE = "Заявка принята. Ожидайте ответ на электронную почту.";

    private static SeleniumContainer seleniumContainer;

    @Autowired
    CertificateUtil certificateUtil;

    @Autowired
    RequestService requestService;

    List<Character> rusChars = new ArrayList<>(70);
    List<Character> engChars = new ArrayList<>(70);



    @LocalServerPort
    private int port;
    final String protocol = "http";
    final String host = "127.0.0.1";
    @Value("${test.webdriver.chrome.driver}") //#Смотрим версию своего браузера и качаем драйвер для своей версии отсюда https://chromedriver.chromium.org/downloads
    String chromeDriverLocation;
    @Value("${test.webdriver.browser.show:true}")
    boolean showBrowserWindow;
   /* WebDriver driver;
    BrowserMobProxy proxy;
    private OrganizationAddPage this.seleniumContainer.getOrganizationAddPage();*/

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

    @Before
    public void fillDataToEnableSubmitButton(){
        this.seleniumContainer = SeleniumContainer.getInstance(showBrowserWindow, port, protocol, host, chromeDriverLocation, certificateUtil);
        this.seleniumContainer.getOrganizationAddPage().getIsProtect().setChecked(true);
        this.seleniumContainer.getOrganizationAddPage().getIsAgree().setChecked(true);
        this.seleniumContainer.getOrganizationAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
    }

    @Test
    public void testDisabilitySubmitButton(){
        for (int i = 0; i < 3; i++) {
            this.seleniumContainer.getOrganizationAddPage().getIsProtect().setChecked(false);
            this.seleniumContainer.getOrganizationAddPage().getIsAgree().setChecked(false);
            this.seleniumContainer.getOrganizationAddPage().getPeopleTable().clearTable();
            Assertions.assertTrue(this.seleniumContainer.getOrganizationAddPage().getSendBtn().isDisabled());
            System.out.println(this.seleniumContainer.getOrganizationAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getPeopleTable().getRowsSize() );
            if(i == 0){
                this.seleniumContainer.getOrganizationAddPage().getIsProtect().setChecked(true);
            }
            if( i==1 ){
                this.seleniumContainer.getOrganizationAddPage().getIsAgree().setChecked(true);
            }
            if( i== 3 ){
                this.seleniumContainer.getOrganizationAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
            }
            System.out.println(this.seleniumContainer.getOrganizationAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getPeopleTable().getRowsSize() );

            for (int j = 0; j < 3; j++) {
                if(i!=j) {
                    if(j == 0){
                        this.seleniumContainer.getOrganizationAddPage().getIsProtect().setChecked(true);
                    }
                    if( j==1 ){
                        this.seleniumContainer.getOrganizationAddPage().getIsAgree().setChecked(true);
                    }
                    if( j== 2 ){
                        this.seleniumContainer.getOrganizationAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
                    }
                    System.out.println(this.seleniumContainer.getOrganizationAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getPeopleTable().getRowsSize() );
                    if(this.seleniumContainer.getOrganizationAddPage().getIsProtect().getChecked() && this.seleniumContainer.getOrganizationAddPage().getIsAgree().getChecked() && this.seleniumContainer.getOrganizationAddPage().getPeopleTable().getRowsSize() > 0){
                        Assertions.assertFalse(this.seleniumContainer.getOrganizationAddPage().getSendBtn().isDisabled());
                    } else {
                        Assertions.assertTrue(this.seleniumContainer.getOrganizationAddPage().getSendBtn().isDisabled());
                    }

                }
            }
            for (int j = 0; j < 3; j++) {
                if(i!=j){
                    if(j == 0){
                        this.seleniumContainer.getOrganizationAddPage().getIsProtect().setChecked(false);
                    }
                    if( j==1 ){
                        this.seleniumContainer.getOrganizationAddPage().getIsAgree().setChecked(false);
                    }
                    if( j== 2 ){
                        this.seleniumContainer.getOrganizationAddPage().getPeopleTable().deleteRow(0);
                    }
                    System.out.println(this.seleniumContainer.getOrganizationAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getPeopleTable().getRowsSize() );
                    if(this.seleniumContainer.getOrganizationAddPage().getIsProtect().getChecked() && this.seleniumContainer.getOrganizationAddPage().getIsAgree().getChecked() && this.seleniumContainer.getOrganizationAddPage().getPeopleTable().getRowsSize() > 0){
                        Assertions.assertFalse(this.seleniumContainer.getOrganizationAddPage().getSendBtn().isDisabled());
                    } else {
                        Assertions.assertTrue(this.seleniumContainer.getOrganizationAddPage().getSendBtn().isDisabled());
                    }


                }
            }
            this.seleniumContainer.getOrganizationAddPage().getIsProtect().setChecked(true);
            this.seleniumContainer.getOrganizationAddPage().getIsAgree().setChecked(true);
            this.seleniumContainer.getOrganizationAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
            System.out.println(this.seleniumContainer.getOrganizationAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getOrganizationAddPage().getPeopleTable().getRowsSize() );
            Assertions.assertFalse(this.seleniumContainer.getOrganizationAddPage().getSendBtn().isDisabled());
            //this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();

            List<HarEntry> entries = this.seleniumContainer.getProxy().getHar().getLog().getEntries();
            HarResponse response = getResponseForRequest(entries, "POST", "http://127.0.0.1:8091/form");
            Assertions.assertNull(response);
        }
    }

    public void assertResponseWasNull(){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<HarEntry> entries = this.seleniumContainer.getProxy().getHar().getLog().getEntries();
        HarResponse response = getResponseForRequest(entries, "POST", "http://127.0.0.1:8091/form");
        Assertions.assertNull(response);
    }

    @Test
    public void testOrganizationNameError(){
        this.seleniumContainer.getOrganizationAddPage().getOrganizationName().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationName().getErrorText());
        assertResponseWasNull();
        this.seleniumContainer.getOrganizationAddPage().getOrganizationName().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationName().getErrorText());
        assertResponseWasNull();

    }

    @Test
    public void testOrganizationShortNameError(){
        this.seleniumContainer.getOrganizationAddPage().getOrganizationShortName().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationShortName().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getOrganizationShortName().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationShortName().getErrorText());
        

    }

    @Test
    public void testInnError(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            this.seleniumContainer.getOrganizationAddPage().getOrganizationInn().setText(stringBuilder.toString());
            this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
            assertResponseWasNull();
            if(stringBuilder.length() == 12 || stringBuilder.length() == 10){
                Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationInn().getErrorText());
            } else {
                Assertions.assertEquals(INN_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationInn().getErrorText());
            }
            stringBuilder.append(i);
        }

    }

    @Test
    public void testOgrnError(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 17; i++) {
            this.seleniumContainer.getOrganizationAddPage().getOrganizationOgrn().setText(stringBuilder.toString());
            this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
            assertResponseWasNull();
            if(stringBuilder.length() == 13 || stringBuilder.length() == 15){
                Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationOgrn().getErrorText());
            } else {
                Assertions.assertEquals(OGRN_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationOgrn().getErrorText());
            }
            stringBuilder.append(i);
        }

    }

    @Test
    public void testOrganizationPhoneError(){
        assertResponseWasNull();
        this.seleniumContainer.getOrganizationAddPage().getOrganizationPhone().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationPhone().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getOrganizationPhone().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationPhone().getErrorText());

    }

    @Test
    public void testOrganizationEmailError(){
        this.seleniumContainer.getOrganizationAddPage().getOrganizationEmail().setText(generateEmail(50));
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationEmail().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getOrganizationEmail().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationEmail().getErrorText());

    }

    @Test
    public void testOrganizationOkvedError(){
        this.seleniumContainer.getOrganizationAddPage().getOrganizationOkved().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationOkved().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getOrganizationOkved().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationOkved().getErrorText());

    }

    @Test
    public void testReqBasisError(){
        this.seleniumContainer.getOrganizationAddPage().getReqBasis().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getReqBasis().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getReqBasis().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(JUR_ADDRESS_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getReqBasis().getErrorText());

    }

    @Test
    public void testPersonOfficeCntError(){
        this.seleniumContainer.getOrganizationAddPage().getPersonOfficeCnt().setText(String.valueOf(getRandomNumber(1,200)));
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getPersonOfficeCnt().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getPersonOfficeCnt().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getPersonOfficeCnt().getErrorText());

    }

    @Test
    public void testPersonRemoteCntError(){
        this.seleniumContainer.getOrganizationAddPage().getPersonRemoteCnt().setText(String.valueOf(getRandomNumber(1,200)));
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getPersonRemoteCnt().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getPersonRemoteCnt().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getPersonRemoteCnt().getErrorText());

    }

    @Test
    public void testPersonSlrySaveCntError(){
        this.seleniumContainer.getOrganizationAddPage().getPersonSlrySaveCnt().setText(String.valueOf(getRandomNumber(1,200)));
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getPersonSlrySaveCnt().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getPersonSlrySaveCnt().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getPersonSlrySaveCnt().getErrorText());

    }

    @Test
    public void testOrganizationAddressJurError(){
        this.seleniumContainer.getOrganizationAddPage().getOrganizationAddressJur().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getOrganizationAddPage().getOrganizationAddressJur().getErrorText());
        this.seleniumContainer.getOrganizationAddPage().getOrganizationAddressJur().setText("");
        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getOrganizationAddPage().getOrganizationAddressJur().getErrorText());
    }


    @Test
    public void testingSendingToServer(){



        this.seleniumContainer.getOrganizationAddPage().getOrganizationName().setText(TEST_ORGANIZATION_NAME);


        this.seleniumContainer.getOrganizationAddPage().getOrganizationShortName().setText(generateWord(99));



        this.seleniumContainer.getOrganizationAddPage().getOrganizationInn().setText("9999999999");



        this.seleniumContainer.getOrganizationAddPage().getOrganizationOgrn().setText("999999999999999");


        this.seleniumContainer.getOrganizationAddPage().getOrganizationPhone().setText(generateNumberSequence(11));


        this.seleniumContainer.getOrganizationAddPage().getOrganizationEmail().setText(generateEmail(30));


        this.seleniumContainer.getOrganizationAddPage().getOrganizationOkved().setText(generateWord(99));


        this.seleniumContainer.getOrganizationAddPage().getOrganizationOkvedAdd().setText(generateWord(99));


        List<WebElement> availableValues = this.seleniumContainer.getOrganizationAddPage().getDepartmentId().getAvailableValues();
        availableValues.get(getRandomNumber(0, availableValues.size())).click();

        this.seleniumContainer.getOrganizationAddPage().getOrganizationAddressJur().setText(generateWord(200));


        Assertions.assertEquals(0, this.seleniumContainer.getOrganizationAddPage().getAddressTable().getRowsSize());
        this.seleniumContainer.getOrganizationAddPage().getAddressTable().addValue(generateWord(99), String.valueOf(getRandomNumber(1,255)));
        Assertions.assertEquals(1, this.seleniumContainer.getOrganizationAddPage().getAddressTable().getRowsSize());


        this.seleniumContainer.getOrganizationAddPage().getReqBasis().setText(generateWord(99));


        this.seleniumContainer.getOrganizationAddPage().getPersonOfficeCnt().setText(String.valueOf(getRandomNumber(1,255)));


        this.seleniumContainer.getOrganizationAddPage().getPersonRemoteCnt().setText(String.valueOf(getRandomNumber(1,255)));


        this.seleniumContainer.getOrganizationAddPage().getPersonSlrySaveCnt().setText(String.valueOf(getRandomNumber(1,255)));



      /*  try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/





        /*this.seleniumContainer.getProxy().newHar(this.seleniumContainer.getOrganizationAddPage().getPageUrl());

*/



      // har.getLog().getPages().stream().forEach(harPage ->  log.info(harPage.getTitle()));


        this.seleniumContainer.getOrganizationAddPage().getSendBtn().submit();

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<HarEntry> entries = this.seleniumContainer.getProxy().getHar().getLog().getEntries();
        HarResponse response = getResponseForRequest(entries, "POST", "http://127.0.0.1:8091/form");
        Assertions.assertEquals(200, response.getStatus());

        /*har.getLog().getEntries()
        for (HarEntry harEntry : har.getLog().getEntries()) {
            HarRequest request = harEntry.getRequest();
            HarResponse response = harEntry.getResponse();
            request.getMethod()
            log.info(String.format("%s : %d", request.getUrl(), response.getStatus()));
        }*/

        HTMLFinalConfirmationModalWindow finalModalWindow = this.seleniumContainer.getOrganizationAddPage().getFinalModalWindow();
        Assertions.assertEquals(REQUEST_ACCEPTED_MESSAGE ,finalModalWindow.getText());
        Assertions.assertEquals(REQUEST_ACCEPTED_MESSAGE, response.getContent().getText());
        finalModalWindow.newRequest();




        //requestService.deleteAllByOrganizationName(TEST_ORGANIZATION_NAME);
        //seleniumContainer.getProxy().stop();



    }


    public HarResponse getResponseForRequest(List<HarEntry> entries , String method, String url){

        for (HarEntry harEntry : entries) {
            HarRequest request = harEntry.getRequest();
            HarResponse response = harEntry.getResponse();
            log.info(String.format("%s %s : %d",request.getMethod(), request.getUrl(), response.getStatus()));
            if(request.getUrl().equals(url) && request.getMethod().equals(method)){
                this.seleniumContainer.getProxy().endHar();
                this.seleniumContainer.getProxy().newHar(this.seleniumContainer.getBaseUrl());
                return response;
            }

        }
        return null;
    }

//    @Test
//    public void test(){}

    @After
    public void deleteTestValuesFromDb(){
        /*this.driver.quit();
        this.proxy.stop();*/
        requestService.deleteAllByOrganizationName(TEST_ORGANIZATION_NAME);
    }

    @AfterClass
    public static void closeDriverAndProxy(){
        if(seleniumContainer != null) seleniumContainer.closeDriverAndProxy();
    }




}
