package ru.sibdigital.addcovid.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.DocEmployee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ExcelWriter {

    final String[] SHEET_NAMES = {"РАБОТНИКИ ВЫХОДЯЩИЕ НА РАБОТУ"};
    final int SHEET_PEOPLE_INDEX = 0;

    public File createEmployeesFile(List<DocEmployee> employees) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAMES[SHEET_PEOPLE_INDEX]);

        File tempFile = File.createTempFile("Employees", ".xlsx");

        Row firstRow = sheet.createRow(0);
        Cell cell1 = firstRow.createCell(0);
        cell1.setCellValue("Фамилия");
        Cell cell2 = firstRow.createCell(1);
        cell2.setCellValue("Имя");
        Cell cell3 = firstRow.createCell(2);
        cell3.setCellValue("Отчество");

        int rowCount = 1;
        for (DocEmployee employee : employees) {
            Row row = sheet.createRow(rowCount++);

            Cell cellFirstName = row.createCell(0);
            cellFirstName.setCellValue(employee.getPerson().getFirstname());

            Cell cellLastName = row.createCell(1);
            cellLastName.setCellValue(employee.getPerson().getLastname());

            Cell cellPatronymic = row.createCell(2);
            cellPatronymic.setCellValue(employee.getPerson().getPatronymic());
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            workbook.write(outputStream);
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempFile;
    }
}
