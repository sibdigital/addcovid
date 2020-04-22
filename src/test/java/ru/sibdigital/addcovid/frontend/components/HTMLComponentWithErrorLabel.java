package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLComponentWithErrorLabel extends HTMLComponent {
    public HTMLComponentWithErrorLabel(WebDriver driver, By selector) {
        super(driver,selector);
    }

    public String getErrorText(){
        String error = "";
        try {
             error = this.getElement().findElement(By.xpath("./div[@class='webix_inp_bottom_label']")).getText();
        } catch (NoSuchElementException e){

        }
        return error;
    }

    public boolean isErrorShown(){


        boolean isDisplayed = false;
        try {
            WebElement element = this.getElement().findElement(By.xpath("/div[@class='webix_inp_bottom_label]"));
            isDisplayed = element.isDisplayed() || !"none".equals(element.getCssValue("display"));
        } catch (NoSuchElementException e){

        }


        return isDisplayed;
    }
}
