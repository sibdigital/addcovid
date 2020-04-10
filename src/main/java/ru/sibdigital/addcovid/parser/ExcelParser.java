package ru.sibdigital.addcovid.parser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.FactAddressDto;
import ru.sibdigital.addcovid.dto.PersonDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.ClsDepartment;
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelParser {

    @Autowired
    ClsDepartmentRepo clsDepartmentRepo;


    final String[] SHEET_NAMES = {"ДАННЫЕ О ВАШЕЙ ОРГАНИЗАЦИИ", "АДРЕСНАЯ ИНФОРМАЦИЯ", "РАБОТНИКИ ВЫХОДЯЩИЕ НА РАБОТУ"};

    final String[] ADDRESS_COLUMNS_NAMES = {"Фактический адрес осуществления деятельности","Численность работников, не подлежащих переводу на дистанционный режим работы, осуществляющих деятельность  фактическому адресу"};
    final String[] PEOPLE_COLUMNS_NAMES = {"Фамилия","Имя","Отчество"};

    final String[] COMMON_ORGANIZATION_INFO = {
            "*Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя: ",
            "*Краткое наименование организации",
            "*ИНН (10 или 12 цифр)",
            "*ОГРН  (13 цифр)",
            "*e-mail",
            "*Телефон (любой формат)"
    };

    final String[] ORGANIZATION_ACTIVITY_INFO = {
            "*Основной вид осуществляемой деятельности (отрасль)",
            "Дополнительные виды осуществляемой деятельности (через запятую)",
            "*Номер Министерства, курирующее вашу деятельность (берется № из листа Справочник министерств)"
    };

    final String[] ORGANIZATION_NUMBER_INFO = {
            "* Юридический адрес",
            "* Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы",
            "* Суммарная численность работников, подлежащих переводу на дистанционный режим работы",
            "* Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)"
    };

    final int SHEET_ORGANIZATION_INFO_INDEX = 0;
    final int SHEET_ADDRESSES_INDEX = 1;
    final int SHEET_PEOPLE_INDEX = 2;

    public CheckProtocol parseFile(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        String name = file.getOriginalFilename();
        return parseFile(name, inputStream);
    }


    public CheckProtocol parseFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        String name = file.getName();
        return parseFile(name, inputStream);
    }

    private CheckProtocol parseFile(String name, InputStream inputStream) throws IOException {
        String[] split = name.split("\\.");
        String ext = split[split.length-1];
        if(ext.equals("xls")){
            return readXLSFile(inputStream);
        } else if(ext.equals("xlsx")) {
            return readXLSXFile(inputStream);
        } else {
            throw new IOException("Не возможно обработать файл в формате ." + ext);
        }
    }


    private CheckProtocol readXLSFile(InputStream inputStream) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        // Get first sheet from the workbook
        Sheet sheetOrganizationInfo = workbook.getSheetAt(SHEET_ORGANIZATION_INFO_INDEX);
        Sheet sheetAddressesInfo = workbook.getSheetAt(SHEET_ADDRESSES_INDEX);
        Sheet sheetPeopleInfo = workbook.getSheetAt(SHEET_PEOPLE_INDEX);

        return parse(sheetOrganizationInfo, sheetAddressesInfo, sheetPeopleInfo);
    }

    private CheckProtocol readXLSXFile(InputStream inputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        // Get first sheet from the workbook

        Sheet sheetOrganizationInfo = workbook.getSheetAt(SHEET_ORGANIZATION_INFO_INDEX);
        Sheet sheetAddressesInfo = workbook.getSheetAt(SHEET_ADDRESSES_INDEX);
        Sheet sheetPeopleInfo = workbook.getSheetAt(SHEET_PEOPLE_INDEX);

        return parse(sheetOrganizationInfo, sheetAddressesInfo, sheetPeopleInfo);
    }

    private CheckProtocol parse(Sheet ...sheets) throws IOException {

        DataFormatter fmt = new DataFormatter();

        PostFormDto postFormDto = new PostFormDto();
        CheckProtocol checkProtocol = new CheckProtocol(postFormDto);

        boolean organizationInfoTemplateIsCorrect = this.checkSheetWithOrganizationInfo(sheets, SHEET_ORGANIZATION_INFO_INDEX, fmt);
        Iterator<Row> sheetAddressesRowsIterator = this.checkSheetWithTables(sheets, SHEET_ADDRESSES_INDEX, ADDRESS_COLUMNS_NAMES,fmt);
        Iterator<Row> sheetPeopleRowsIterator = this.checkSheetWithTables(sheets, SHEET_PEOPLE_INDEX, PEOPLE_COLUMNS_NAMES,fmt);

        if(!organizationInfoTemplateIsCorrect) {
            throw new IOException("Неизвестная ошибка при обработке страницы: " + SHEET_NAMES[0]);
        }
        if(sheetAddressesRowsIterator == null) {
            throw new IOException("Неизвестная ошибка при обработке страницы: " + SHEET_NAMES[1]);
        }
        if(sheetPeopleRowsIterator == null) {
            throw new IOException("Неизвестная ошибка при обработке страницы: " + SHEET_NAMES[2]);
        }



        checkProtocol = this.parseSheetWithAddresses(sheetAddressesRowsIterator, checkProtocol, fmt);
        checkProtocol = this.parseSheetWithPeople(sheetPeopleRowsIterator, checkProtocol, fmt);


        return checkProtocol;


    }

    private boolean checkSheetWithOrganizationInfo(final Sheet[] sheets, final int sheetIndex, final DataFormatter fmt) throws IOException {
        Sheet sheet = sheets[sheetIndex];
        int COMMON_ORGANIZATION_INFO_INDEX_START = 1;
        int ACTIVITY_INDEX_START = 1;
        int NUMBER_INDEX_START = 10;




        if(!SHEET_NAMES[sheetIndex].equals(sheet.getSheetName())){
            throw new IOException(String.format("Неверное содержание файла. %d-ия страниц должна иметь имя: %s", sheetIndex, SHEET_NAMES[sheetIndex]));
        }


        checkCommonInfo(sheetIndex, fmt, sheet, COMMON_ORGANIZATION_INFO, COMMON_ORGANIZATION_INFO_INDEX_START, 0);
        checkCommonInfo(sheetIndex, fmt, sheet, ORGANIZATION_ACTIVITY_INFO, ACTIVITY_INDEX_START, 3);
        checkCommonInfo(sheetIndex, fmt, sheet, ORGANIZATION_NUMBER_INFO, NUMBER_INDEX_START, 0);

        return true;
    }

    private void checkCommonInfo(int sheetIndex, DataFormatter fmt, Sheet sheet, final String[] ROWS_LABELS, final int ROW_INDEX_START, final int COLUMN_INDEX) throws IOException {
        char[] CELLS_CHARS = {'A', 'B', 'C', 'D', 'E'};
        Cell cell;
        for (int i = 0; i < ROWS_LABELS.length; i++) {
            cell = sheet.getRow(ROW_INDEX_START + i).getCell(COLUMN_INDEX, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String s = fmt.formatCellValue(cell);
            if(!ROWS_LABELS[i].equals(s)){
                throw new IOException(String.format("Неверное содержание файла. Страница %s - Ячейка %c%d должна именноваться: %s", SHEET_NAMES[sheetIndex], CELLS_CHARS[COLUMN_INDEX],ROW_INDEX_START +i+1, ROWS_LABELS[i] ));
            }
        }
    }


    private Iterator<Row> checkSheetWithTables(final Sheet[] sheets, final int sheetIndex, final String[] columnsNames, final DataFormatter fmt) throws IOException {
        Sheet sheet = sheets[sheetIndex];
        if(!SHEET_NAMES[sheetIndex].equals(sheet.getSheetName())){
            throw new IOException(String.format("Неверное содержание файла. %d-ия страниц должна иметь имя: %s", sheetIndex, SHEET_NAMES[sheetIndex]));
        }

        Iterator<Row> rowIterator = sheet.iterator();
        if(rowIterator.hasNext()) {
            Row names = rowIterator.next();
            for (int i = 0; i < columnsNames.length; i++) {
                if(!columnsNames[i].equals(fmt.formatCellValue(names.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)))){
                    throw new IOException(String.format("Неверное содержание файла. Страница %s - колонка № %d должна именноваться: %s", SHEET_NAMES[sheetIndex], i+1, columnsNames[i]));
                }
            }
        }

        return rowIterator;
    }

    private CheckProtocol parseSheetWithOrganizationInfo(Sheet sheet, CheckProtocol checkProtocol, DataFormatter fmt){
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Cell cell = sheet.getRow(1).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя:
        String text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationName(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationNameStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
        }

        cell = sheet.getRow(2).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Краткое наименование организации
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationShortName(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationShortNameStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
        }

        cell = sheet.getRow(3).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);// *ИНН (10 или 12 цифр)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationInn(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationInnStatus("Не может быть пустым");
        } else {
            if(text.length() != 10 && text.length() != 12 ) {
                postFormDto.setOrganizationInnStatus("Должно быть длиной 10 или 12 символов");
                checkProtocol.setSuccess( false );
            }
        }

        cell = sheet.getRow(4).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *ОГРН  (13 цифр)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationOgrn(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationOgrnStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
        } else {
            if(text.length() != 10 && text.length() != 12 ) {
                postFormDto.setOrganizationOgrnStatus("Должно быть длиной 10 или 12 символов");
                checkProtocol.setSuccess( false );
            }
        }

        cell = sheet.getRow(4).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *e-mail
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationEmail(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationEmailStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
        }

        cell = sheet.getRow(4).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Телефон (любой формат)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationPhone(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationPhoneStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
        }


        /*===============================*/


        cell = sheet.getRow(1).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Основной вид осуществляемой деятельности (отрасль)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationOkved(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationOkvedStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
        }

        cell = sheet.getRow(2).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Основной вид осуществляемой деятельности (отрасль)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationOkvedAdd(text);

        cell = sheet.getRow(1).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Номер Министерства, курирующее вашу деятельность (берется № из листа Справочник министерств)

        text = fmt.formatCellValue(cell).trim();
        if(StringUtils.isBlank(text)){
            postFormDto.setDepartmentIdStatus(String.format("Не может быть пустым"));
            checkProtocol.setSuccess(false);
        } else {
            try{
                postFormDto.setDepartmentId(Long.valueOf(text));
                ClsDepartment department = clsDepartmentRepo.findById(postFormDto.getDepartmentId()).orElseGet(() -> null);
                if(department == null) {
                    postFormDto.setDepartmentIdStatus(String.format("Нет министерства под номером %s", text));
                }
            } catch (NumberFormatException e) {
                checkProtocol.setSuccess( false );
                postFormDto.setDepartmentIdStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                postFormDto.setDepartmentId(null);
            }
        }

        /*================================*/

        cell = sheet.getRow(10).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Юридический адрес
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationAddressJur(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationAddressJurStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
        }

        cell = sheet.getRow(10).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы
        text = fmt.formatCellValue(cell).trim();
        if(StringUtils.isBlank(text)){
            postFormDto.setPersonSlrySaveCntStatus(String.format("Не может быть пустым"));
            checkProtocol.setSuccess(false);
        } else {
            try{
                postFormDto.setPersonSlrySaveCnt(Long.valueOf(text));
            } catch (NumberFormatException e) {
                checkProtocol.setSuccess( false );
                postFormDto.setPersonSlrySaveCntStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                postFormDto.setPersonSlrySaveCnt(null);
            }
        }

        cell = sheet.getRow(11).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Суммарная численность работников, подлежащих переводу на дистанционный режим работы
        text = fmt.formatCellValue(cell).trim();
        if(StringUtils.isBlank(text)){
            postFormDto.setPersonRemoteCntStatus(String.format("Не может быть пустым"));
            checkProtocol.setSuccess(false);
        } else {
            try{
                postFormDto.setPersonRemoteCnt(Long.valueOf(text));
            } catch (NumberFormatException e) {
                checkProtocol.setSuccess( false );
                postFormDto.setPersonRemoteCntStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                postFormDto.setPersonRemoteCnt(null);
            }
        }

        cell = sheet.getRow(11).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)
        text = fmt.formatCellValue(cell).trim();
        if(StringUtils.isBlank(text)){
            postFormDto.setPersonOfficeCntStatus(String.format("Не может быть пустым"));
            checkProtocol.setSuccess(false);
        } else {
            try{
                postFormDto.setPersonRemoteCnt(Long.valueOf(text));
            } catch (NumberFormatException e) {
                checkProtocol.setSuccess( false );
                postFormDto.setPersonOfficeCntStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                postFormDto.setPersonOfficeCnt(null);
            }
        }

        return checkProtocol;

    }

    private CheckProtocol parseSheetWithAddresses(Iterator<Row> rowIterator, CheckProtocol checkProtocol, DataFormatter fmt) {


        Row row;

        List<Integer> emptyRows = new ArrayList<>(750);

        List<FactAddressDto> addresses = new ArrayList<>(1500);
        int i = 0;
        while (rowIterator.hasNext()){
            FactAddressDto addressInfo = new FactAddressDto();
            row = rowIterator.next();
            for (int j = 0; j < ADDRESS_COLUMNS_NAMES.length; j++) {
                String text = fmt.formatCellValue(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
                switch (j){
                    case 0:
                        addressInfo.setAddressFact(text);
                        break;
                    case 1:
                        if(StringUtils.isBlank(text)){
                            addressInfo.setStatus(String.format("Поле \"%s\" не может быть пустым",ADDRESS_COLUMNS_NAMES[j]));
                            checkProtocol.setSuccess(false);
                        } else {
                            try{
                                addressInfo.setPersonOfficeFactCnt(Long.valueOf(text));
                            } catch (NumberFormatException e) {
                                checkProtocol.setSuccess( false );
                                addressInfo.setStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                                addressInfo.setPersonOfficeFactCnt(null);
                            }
                        }
                        break;
                }
            }

            if(StringUtils.isBlank(addressInfo.getAddressFact()) && addressInfo.getPersonOfficeFactCnt() == null){
                emptyRows.add(i+1);
            } else{
                addressInfo.setStatus("OK");
                if(StringUtils.isBlank(addressInfo.getAddressFact())) {
                    addressInfo.setStatus(String.format("Поле \"%s\" не может быть пустым",ADDRESS_COLUMNS_NAMES[0]));
                    checkProtocol.setSuccess( false );
                }
                addresses.add(addressInfo);
            }
            ++i;
        }
        if(addresses.size() == 0 ) {
            checkProtocol.setSuccess( false );
            checkProtocol.getPostFormDto().setAddressFactStatus("Список не может быть пустым!");

        }
        checkProtocol.getPostFormDto().setAddressFact(addresses);
        checkProtocol.setAddressesEmptyRowsInExcel(emptyRows);

        return checkProtocol;

    }

    private CheckProtocol parseSheetWithPeople(Iterator<Row> rowIterator, CheckProtocol checkProtocol, DataFormatter fmt) {


        Row row;

        List<Integer> emptyRows = new ArrayList<>(750);

        List<PersonDto> persons = new ArrayList<>(1500);
        int i = 0;
        while (rowIterator.hasNext()){
            PersonDto personInfo = new PersonDto();
            row = rowIterator.next();
            for (int j = 0; j < PEOPLE_COLUMNS_NAMES.length; j++) {
                String text = fmt.formatCellValue(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                switch (j){
                    case 0:
                        personInfo.setLastname(text.trim());
                        break;
                    case 1:
                        personInfo.setFirstname(text.trim());
                        break;
                    case 2:
                        personInfo.setPatronymic(text.trim());
                        break;
                }
            }

            if(StringUtils.isBlank(personInfo.getFirstname()) && StringUtils.isBlank(personInfo.getLastname())){
                emptyRows.add(i+1);
            } else{
                personInfo.setStatus("OK");
                if(StringUtils.isBlank(personInfo.getFirstname())) {
                    personInfo.setStatus(String.format("Поле \"%s\" не может быть пустым",PEOPLE_COLUMNS_NAMES[0]));
                    checkProtocol.setSuccess( false );
                }
                if(StringUtils.isBlank(personInfo.getLastname())) {
                    personInfo.setStatus(String.format("Поле \"%s\" не может быть пустым",PEOPLE_COLUMNS_NAMES[1]));
                    checkProtocol.setSuccess( false );
                }
                persons.add(personInfo);
            }
            ++i;
        }

        if(persons.size() == 0 ) {
            checkProtocol.setSuccess( false );
            checkProtocol.getPostFormDto().setPersonsStatus("Список не может быть пустым!");
        }

        checkProtocol.getPostFormDto().setPersons(persons);
        checkProtocol.setPersonsEmptyRowsInExcel(emptyRows);
        return checkProtocol;

    }




}
