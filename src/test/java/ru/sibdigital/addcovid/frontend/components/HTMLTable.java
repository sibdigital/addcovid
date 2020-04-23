package ru.sibdigital.addcovid.frontend.components;

import lombok.Data;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
@Data
public class HTMLTable extends HTMLComponent {

    private HTMLButton addButton;
    private HTMLButton deleteButton;
    private HTMLButton editButton;
    private HTMLButton clearButton;
    private List<HTMLComponent> textFields;




    public HTMLTable(WebDriver driver,
                     By selector,
                     List<HTMLComponent> textFields,
                     HTMLButton addButton,
                     HTMLButton deleteButton,
                     HTMLButton editButton,
                     HTMLButton clearButton
    ){
        this(driver,selector,textFields,addButton,deleteButton,editButton);
        this.clearButton = clearButton;
    }

    public HTMLTable(WebDriver driver,
                     By selector,
                     List<HTMLComponent> textFields,
                     HTMLButton addButton,
                     HTMLButton deleteButton,
                     HTMLButton editButton
    ){
        super(driver, selector);
        this.textFields = textFields;
        this.addButton = addButton;
        this.deleteButton = deleteButton;
        this.editButton = editButton;
    }


    public WebElement getCell(int rowIndex ,int columnIndex){
        return this.getElement().findElement(By.xpath(String.format(".//div[@aria-colindex='%d' and @aria-rowindex='%d']", columnIndex+1,rowIndex+1)));
    }

    public int getColumnSize(){
        return Integer.parseInt(this.getElement().getAttribute("aria-colcount"));
    }

    public int getRowsSize(){
        return Integer.parseInt(this.getElement().getAttribute("aria-rowcount"));
    }

    public void addValue(String... rowValues){

        if(rowValues.length > textFields.size()) throw new IllegalArgumentException(String.format("Can add maximum $d elements, but you've tried %d", textFields.size(),rowValues.length));

        for (int i = 0; i < rowValues.length; i++) {
            textFields.get(i).setText(rowValues[i]);
        }
        this.addButton.submit();
    }

    public void deleteRow(int rowIndex){
        if(rowIndex+1 <= this.getRowsSize()) {
            this.getCell(rowIndex, 0).click();
            this.deleteButton.submit();
            this.confirmDeletion();
        }
    }

    public void clearTable(){
        if(this.getRowsSize() != 0){
            if(this.clearButton != null) {
                this.clearButton.submit();
                this.confirmDeletion();
            } else {
                for (int i = 0; i < getRowsSize(); i++) {
                    getCell(i,0).click();
                    deleteButton.submit();
                    this.confirmDeletion();
                }
            }
        }
    }

    public void editRow(int rowIndex, String... rowNewValues){

        int rowsSize = this.getRowsSize();

        if(rowIndex+1 > rowsSize) {
            throw new IndexOutOfBoundsException(String.format("index %d is out of size %d", rowIndex, rowsSize));
        }

        if(rowNewValues.length > textFields.size()) {
            throw new IllegalArgumentException(String.format("Can add maximum $d elements, but you've tried %d", textFields.size(),rowNewValues.length));
        }


        this.getCell(rowIndex,0).click();

        for (int i = 0; i < rowNewValues.length; i++) {
            textFields.get(i).setText(rowNewValues[i]);
        }
        this.editButton.submit();
    }


    public void declineDeletion(){
        this.driver.findElement(By.xpath("//div[@role='button' and @aria-label='Отмена' and @class='webix_popup_button' and @result='false']")).click();
    }

    public void confirmDeletion(){
        this.driver.findElement(By.xpath("//div[@role='button' and @aria-label='OK' and @class='webix_popup_button confirm' and @result='true']")).click();
    }


}
