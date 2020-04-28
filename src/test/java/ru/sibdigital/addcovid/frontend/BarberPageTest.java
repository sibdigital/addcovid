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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.sibdigital.addcovid.frontend.components.HTMLFinalConfirmationModalWindow;
import ru.sibdigital.addcovid.frontend.util.CertificateUtil;
import ru.sibdigital.addcovid.frontend.util.CharsAndSymbolsGenerator;
import ru.sibdigital.addcovid.frontend.util.SeleniumContainer;
import ru.sibdigital.addcovid.service.RequestService;

import java.util.List;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "server.port=8091", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BarberPageTest {

    final private String INN_ERROR_TEXT = "Должен состоять из 10 или 12 цифр";
    final private String OGRN_ERROR_TEXT = "Должен состоять из 13 или 15 цифр";
    final private String EMPTY_ERROR_TEXT = "Поле не может быть пустым";
    final private String JUR_ADDRESS_ERROR_TEXT = "Должен содержать от 1 до 255 сиволов";
    final private String TEST_ORGANIZATION_NAME = "Тестовая парикмахерская";
    final private String FINAL_WINDOW_MESSAGE = "Заявка принята. Ожидайте ответ на электронную почту.\n" +
            "Обязательно распечатайте предписание Управления Роспотребнадзора по Республике Бурятия и разместите на видном месте\n" +
            "Ссылка для скачивания";
    final private String REQUEST_ACCEPTED_MESSAGE = "Заявка принята. Ожидайте ответ на электронную почту.";

    private static SeleniumContainer seleniumContainer;

    @Autowired
    CertificateUtil certificateUtil;

    @Autowired
    RequestService requestService;

    

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
    private OrganizationAddPage this.seleniumContainer.getBarberAddPage();*/

    public BarberPageTest() {}

    

    @Before
    public void fillDataToEnableSubmitButton(){
        this.seleniumContainer = SeleniumContainer.getInstance(showBrowserWindow, port, protocol, host, chromeDriverLocation, certificateUtil);
        this.seleniumContainer.getBarberAddPage().getIsProtect().setChecked(true);
        this.seleniumContainer.getBarberAddPage().getIsAgree().setChecked(true);
        this.seleniumContainer.getBarberAddPage().getPeopleTable().clearTable();
        this.seleniumContainer.getBarberAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
    }

    @Test
    public void testDisabilitySubmitButton(){
        for (int i = 0; i < 3; i++) {
            this.seleniumContainer.getBarberAddPage().getIsProtect().setChecked(false);
            this.seleniumContainer.getBarberAddPage().getIsAgree().setChecked(false);
            this.seleniumContainer.getBarberAddPage().getPeopleTable().clearTable();
            Assertions.assertTrue(this.seleniumContainer.getBarberAddPage().getSendBtn().isDisabled());
            System.out.println(this.seleniumContainer.getBarberAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getPeopleTable().getRowsSize() );
            if(i == 0){
                this.seleniumContainer.getBarberAddPage().getIsProtect().setChecked(true);
            }
            if( i==1 ){
                this.seleniumContainer.getBarberAddPage().getIsAgree().setChecked(true);
            }
            if( i== 3 ){
                this.seleniumContainer.getBarberAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
            }
            System.out.println(this.seleniumContainer.getBarberAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getPeopleTable().getRowsSize() );

            for (int j = 0; j < 3; j++) {
                if(i!=j) {
                    if(j == 0){
                        this.seleniumContainer.getBarberAddPage().getIsProtect().setChecked(true);
                    }
                    if( j==1 ){
                        this.seleniumContainer.getBarberAddPage().getIsAgree().setChecked(true);
                    }
                    if( j== 2 ){
                        this.seleniumContainer.getBarberAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
                    }
                    System.out.println(this.seleniumContainer.getBarberAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getPeopleTable().getRowsSize() );
                    if(this.seleniumContainer.getBarberAddPage().getIsProtect().getChecked() && this.seleniumContainer.getBarberAddPage().getIsAgree().getChecked() && this.seleniumContainer.getBarberAddPage().getPeopleTable().getRowsSize() > 0){
                        Assertions.assertFalse(this.seleniumContainer.getBarberAddPage().getSendBtn().isDisabled());
                    } else {
                        Assertions.assertTrue(this.seleniumContainer.getBarberAddPage().getSendBtn().isDisabled());
                    }

                }
            }
            for (int j = 0; j < 3; j++) {
                if(i!=j){
                    if(j == 0){
                        this.seleniumContainer.getBarberAddPage().getIsProtect().setChecked(false);
                    }
                    if( j==1 ){
                        this.seleniumContainer.getBarberAddPage().getIsAgree().setChecked(false);
                    }
                    if( j== 2 ){
                        this.seleniumContainer.getBarberAddPage().getPeopleTable().deleteRow(0);
                    }
                    System.out.println(this.seleniumContainer.getBarberAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getPeopleTable().getRowsSize() );
                    if(this.seleniumContainer.getBarberAddPage().getIsProtect().getChecked() && this.seleniumContainer.getBarberAddPage().getIsAgree().getChecked() && this.seleniumContainer.getBarberAddPage().getPeopleTable().getRowsSize() > 0){
                        Assertions.assertFalse(this.seleniumContainer.getBarberAddPage().getSendBtn().isDisabled());
                    } else {
                        Assertions.assertTrue(this.seleniumContainer.getBarberAddPage().getSendBtn().isDisabled());
                    }


                }
            }
            this.seleniumContainer.getBarberAddPage().getIsProtect().setChecked(true);
            this.seleniumContainer.getBarberAddPage().getIsAgree().setChecked(true);
            this.seleniumContainer.getBarberAddPage().getPeopleTable().addValue("Hey", "Heyt", "Dddd");
            System.out.println(this.seleniumContainer.getBarberAddPage().getIsProtect().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getIsAgree().getChecked()+" "+this.seleniumContainer.getBarberAddPage().getPeopleTable().getRowsSize() );
            Assertions.assertFalse(this.seleniumContainer.getBarberAddPage().getSendBtn().isDisabled());
            //this.seleniumContainer.getBarberAddPage().getSendBtn().submit();

            List<HarEntry> entries = this.seleniumContainer.getProxy().getHar().getLog().getEntries();
            HarResponse response = getResponseForRequest(entries, "POST", this.seleniumContainer.getBarberAddPage().getPageUrl());
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
        HarResponse response = getResponseForRequest(entries, "POST", this.seleniumContainer.getBarberAddPage().getPageUrl());
        Assertions.assertNull(response);
    }

    @Test
    public void testOrganizationNameError(){
        this.seleniumContainer.getBarberAddPage().getOrganizationName().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationName().getErrorText());
        assertResponseWasNull();
        this.seleniumContainer.getBarberAddPage().getOrganizationName().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationName().getErrorText());
        assertResponseWasNull();

    }

    @Test
    public void testOrganizationShortNameError(){
        this.seleniumContainer.getBarberAddPage().getOrganizationShortName().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationShortName().getErrorText());
        this.seleniumContainer.getBarberAddPage().getOrganizationShortName().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationShortName().getErrorText());
        

    }

    @Test
    public void testInnError(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            this.seleniumContainer.getBarberAddPage().getOrganizationInn().setText(stringBuilder.toString());
            this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
            assertResponseWasNull();
            if(stringBuilder.length() == 12 || stringBuilder.length() == 10){
                Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationInn().getErrorText());
            } else {
                Assertions.assertEquals(INN_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationInn().getErrorText());
            }
            stringBuilder.append(i);
        }

    }

    @Test
    public void testOgrnError(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 17; i++) {
            this.seleniumContainer.getBarberAddPage().getOrganizationOgrn().setText(stringBuilder.toString());
            this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
            assertResponseWasNull();
            if(stringBuilder.length() == 13 || stringBuilder.length() == 15){
                Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationOgrn().getErrorText());
            } else {
                Assertions.assertEquals(OGRN_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationOgrn().getErrorText());
            }
            stringBuilder.append(i);
        }

    }

    @Test
    public void testOrganizationPhoneError(){
        assertResponseWasNull();
        this.seleniumContainer.getBarberAddPage().getOrganizationPhone().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationPhone().getErrorText());
        this.seleniumContainer.getBarberAddPage().getOrganizationPhone().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationPhone().getErrorText());

    }

    @Test
    public void testOrganizationEmailError(){
        this.seleniumContainer.getBarberAddPage().getOrganizationEmail().setText(CharsAndSymbolsGenerator.generateEmail(50));
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationEmail().getErrorText());
        this.seleniumContainer.getBarberAddPage().getOrganizationEmail().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationEmail().getErrorText());

    }

    @Test
    public void testOrganizationOkvedError(){
        this.seleniumContainer.getBarberAddPage().getOrganizationOkved().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationOkved().getErrorText());
        this.seleniumContainer.getBarberAddPage().getOrganizationOkved().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationOkved().getErrorText());

    }

    @Test
    public void testReqBasisError(){
        this.seleniumContainer.getBarberAddPage().getReqBasis().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getReqBasis().getErrorText());
        this.seleniumContainer.getBarberAddPage().getReqBasis().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(JUR_ADDRESS_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getReqBasis().getErrorText());

    }

    @Test
    public void testPersonOfficeCntError(){
        this.seleniumContainer.getBarberAddPage().getPersonOfficeCnt().setText(String.valueOf(CharsAndSymbolsGenerator.getRandomNumber(1,200)));
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getPersonOfficeCnt().getErrorText());
        this.seleniumContainer.getBarberAddPage().getPersonOfficeCnt().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getPersonOfficeCnt().getErrorText());

    }

    @Test
    public void testPersonRemoteCntError(){
        this.seleniumContainer.getBarberAddPage().getPersonRemoteCnt().setText(String.valueOf(CharsAndSymbolsGenerator.getRandomNumber(1,200)));
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getPersonRemoteCnt().getErrorText());
        this.seleniumContainer.getBarberAddPage().getPersonRemoteCnt().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getPersonRemoteCnt().getErrorText());

    }

    @Test
    public void testPersonSlrySaveCntError(){
        this.seleniumContainer.getBarberAddPage().getPersonSlrySaveCnt().setText(String.valueOf(CharsAndSymbolsGenerator.getRandomNumber(1,200)));
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getPersonSlrySaveCnt().getErrorText());
        this.seleniumContainer.getBarberAddPage().getPersonSlrySaveCnt().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getPersonSlrySaveCnt().getErrorText());

    }

    @Test
    public void testOrganizationAddressJurError(){
        this.seleniumContainer.getBarberAddPage().getOrganizationAddressJur().setText(TEST_ORGANIZATION_NAME);
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals("", this.seleniumContainer.getBarberAddPage().getOrganizationAddressJur().getErrorText());
        this.seleniumContainer.getBarberAddPage().getOrganizationAddressJur().setText("");
        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();
        assertResponseWasNull();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getBarberAddPage().getOrganizationAddressJur().getErrorText());
    }


    @Test
    public void testingSendingToServer(){



        this.seleniumContainer.getBarberAddPage().getOrganizationName().setText(TEST_ORGANIZATION_NAME);


        this.seleniumContainer.getBarberAddPage().getOrganizationShortName().setText(CharsAndSymbolsGenerator.generateWord(99));



        this.seleniumContainer.getBarberAddPage().getOrganizationInn().setText("9999999999");



        this.seleniumContainer.getBarberAddPage().getOrganizationOgrn().setText("999999999999999");


        this.seleniumContainer.getBarberAddPage().getOrganizationPhone().setText(CharsAndSymbolsGenerator.generateNumberSequence(11));


        this.seleniumContainer.getBarberAddPage().getOrganizationEmail().setText(CharsAndSymbolsGenerator.generateEmail(30));


        this.seleniumContainer.getBarberAddPage().getOrganizationOkved().setText(CharsAndSymbolsGenerator.generateWord(99));


        this.seleniumContainer.getBarberAddPage().getOrganizationOkvedAdd().setText(CharsAndSymbolsGenerator.generateWord(99));

        this.seleniumContainer.getBarberAddPage().getOrganizationAddressJur().setText(CharsAndSymbolsGenerator.generateWord(200));


        Assertions.assertEquals(0, this.seleniumContainer.getBarberAddPage().getAddressTable().getRowsSize());
        this.seleniumContainer.getBarberAddPage().getAddressTable().addValue(CharsAndSymbolsGenerator.generateWord(99), String.valueOf(CharsAndSymbolsGenerator.getRandomNumber(1,255)));
        Assertions.assertEquals(1, this.seleniumContainer.getBarberAddPage().getAddressTable().getRowsSize());


        this.seleniumContainer.getBarberAddPage().getReqBasis().setText(CharsAndSymbolsGenerator.generateWord(99));


        this.seleniumContainer.getBarberAddPage().getPersonOfficeCnt().setText(String.valueOf(CharsAndSymbolsGenerator.getRandomNumber(1,255)));


        this.seleniumContainer.getBarberAddPage().getPersonRemoteCnt().setText(String.valueOf(CharsAndSymbolsGenerator.getRandomNumber(1,255)));


        this.seleniumContainer.getBarberAddPage().getPersonSlrySaveCnt().setText(String.valueOf(CharsAndSymbolsGenerator.getRandomNumber(1,255)));



      /*  try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/





        /*this.seleniumContainer.getProxy().newHar(this.seleniumContainer.getBarberAddPage().getPageUrl());

*/



      // har.getLog().getPages().stream().forEach(harPage ->  log.info(harPage.getTitle()));


        this.seleniumContainer.getBarberAddPage().getSendBtn().submit();

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        List<HarEntry> entries = this.seleniumContainer.getProxy().getHar().getLog().getEntries();
        HarResponse response = getResponseForRequest(entries, "POST", this.seleniumContainer.getBarberAddPage().getPageUrl());
        Assertions.assertEquals(200, response.getStatus());

        /*har.getLog().getEntries()
        for (HarEntry harEntry : har.getLog().getEntries()) {
            HarRequest request = harEntry.getRequest();
            HarResponse response = harEntry.getResponse();
            request.getMethod()
            log.info(String.format("%s : %d", request.getUrl(), response.getStatus()));
        }*/

        HTMLFinalConfirmationModalWindow finalModalWindow = this.seleniumContainer.getBarberAddPage().getFinalModalWindow();
        Assertions.assertEquals(FINAL_WINDOW_MESSAGE ,finalModalWindow.getText());
        Assertions.assertEquals(REQUEST_ACCEPTED_MESSAGE, response.getContent().getText());
        finalModalWindow.stayOnPage();




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
        try {
            seleniumContainer.getBarberAddPage().getFinalModalWindow().stayOnPage();
        } catch (Exception e){

        }
        /*this.driver.quit();
        this.proxy.stop();*/
        requestService.deleteAllByOrganizationName(TEST_ORGANIZATION_NAME);
    }

    @AfterClass
    public static void closeDriverAndProxy(){
        if(seleniumContainer != null) seleniumContainer.closeDriverAndProxy();
    }




}
