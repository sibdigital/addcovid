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
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.sibdigital.addcovid.frontend.components.*;
import ru.sibdigital.addcovid.frontend.util.CertificateUtil;
import ru.sibdigital.addcovid.frontend.util.SeleniumContainer;
import ru.sibdigital.addcovid.service.RequestService;

import java.util.List;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = "server.port=8091", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DachaPageTest {
    final private String EMPTY_ERROR_TEXT = "Поле не может быть пустым";

    final private String FINAL_WINDOW_MESSAGE = "Желаем Вам счастливого пути!\n" +
            "Напоминаем о необходимости иметь при себе паспорт и документы, подтверждающие право собственности или владения недвижимостью!";
    final private String REQUEST_ACCEPTED_MESSAGE = "<b>Желаем Вам счастливого пути!</b><br/>Напоминаем о необходимости иметь при себе паспорт и документы, подтверждающие право собственности или владения недвижимостью!";

    final private String TEST_DESTINATION_FROM_TEXT = "ул. Пушкина. д. 2";
    final private String TEST_DESTINATION_TO_TEXT = "ул. Домодедово. д. 23";

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

    @Before
    public void fillDataToEnableSubmitButton(){
        this.seleniumContainer = SeleniumContainer.getInstance(showBrowserWindow, port, protocol, host, chromeDriverLocation, certificateUtil);
        if(!this.seleniumContainer.getDriver().getCurrentUrl().equals(this.seleniumContainer.getDachaAddPage().getPageUrl())){
            this.seleniumContainer.getDriver().get(this.seleniumContainer.getDachaAddPage().getPageUrl());
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //this.seleniumContainer.getDriver().findElement(By.xpath("//*[@view_id='add_btn']")).click();

        }

        this.seleniumContainer.getDachaAddPage().getIsAgree1().setChecked(true);
        this.seleniumContainer.getDachaAddPage().getIsAgree2().setChecked(true);
        this.seleniumContainer.getDachaAddPage().getPeopleTable().clearTable();
        try{
            this.seleniumContainer.getDachaAddPage().getPeopleTable().addValue("Lorem", "Ipsum", "Allavere", "26");
        } catch (NoSuchElementException e) {
            this.seleniumContainer.getDriver().findElement(By.xpath("//*[@view_id='add_btn']//button")).click();
            this.seleniumContainer.getDachaAddPage().getPeopleTable().addValue("Lorem", "Ipsum", "Allavere", "26");
        }

    }

    @Test
    public void testDisabilitySubmitButton(){
        for (int i = 0; i < 3; i++) {
            this.seleniumContainer.getDachaAddPage().getIsAgree1().setChecked(false);
            this.seleniumContainer.getDachaAddPage().getIsAgree2().setChecked(false);
            this.seleniumContainer.getDachaAddPage().getPeopleTable().clearTable();
            Assertions.assertTrue(this.seleniumContainer.getDachaAddPage().getSendBtn().isDisabled());
            System.out.println(this.seleniumContainer.getDachaAddPage().getIsAgree1().getChecked()+" "+this.seleniumContainer.getDachaAddPage().getIsAgree2().getChecked());
            if(i == 0){
                this.seleniumContainer.getDachaAddPage().getIsAgree2().setChecked(true);
            }
            if( i==1 ){
                this.seleniumContainer.getDachaAddPage().getIsAgree1().setChecked(true);
            }
            if( i == 2){
                this.seleniumContainer.getDachaAddPage().getPeopleTable().addValue("Lorem", "Ipsum", "Allavere", "26");
            }
            System.out.println(this.seleniumContainer.getDachaAddPage().getIsAgree2().getChecked()+" "+this.seleniumContainer.getDachaAddPage().getIsAgree1().getChecked());

            for (int j = 0; j < 3; j++) {
                if(i!=j) {
                    if(j == 0){
                        this.seleniumContainer.getDachaAddPage().getIsAgree2().setChecked(true);
                    }
                    if( j==1 ){
                        this.seleniumContainer.getDachaAddPage().getIsAgree1().setChecked(true);
                    }
                    if( j == 2){
                        this.seleniumContainer.getDachaAddPage().getPeopleTable().addValue("Lorem", "Ipsum", "Allavere", "26");
                    }
                    System.out.println(this.seleniumContainer.getDachaAddPage().getIsAgree2().getChecked()+" "+this.seleniumContainer.getDachaAddPage().getIsAgree1().getChecked());
                    if(this.seleniumContainer.getDachaAddPage().getIsAgree2().getChecked() && this.seleniumContainer.getDachaAddPage().getIsAgree1().getChecked() && this.seleniumContainer.getDachaAddPage().getPeopleTable().getRowsSize() > 0){
                        Assertions.assertFalse(this.seleniumContainer.getDachaAddPage().getSendBtn().isDisabled());
                    } else {
                        Assertions.assertTrue(this.seleniumContainer.getDachaAddPage().getSendBtn().isDisabled());
                    }

                }
            }
            for (int j = 0; j < 3; j++) {
                if(i!=j){
                    if(j == 0){
                        this.seleniumContainer.getDachaAddPage().getIsAgree2().setChecked(false);
                    }
                    if( j==1 ){
                        this.seleniumContainer.getDachaAddPage().getIsAgree1().setChecked(false);
                    }
                    if( j == 2){
                        this.seleniumContainer.getDachaAddPage().getPeopleTable().clearTable();
                    }
                    System.out.println(this.seleniumContainer.getDachaAddPage().getIsAgree2().getChecked()+" "+this.seleniumContainer.getDachaAddPage().getIsAgree1().getChecked());
                    if(this.seleniumContainer.getDachaAddPage().getIsAgree2().getChecked() && this.seleniumContainer.getDachaAddPage().getIsAgree1().getChecked() && this.seleniumContainer.getDachaAddPage().getPeopleTable().getRowsSize() > 0){
                        Assertions.assertFalse(this.seleniumContainer.getDachaAddPage().getSendBtn().isDisabled());
                    } else {
                        Assertions.assertTrue(this.seleniumContainer.getDachaAddPage().getSendBtn().isDisabled());
                    }


                }
            }
            this.seleniumContainer.getDachaAddPage().getIsAgree2().setChecked(true);
            this.seleniumContainer.getDachaAddPage().getIsAgree1().setChecked(true);
            this.seleniumContainer.getDachaAddPage().getPeopleTable().addValue("Lorem", "Ipsum", "Allavere", "26");


            System.out.println(this.seleniumContainer.getDachaAddPage().getIsAgree2().getChecked()+" "+this.seleniumContainer.getDachaAddPage().getIsAgree1().getChecked());
            Assertions.assertFalse(this.seleniumContainer.getDachaAddPage().getSendBtn().isDisabled());
            //this.seleniumContainer.getDachaAddPage().getSendBtn().submit();

            List<HarEntry> entries = this.seleniumContainer.getProxy().getHar().getLog().getEntries();
            HarResponse response = getResponseForRequest(entries, "POST", this.seleniumContainer.getDachaAddPage().getPageUrl());
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
        HarResponse response = getResponseForRequest(entries, "POST", this.seleniumContainer.getDachaAddPage().getPageUrl());
        Assertions.assertNull(response);
    }

    @Test
    public void testDistrictToError(){
        HTMLAutoCompleteField autoCompleteField = this.seleniumContainer.getDachaAddPage().getToDistrict();
        //String autoCompleteText = autoCompleteField.getAvailableValues().get(1).getText();
        autoCompleteField.setText("Баунтовский");
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        Assertions.assertEquals("", autoCompleteField.getErrorText());
        assertResponseWasNull();
        autoCompleteField.setText("");
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, autoCompleteField.getErrorText());
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        assertResponseWasNull();
    }

    @Test
    public void testDistrictFromError(){
        HTMLChoiceBox choiceBox = this.seleniumContainer.getDachaAddPage().getFromDistrict();
        choiceBox.setText("Баунтовский");
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        Assertions.assertEquals("", choiceBox.getErrorText());
        assertResponseWasNull();
        choiceBox.setText("");
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, choiceBox.getErrorText());
        assertResponseWasNull();
    }

    @Test
    public void testAddressFromError(){
        this.seleniumContainer.getDachaAddPage().getFromAddress().setText(TEST_DESTINATION_FROM_TEXT);
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        Assertions.assertEquals("", this.seleniumContainer.getDachaAddPage().getFromAddress().getErrorText());
        assertResponseWasNull();
        this.seleniumContainer.getDachaAddPage().getFromAddress().setText("");
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getDachaAddPage().getFromAddress().getErrorText());
        assertResponseWasNull();
    }

    @Test
    public void testAddressToError(){
        this.seleniumContainer.getDachaAddPage().getToAddress().setText(TEST_DESTINATION_TO_TEXT);
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        Assertions.assertEquals("", this.seleniumContainer.getDachaAddPage().getToAddress().getErrorText());
        assertResponseWasNull();
        this.seleniumContainer.getDachaAddPage().getToAddress().setText("");
        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();
        this.seleniumContainer.getDachaAddPage().getFinalModalWindow().closeWindow();
        Assertions.assertEquals(EMPTY_ERROR_TEXT, this.seleniumContainer.getDachaAddPage().getToAddress().getErrorText());
        assertResponseWasNull();
    }



    @Test
    public void testingSendingToServer() {

        this.seleniumContainer.getDachaAddPage().getFromDistrict().setText("Баунтовский");
        this.seleniumContainer.getDachaAddPage().getFromAddress().setText(TEST_DESTINATION_FROM_TEXT);


        this.seleniumContainer.getDachaAddPage().getToDistrict().setText("Закаменский");
        this.seleniumContainer.getDachaAddPage().getToAddress().setText(TEST_DESTINATION_TO_TEXT);


        this.seleniumContainer.getDachaAddPage().getSendBtn().submit();

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<HarEntry> entries = this.seleniumContainer.getProxy().getHar().getLog().getEntries();
        HarResponse response = getResponseForRequest(entries, "POST", this.seleniumContainer.getDachaAddPage().getPageUrl());
        Assertions.assertEquals(200, response.getStatus());


        HTMLFinalConfirmationModalWindow finalModalWindow = this.seleniumContainer.getDachaAddPage().getFinalModalWindow();
        Assertions.assertEquals(FINAL_WINDOW_MESSAGE, finalModalWindow.getText());
        Assertions.assertEquals(REQUEST_ACCEPTED_MESSAGE, response.getContent().getText());
        finalModalWindow.closeWindow();
        this.seleniumContainer.getDriver().get(this.seleniumContainer.getDachaAddPage().getPageUrl());
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //this.seleniumContainer.getDriver().get(this.seleniumContainer.getDachaAddPage().getPageUrl());
        this.seleniumContainer.getDriver().findElement(By.xpath("//*[@view_id='add_btn']//button")).click();
        int a = 0;
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
            seleniumContainer.getDachaAddPage().getFinalModalWindow().stayOnPage();
        } catch (Exception e){

        }
        /*this.driver.quit();
        this.proxy.stop();*/
        //requestService.deleteAllByOrganizationName(TEST_ORGANIZATION_NAME);
    }

    @AfterClass
    public static void closeDriverAndProxy(){
        if(seleniumContainer != null) seleniumContainer.closeDriverAndProxy();
    }




}
