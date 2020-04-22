package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLComponent {


    protected By selector;
    protected WebDriver driver;

    public HTMLComponent() {
    }

    public HTMLComponent(WebDriver driver, By selector) {
        this.driver = driver;
        this.selector = selector;
        this.getElement().findElement(selector);
    }



    public WebElement getElement() {
        return this.driver.findElement(selector);
    }

    public WebElement getContainer(){
        return this.getElement();
    }

    public boolean isDisabled() {
        return !this.getElement().isEnabled();
    }

    public boolean isVisible() {
        WebElement element = this.getElement().findElement(By.tagName("button"));
        return element.isDisplayed() & !"none".equals(element.getCssValue("display"));
    }

}
