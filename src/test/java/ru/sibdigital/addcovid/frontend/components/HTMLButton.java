package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLButton extends HTMLComponent {
    public HTMLButton(WebDriver driver, By selector) {
        super(driver, selector);
    }

    public void submit(){

        this.getElement().findElement(By.tagName("button")).click();
    }



    public WebElement getAsWebElement(){
        return this.getElement().findElement(By.tagName("button"));
    }

    @Override
    public boolean isDisabled() {
        return !this.getElement().findElement(By.tagName("button")).isEnabled();
    }

    @Override
    public boolean isVisible() {
        WebElement button = this.getElement().findElement(By.tagName("button"));
        return button.isDisplayed() & !"none".equals(button.getCssValue("display"));
    }


}
