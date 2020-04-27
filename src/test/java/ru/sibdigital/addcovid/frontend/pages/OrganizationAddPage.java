package ru.sibdigital.addcovid.frontend.pages;

import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.sibdigital.addcovid.frontend.components.*;

import java.util.ArrayList;
import java.util.List;

public class OrganizationAddPage {
    private String pageUrl;

    private final HTMLComponent organizationName;
    HTMLComponent organizationShortName;
    HTMLComponent organizationInn;
    HTMLComponent organizationOgrn;
    HTMLComponent organizationEmail;
    HTMLComponent organizationPhone;
    HTMLComponent organizationOkved;
    HTMLComponent organizationOkvedAdd;
    HTMLComponent departmentId;

    HTMLComponent organizationAddressJur;

    private final HTMLComponent addressTable;



    HTMLComponent reqBasis;
    HTMLComponent upload;

    HTMLComponent personSlrySaveCnt;
    HTMLComponent personRemoteCnt;
    HTMLComponent personOfficeCnt;

    HTMLComponent peopleTable;


    HTMLComponent isAgree;
    HTMLComponent isProtect;
    HTMLComponent sendBtn;

    HTMLComponent errorPane;
    HTMLComponent finalModalWindow;



    public OrganizationAddPage(WebDriver driver, BrowserMobProxy proxy, String baseUrl) {

        this.pageUrl = baseUrl+"/form";




        driver.get(pageUrl);
        this.organizationName = new HTMLText(driver, By.xpath("//*[@view_id='organizationName']"));
        this.organizationShortName = new HTMLText(driver, By.xpath("//*[@view_id='organizationShortName']"));
        this.organizationInn = new HTMLText(driver, By.xpath("//*[@view_id='organizationInn']"));
        this.organizationOgrn = new HTMLText(driver, By.xpath("//*[@view_id='organizationOgrn']"));
        this.organizationEmail = new HTMLText(driver, By.xpath("//*[@view_id='organizationEmail']"));
        this.organizationPhone = new HTMLText(driver, By.xpath("//*[@view_id='organizationPhone']"));
        this.organizationOkved = new HTMLText(driver, By.xpath("//*[@view_id='organizationOkved']"));
        this.organizationOkvedAdd = new HTMLTextArea(driver, By.xpath("//*[@view_id='organizationOkvedAdd']"));
        this.departmentId = new HTMLChoiceBox(driver, By.xpath("//*[@view_id='departmentId']"));

        this.organizationAddressJur = new HTMLTextArea(driver, By.xpath("//*[@view_id='organizationAddressJur']"));


        HTMLComponent addressFact = new HTMLText(driver, By.xpath("//*[@view_id='addressFactText']"));
        HTMLComponent personOfficeFactCnt = new HTMLText(driver, By.xpath("//*[@view_id='personOfficeFactCntText']"));
        HTMLButton buttonAddrAdd = new HTMLButton(driver, By.xpath("//*[@view_id='buttonAddrAdd']"));
        HTMLButton buttonAddrEdit = new HTMLButton(driver, By.xpath("//*[@view_id='buttonAddrEdit']"));
        HTMLButton buttonAddrRemove = new HTMLButton(driver, By.xpath("//*[@view_id='buttonAddrRemove']"));
        List<HTMLComponent> addressFields = new ArrayList<>(2);
        addressFields.add(addressFact);
        addressFields.add(personOfficeFactCnt);
        this.addressTable = new HTMLTable(driver, By.xpath("//*[@view_id='addr_table']"), addressFields, buttonAddrAdd, buttonAddrRemove, buttonAddrEdit);


        this.reqBasis = new HTMLTextArea(driver, By.xpath("//*[@view_id='reqBasis']"));
        this.upload = new HTMLText(driver, By.xpath("//*[@view_id='upload']")); //File

        this.personSlrySaveCnt = new HTMLText(driver, By.xpath("//*[@view_id='personSlrySaveCnt']"));
        this.personRemoteCnt = new HTMLText(driver, By.xpath("//*[@view_id='personRemoteCnt']"));
        this.personOfficeCnt = new HTMLText(driver, By.xpath("//*[@view_id='personOfficeCnt']"));

        HTMLComponent lastname  = new HTMLText(driver, By.xpath("//*[@view_id='lastname']"));
        HTMLComponent firstname = new HTMLText(driver, By.xpath("//*[@view_id='firstname']"));
        HTMLComponent patronymic = new HTMLText(driver, By.xpath("//*[@view_id='patronymic']"));


        ArrayList<HTMLComponent> personFields = new ArrayList<>(3);
        personFields.add(lastname);
        personFields.add(firstname);
        personFields.add(patronymic);

        HTMLButton addPersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='addPersonsBtn']"));
        HTMLButton editPersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='editPersonsBtn']"));
        HTMLButton removePersonsBtn  = new HTMLButton(driver, By.xpath("//*[@view_id='removePersonsBtn']"));
        HTMLButton clearPersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='clearPersonsBtn']"));

        this.peopleTable = new HTMLTable(driver, By.xpath("//*[@view_id='person_table']"), personFields, addPersonsBtn, removePersonsBtn, editPersonsBtn, clearPersonsBtn); //Table

        this.isAgree = new HTMLCheckBox(driver, By.xpath("//*[@view_id='isAgree']"));
        this.isProtect = new HTMLCheckBox(driver, By.xpath("//*[@view_id='isProtect']"));
        this.sendBtn = new HTMLButton(driver, By.xpath("//*[@view_id='send_btn']"));

        this.errorPane = new HTMLNotificationPane(driver, By.xpath("//div[@class='webix_message']")); //Error pane
        this.finalModalWindow = new HTMLFinalConfirmationModalWindow(driver, By.xpath("//div[@webixbox='1' and @role='alertdialog']"));

    }

    public HTMLText getOrganizationName() {
        return (HTMLText) organizationName;
    }

    public HTMLText getOrganizationShortName() {
        return (HTMLText) organizationShortName;
    }

    public HTMLText getOrganizationInn() {
        return (HTMLText) organizationInn;
    }

    public HTMLText getOrganizationOgrn() {
        return (HTMLText) organizationOgrn;
    }

    public HTMLText getOrganizationEmail() {
        return (HTMLText) organizationEmail;
    }

    public HTMLText getOrganizationPhone() {
        return (HTMLText) organizationPhone;
    }

    public HTMLText getOrganizationOkved() {
        return (HTMLText) organizationOkved;
    }

    public HTMLTextArea getOrganizationOkvedAdd() {
        return (HTMLTextArea) organizationOkvedAdd;
    }

    public HTMLChoiceBox getDepartmentId() {
        return (HTMLChoiceBox) departmentId;
    }

    public HTMLTable getAddressTable() {
        return (HTMLTable) addressTable;
    }

    public HTMLTextArea getOrganizationAddressJur() {return (HTMLTextArea) organizationAddressJur;}

    public HTMLTextArea getReqBasis() {
        return (HTMLTextArea) reqBasis;
    }

    public HTMLComponent getUpload() {
        return upload;
    }


    public HTMLText getPersonSlrySaveCnt() {
        return (HTMLText) personSlrySaveCnt;
    }

    public HTMLText getPersonRemoteCnt() {
        return (HTMLText) personRemoteCnt;
    }

    public HTMLText getPersonOfficeCnt() {
        return (HTMLText) personOfficeCnt;
    }

    public HTMLTable getPeopleTable() {
        return (HTMLTable) peopleTable;
    }


    public HTMLCheckBox getIsAgree() {
        return (HTMLCheckBox) isAgree;
    }

    public HTMLCheckBox getIsProtect() {
        return (HTMLCheckBox) isProtect;
    }

    public HTMLButton getSendBtn() {
        return (HTMLButton) sendBtn;
    }

    public HTMLNotificationPane getErrorPane() {
        return (HTMLNotificationPane) errorPane;
    }

    public HTMLFinalConfirmationModalWindow getFinalModalWindow() {
        return (HTMLFinalConfirmationModalWindow) finalModalWindow;
    }

    public String getPageUrl() {
        return pageUrl;
    }
}
