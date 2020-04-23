package ru.sibdigital.addcovid.frontend.components;

import lombok.Data;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@Data
public class HTMLFinalConfirmationModalWindow extends HTMLComponent {




    public HTMLFinalConfirmationModalWindow(WebDriver driver, By selector){
        this.driver = driver;
        this.selector = selector;
    }


    public String getText(){
        return this.getElement().findElement(By.xpath(".//div[@class='webix_popup_text']/span")).getText();
    }

    public void closeWindow(){
        this.getElement().findElement(By.xpath(".//div[@role='button' and @class='webix_popup_button confirm' and @result='true']")).click();
    }

    public void newRequest(){
        this.getElement().findElement(By.xpath(".//div[@role='button' and @class='webix_popup_button' and @result='false']")).click();
    }


}
