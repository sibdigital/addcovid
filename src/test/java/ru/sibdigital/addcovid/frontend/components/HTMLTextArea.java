package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HTMLTextArea extends HTMLComponent {
    public HTMLTextArea(WebDriver driver, By selector) {
        super(driver, selector);
    }

    public void setText(String text){
        this.getElement().findElement(By.tagName("textarea")).sendKeys(text);
    }



}
