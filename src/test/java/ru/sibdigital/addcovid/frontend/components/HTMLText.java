package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLText extends HTMLComponentWithErrorLabel {
    public HTMLText(WebDriver driver, By selector) {
        super(driver, selector);
    }


    public void setText(String text){
        WebElement input = this.getElement().findElement(By.tagName("input"));
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(Keys.DELETE);
        input.sendKeys(text);
    }

}