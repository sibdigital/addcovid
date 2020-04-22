package ru.sibdigital.addcovid.frontend.pages;

import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.sibdigital.addcovid.frontend.components.*;

public class OrganizationAddPage {

    HTMLComponent organizationName;
    HTMLComponent organizationShortName;
    HTMLComponent organizationInn;
    HTMLComponent organizationOgrn;
    HTMLComponent organizationEmail;
    HTMLComponent organizationPhone;
    HTMLComponent organizationOkved;
    HTMLComponent organizationOkvedAdd;
    HTMLComponent departmentId;

    HTMLComponent addressFact;
    HTMLComponent personOfficeFactCnt;
    HTMLComponent buttonAddrAdd;
    HTMLComponent buttonAddrEdit;
    HTMLComponent buttonAddrRemove;


    HTMLComponent reqBasis;
    HTMLComponent upload;

    HTMLComponent personSlrySaveCnt;
    HTMLComponent personRemoteCnt;
    HTMLComponent personOfficeCnt;

    HTMLComponent persons;
    HTMLComponent lastname;
    HTMLComponent firstname;
    HTMLComponent patronymic;
    HTMLComponent addPersonsBtn;
    HTMLComponent editPersonsBtn;
    HTMLComponent removePersonsBtn;
    HTMLComponent clearPersonsBtn;

    HTMLComponent isAgree;
    HTMLComponent isProtect;
    HTMLComponent sendBtn;

    HTMLComponent errorPane;



    public OrganizationAddPage(WebDriver driver, BrowserMobProxy proxy, String baseUrl) {

        String url = baseUrl+"/form";



        // create a new HAR with the label "yahoo.com"
        proxy.newHar(url);
        driver.get(url);
        this.organizationName = new HTMLText(driver, By.xpath("//*[@view_id='organizationName']"));
        this.organizationShortName = new HTMLText(driver, By.xpath("//*[@view_id='organizationShortName']"));
        this.organizationInn = new HTMLText(driver, By.xpath("//*[@view_id='organizationInn']"));
        this.organizationOgrn = new HTMLText(driver, By.xpath("//*[@view_id='organizationOgrn']"));
        this.organizationEmail = new HTMLText(driver, By.xpath("//*[@view_id='organizationEmail']"));
        this.organizationPhone = new HTMLText(driver, By.xpath("//*[@view_id='organizationPhone']"));
        this.organizationOkved = new HTMLText(driver, By.xpath("//*[@view_id='organizationOkved']"));
        this.organizationOkvedAdd = new HTMLTextArea(driver, By.xpath("//*[@view_id='organizationOkvedAdd']"));
        this.departmentId = new HTMLChoiceBox(driver, By.xpath("//*[@view_id='departmentId']"));


        this.addressFact = new HTMLText(driver, By.xpath("//*[@view_id='addressFactText']"));
        this.personOfficeFactCnt = new HTMLText(driver, By.xpath("//*[@view_id='personOfficeFactCntText']"));
        this.buttonAddrAdd = new HTMLText(driver, By.xpath("//*[@view_id='buttonAddrAdd']"));
        this.buttonAddrEdit = new HTMLButton(driver, By.xpath("//*[@view_id='buttonAddrEdit']"));
        this.buttonAddrRemove = new HTMLButton(driver, By.xpath("//*[@view_id='buttonAddrRemove']"));


        this.reqBasis = new HTMLTextArea(driver, By.xpath("//*[@view_id='reqBasis']"));
        this.upload = new HTMLText(driver, By.xpath("//*[@view_id='upload']")); //File

        this.personSlrySaveCnt = new HTMLText(driver, By.xpath("//*[@view_id='personSlrySaveCnt']"));
        this.personRemoteCnt = new HTMLText(driver, By.xpath("//*[@view_id='personRemoteCnt']"));
        this.personOfficeCnt = new HTMLText(driver, By.xpath("//*[@view_id='personOfficeCnt']"));

        this.persons = new HTMLText(driver, By.xpath("//*[@view_id='person_table']")); //Table
        this.lastname = new HTMLText(driver, By.xpath("//*[@view_id='lastname']"));
        this.firstname = new HTMLText(driver, By.xpath("//*[@view_id='firstname']"));
        this.patronymic = new HTMLText(driver, By.xpath("//*[@view_id='patronymic']"));
        this.addPersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='addPersonsBtn']"));
        this.editPersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='editPersonsBtn']"));
        this.removePersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='removePersonsBtn']"));
        this.clearPersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='clearPersonsBtn']"));

        this.isAgree = new HTMLCheckBox(driver, By.xpath("//*[@view_id='isAgree']"));
        this.isProtect = new HTMLCheckBox(driver, By.xpath("//*[@view_id='isProtect']"));
        this.sendBtn = new HTMLButton(driver, By.xpath("//*[@view_id='send_btn']"));

        this.errorPane = new HTMLMessagePane(driver, By.xpath("//div[@class='webix_message webix_error']")); //Error pane
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

    public HTMLText getAddressFact() {
        return (HTMLText) addressFact;
    }

    public HTMLText getPersonOfficeFactCnt() {
        return (HTMLText) personOfficeFactCnt;
    }

    public HTMLButton getButtonAddrAdd() {
        return (HTMLButton) buttonAddrAdd;
    }

    public HTMLButton getButtonAddrEdit() {
        return (HTMLButton) buttonAddrEdit;
    }

    public HTMLButton getButtonAddrRemove() {
        return (HTMLButton) buttonAddrRemove;
    }

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

    public HTMLComponent getPersons() {
        return persons;
    }

    public HTMLText getLastname() {
        return (HTMLText) lastname;
    }

    public HTMLText getFirstname() {
        return (HTMLText) firstname;
    }

    public HTMLText getPatronymic() {
        return (HTMLText) patronymic;
    }

    public HTMLButton getAddPersonsBtn() {
        return (HTMLButton) addPersonsBtn;
    }

    public HTMLButton getEditPersonsBtn() {
        return (HTMLButton) editPersonsBtn;
    }

    public HTMLButton getRemovePersonsBtn() {
        return (HTMLButton) removePersonsBtn;
    }

    public HTMLButton getClearPersonsBtn() {
        return (HTMLButton) clearPersonsBtn;
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

    public HTMLMessagePane getErrorPane() {
        return (HTMLMessagePane) errorPane;
    }
}
