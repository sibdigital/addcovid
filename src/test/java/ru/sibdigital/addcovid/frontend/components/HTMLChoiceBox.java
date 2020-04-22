package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HTMLChoiceBox extends HTMLComponent {
    public HTMLChoiceBox(WebDriver driver, By selector) {
        super(driver, selector);
    }

    public List<WebElement> getAvailableValues(){
        return this.getElement().findElements(By.xpath("//select/option"));

    }

}
