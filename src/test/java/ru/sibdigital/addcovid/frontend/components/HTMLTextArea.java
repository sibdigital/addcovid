package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLTextArea extends HTMLComponentWithErrorLabel {
    public HTMLTextArea(WebDriver driver, By selector) {
        super(driver, selector);
    }

    @Override
    public void setText(String text){
        WebElement input = this.getElement().findElement(By.tagName("textarea"));
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(Keys.DELETE);
        input.sendKeys(text);
    }



}
