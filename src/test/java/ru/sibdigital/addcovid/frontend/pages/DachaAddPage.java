package ru.sibdigital.addcovid.frontend.pages;

import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.sibdigital.addcovid.frontend.components.*;

import java.util.ArrayList;
import java.util.List;

public class DachaAddPage {
    private String pageUrl;

    private final HTMLComponent fromDistrict;
    private final HTMLComponent fromAddress;
    private final HTMLComponent toDistrict;
    private final HTMLComponent toAddress;

    //private final HTMLComponent validDate;

    private final HTMLComponent peopleTable;

    private final HTMLComponent isAgree1;
    private final HTMLComponent isAgree2;




    private final HTMLComponent sendBtn;

    private final HTMLComponent finalModalWindow;






    public DachaAddPage(WebDriver driver, BrowserMobProxy proxy, String baseUrl) {

        this.pageUrl = baseUrl+"/dacha";




        driver.get(this.pageUrl);

        HTMLAutoCompleteField.Suggestions suggestions = new HTMLAutoCompleteField.Suggestions(driver, By.xpath("//*[@view_id='district_options']"));
        this.fromDistrict = new HTMLAutoCompleteField(driver, By.xpath("//*[@view_id='raion']"), suggestions);
        this.fromAddress = new HTMLText(driver, By.xpath("//*[@view_id='naspunkt']"));
        this.toDistrict = new  HTMLAutoCompleteField(driver, By.xpath("//*[@view_id='district']"), suggestions);
        this.toAddress = new HTMLText(driver, By.xpath("//*[@view_id='address']"));

        //this.validDate = new HTMLText(driver, By.xpath("//*[@view_id='validDate']"));

        HTMLButton openAddPersonButton =  new HTMLButton(driver, By.xpath("//*[@view_id='add_btn']"));
        openAddPersonButton.submit();

        HTMLComponent lastname  = new HTMLText(driver, By.xpath("//*[@view_id='lastname']"));
        HTMLComponent firstname = new HTMLText(driver, By.xpath("//*[@view_id='firstname']"));
        HTMLComponent patronymic = new HTMLText(driver, By.xpath("//*[@view_id='patronymic']"));
        HTMLComponent age = new HTMLText(driver, By.xpath("//*[@view_id='age']"));


        ArrayList<HTMLComponent> personFields = new ArrayList<>(4);
        personFields.add(lastname);
        personFields.add(firstname);
        personFields.add(patronymic);
        personFields.add(age);

        HTMLButton addPersonsBtn = new HTMLButton(driver, By.xpath("//*[@view_id='add_chk_btn']"));

        this.peopleTable = new HTMLTable(driver, By.xpath("//*[@view_id='person_table']"), personFields, addPersonsBtn); //Table

        this.isAgree1 = new HTMLCheckBox(driver, By.xpath("//*[@view_id='isAgree']"));
        this.isAgree2 = new HTMLCheckBox(driver, By.xpath("//*[@view_id='isProtect']"));
        this.sendBtn = new HTMLButton(driver, By.xpath("//*[@view_id='send_btn']"));


        this.finalModalWindow = new HTMLFinalConfirmationModalWindow(driver, By.xpath("//div[@webixbox='1' and @role='alertdialog']"));


    }

    public HTMLAutoCompleteField getFromDistrict() {
        return (HTMLAutoCompleteField) fromDistrict;
    }

    public HTMLText getFromAddress() {
        return (HTMLText) fromAddress;
    }

    public HTMLAutoCompleteField getToDistrict() {
        return (HTMLAutoCompleteField) toDistrict;
    }

    public HTMLText getToAddress() {
        return (HTMLText) toAddress;
    }

    public HTMLTable getPeopleTable() {
        return (HTMLTable) peopleTable;
    }

    public HTMLCheckBox getIsAgree1() {
        return (HTMLCheckBox) isAgree1;
    }

    public HTMLCheckBox getIsAgree2() {
        return (HTMLCheckBox) isAgree2;
    }

    public HTMLButton getSendBtn() {
        return (HTMLButton) sendBtn;
    }

    public HTMLFinalConfirmationModalWindow getFinalModalWindow() {
        return (HTMLFinalConfirmationModalWindow) finalModalWindow;
    }

    public String getPageUrl() {
        return pageUrl;
    }
}
