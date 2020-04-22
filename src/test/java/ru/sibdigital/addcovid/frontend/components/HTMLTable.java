package ru.sibdigital.addcovid.frontend.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HTMLTable extends HTMLComponent {





    public HTMLTable(WebDriver driver, By selector) {
        super(driver, selector);
    }

    public WebElement getCell(int rowIndex ,int columnIndex){
        return this.getElement().findElement(By.xpath(String.format("/div[@aria-colindex=&d @aria-rowindex=%d]", columnIndex, rowIndex)));
    }

    public int getColumnSize(){
        return Integer.parseInt(this.getElement().getAttribute("aria-colcount"));
    }

    public int getRowsSize(){
        return Integer.parseInt(this.getElement().getAttribute("aria-rowcount"));
    }

}
