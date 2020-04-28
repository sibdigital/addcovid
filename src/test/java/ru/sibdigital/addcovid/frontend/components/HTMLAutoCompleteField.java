package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HTMLAutoCompleteField extends HTMLChoiceBox {

    public static class Suggestions extends HTMLComponent{
        public Suggestions(WebDriver driver, By selector) {
            this.driver = driver;
            this.selector = selector;
        }

        public List<WebElement> getSuggestions(){
           return this.getElement().findElements(By.className("webix_list_item"));
        }
    }



    private final Suggestions suggestions;

    public HTMLAutoCompleteField(WebDriver driver, By selector, Suggestions suggestions) {
        super(driver, selector);
        this.suggestions = suggestions;
    }

    @Override
    public List<WebElement> getAvailableValues(){
        this.getElement().findElement(By.tagName("input")).click();
        return this.suggestions.getSuggestions();
    }

    @Override
    public void setText(String text){
        WebElement input = this.getElement().findElement(By.tagName("input"));
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(Keys.DELETE);
        input.sendKeys(text);
    }

}
