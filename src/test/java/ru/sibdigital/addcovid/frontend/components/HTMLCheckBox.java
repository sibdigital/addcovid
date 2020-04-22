package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HTMLCheckBox extends HTMLComponent {
    public HTMLCheckBox(WebDriver driver, By selector) {
        super(driver, selector);
    }
/*
    public void setText(String text){
        this.getElement().findElement(By.tagName("textarea")).sendKeys(text);
    }*/

    public void setChecked(boolean value){
        WebElement button = this.getElement().findElement(By.tagName("button"));
        boolean isChecked = button.getAttribute("aria-checked").equals("true");
        if(value != isChecked) {
            button.click();
        }
    }

    public boolean getChecked(){
        return this.getElement().findElement(By.tagName("button")).getAttribute("aria-checked").equals("true");
    }



}
