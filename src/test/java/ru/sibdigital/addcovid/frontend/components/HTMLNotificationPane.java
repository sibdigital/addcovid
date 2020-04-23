package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLNotificationPane extends HTMLComponent{



    public HTMLNotificationPane(WebDriver driver, By selector) {
        this.driver = driver;
        this.selector = selector;
    }

    @Override
    public WebElement getElement() {
        return this.driver.findElement(selector);
    }




}
