package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HTMLText extends HTMLComponent {
    public HTMLText(WebDriver driver, By selector) {
        super(driver, selector);
    }


    public void setText(String text){
        this.getElement().findElement(By.tagName("input")).sendKeys(text);
    }

    public String getErrorText(){
        return "";
    }

}
