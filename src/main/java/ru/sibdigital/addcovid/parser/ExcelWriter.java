package ru.sibdigital.addcovid.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.sibdigital.addcovid.model.DocEmployee;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ExcelWriter {

    final String[] SHEET_NAMES = {"РАБОТНИКИ ВЫХОДЯЩИЕ НА РАБОТУ"};
    final int SHEET_PEOPLE_INDEX = 0;
    final String[] EMPLOYEES_COLUMN_NAMES = {"Фамилия", "Имя", "Отчество"};
    final String EMPLOYEE_FILENAME = "Employees.xlsx";

    public void downloadEmployeesFile(List<DocEmployee> employees, HttpServletResponse response) throws IOException {

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", EMPLOYEE_FILENAME);
        response.setHeader(headerKey, headerValue);
        response.setContentType("application/octet-stream");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(SHEET_NAMES[SHEET_PEOPLE_INDEX]);

        Row firstRow = sheet.createRow(0);
        firstRow.setRowStyle(getFirstRowStyle(workbook));

        for (int i = 0; i < EMPLOYEES_COLUMN_NAMES.length; i++) {
            Cell cell = firstRow.createCell(i);
            cell.setCellValue(EMPLOYEES_COLUMN_NAMES[i]);
        }

        int rowCount = 1;
        for (DocEmployee employee : employees) {
            createEmployeeRow(sheet, employee, rowCount++);
        }

        sheet.setDefaultColumnWidth(20);

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }


    private void createEmployeeRow(XSSFSheet sheet, DocEmployee employee, int rowCount) {
        Row row = sheet.createRow(rowCount);

        Cell cellLastName = row.createCell(0);
        Cell cellFirstName = row.createCell(1);
        Cell cellPatronymic = row.createCell(2);

        cellFirstName.setCellValue(employee.getPerson().getFirstname());
        cellLastName.setCellValue(employee.getPerson().getLastname());
        cellPatronymic.setCellValue(employee.getPerson().getPatronymic());
    }

    private XSSFCellStyle getFirstRowStyle(XSSFWorkbook workbook ) {
        XSSFCellStyle style = workbook.createCellStyle();

        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);

        style.setFont(boldFont);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }
}
