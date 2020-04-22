package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLMessagePane extends HTMLComponent{



    public HTMLMessagePane(WebDriver driver, By selector) {
        this.driver = driver;
        this.selector = selector;
    }

    @Override
    public WebElement getElement() {
        return this.driver.findElement(selector);
    }




}
