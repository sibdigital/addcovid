package ru.sibdigital.addcovid.parser;

import lombok.extern.slf4j.Slf4j;
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
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Component
public class ExcelParser {

    @Autowired
    ClsDepartmentRepo clsDepartmentRepo;



    final String[] SHEET_NAMES = {"ДАННЫЕ О ВАШЕЙ ОРГАНИЗАЦИИ", "АДРЕСНАЯ ИНФОРМАЦИЯ", "РАБОТНИКИ ВЫХОДЯЩИЕ НА РАБОТУ", "КУРИРУЮЩЕЕ МИНИСТЕРСТВО"};

    final String[] ADDRESS_COLUMNS_NAMES = {"Фактический адрес осуществления деятельности","Численность работников, не подлежащих переводу на дистанционный режим работы, осуществляющих деятельность  фактическому адресу"};
    final String[] DEPARTMENT_COLUMNS_NAMES = {"№","Наимнование органа власти","Выбор","Описание"};
    final String[] PEOPLE_COLUMNS_NAMES = {"Фамилия","Имя","Отчество"};

    final String[] COMMON_ORGANIZATION_INFO = {
            "*Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя: ",
            "*Краткое наименование организации",
            "*ИНН (10 или 12 цифр)",
            "*ОГРН  (13 или 15 цифр)",
            "*e-mail",
            "*Телефон (любой формат)"
    };

    final String[] ORGANIZATION_ACTIVITY_INFO = {
            "*Основной вид осуществляемой деятельности (отрасль)",
            "Дополнительные виды осуществляемой деятельности (через запятую)",
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
    final int SHEET_DEPARTMENT_INDEX = 3;

    final int ALLOWED_COUNT_OF_EMPTY_ROWS = 3;

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
        Sheet sheetDepartmnetInfo = workbook.getSheetAt(SHEET_DEPARTMENT_INDEX);

        return parse(sheetOrganizationInfo, sheetAddressesInfo, sheetPeopleInfo, sheetDepartmnetInfo);
    }

    private CheckProtocol readXLSXFile(InputStream inputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        // Get first sheet from the workbook

        Sheet sheetOrganizationInfo = workbook.getSheetAt(SHEET_ORGANIZATION_INFO_INDEX);
        Sheet sheetAddressesInfo = workbook.getSheetAt(SHEET_ADDRESSES_INDEX);
        Sheet sheetPeopleInfo = workbook.getSheetAt(SHEET_PEOPLE_INDEX);
        Sheet sheetDepartmnetInfo = workbook.getSheetAt(SHEET_DEPARTMENT_INDEX);

        return parse(sheetOrganizationInfo, sheetAddressesInfo, sheetPeopleInfo, sheetDepartmnetInfo);
    }

    private CheckProtocol parse(Sheet ...sheets) throws IOException {

        DataFormatter fmt = new DataFormatter();

        PostFormDto postFormDto = new PostFormDto();
        CheckProtocol checkProtocol = new CheckProtocol(postFormDto);

        Iterator<Row> sheetDepartmentRowsIterator = this.checkSheetWithTables(sheets, SHEET_DEPARTMENT_INDEX, DEPARTMENT_COLUMNS_NAMES,1,fmt);
        boolean organizationInfoTemplateIsCorrect = this.checkSheetWithOrganizationInfo(sheets, SHEET_ORGANIZATION_INFO_INDEX, fmt);
        Iterator<Row> sheetAddressesRowsIterator = this.checkSheetWithTables(sheets, SHEET_ADDRESSES_INDEX, ADDRESS_COLUMNS_NAMES,0,fmt);
        Iterator<Row> sheetPeopleRowsIterator = this.checkSheetWithTables(sheets, SHEET_PEOPLE_INDEX, PEOPLE_COLUMNS_NAMES,0,fmt);

        if(sheetDepartmentRowsIterator== null) {
            throw new IOException("Неизвестная ошибка при обработке страницы: " + SHEET_NAMES[SHEET_DEPARTMENT_INDEX]);
        }
        if(!organizationInfoTemplateIsCorrect) {
            throw new IOException("Неизвестная ошибка при обработке страницы: " + SHEET_NAMES[SHEET_ORGANIZATION_INFO_INDEX]);
        }
        if(sheetAddressesRowsIterator == null) {
            throw new IOException("Неизвестная ошибка при обработке страницы: " + SHEET_NAMES[SHEET_ADDRESSES_INDEX]);
        }
        if(sheetPeopleRowsIterator == null) {
            throw new IOException("Неизвестная ошибка при обработке страницы: " + SHEET_NAMES[SHEET_PEOPLE_INDEX]);
        }
        StringBuilder stringBuilder = new StringBuilder();
        checkProtocol = this.parseSheetWithDepartment(sheetDepartmentRowsIterator, checkProtocol, fmt);
        checkProtocol = this.parseSheetWithOrganizationInfo(sheets[SHEET_ORGANIZATION_INFO_INDEX], checkProtocol, fmt);
        checkProtocol = this.parseSheetWithAddresses(sheetAddressesRowsIterator, checkProtocol, fmt, stringBuilder);
        checkProtocol = this.parseSheetWithPeople(sheetPeopleRowsIterator, checkProtocol, fmt, stringBuilder);
        String globalMessage = stringBuilder.toString();


        if(globalMessage.length() != 0){
            checkProtocol.setGlobalMessage(globalMessage);
        }


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


    private Iterator<Row> checkSheetWithTables(final Sheet[] sheets, final int sheetIndex, final String[] columnsNames, final int startColumnPosition, final DataFormatter fmt) throws IOException {
        Sheet sheet = sheets[sheetIndex];
        if(!SHEET_NAMES[sheetIndex].equals(sheet.getSheetName())){
            throw new IOException(String.format("Неверное содержание файла. %d-ия страниц должна иметь имя: %s", sheetIndex, SHEET_NAMES[sheetIndex]));
        }

        Iterator<Row> rowIterator = sheet.iterator();

        for (int i = 0; i < startColumnPosition; i++) {
            if(rowIterator.hasNext()) rowIterator.next();
        }



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



        int success = 0 , error = 0;
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Cell cell = sheet.getRow(1).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя:
        String text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationName(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationNameStatus("Не может быть пустым");
            log.info("setOrganizationNameStatus setted success as false");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            success++;
        }

        cell = sheet.getRow(2).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Краткое наименование организации
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationShortName(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationShortNameStatus("Не может быть пустым");
            log.info("setOrganizationNameStatus setted success as false");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            success++;
        }

        cell = sheet.getRow(3).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);// *ИНН (10 или 12 цифр)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationInn(text);
        if(StringUtils.isBlank(text)) {
            postFormDto.setOrganizationInnStatus("Не может быть пустым");
            log.info("setOrganizationInnStatus setted success as false");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            if(text.length() != 10 && text.length() != 12 ) {
                log.info("setOrganizationInnStatus setted success as false");
                postFormDto.setOrganizationInnStatus("Должно быть длиной 10 или 12 символов");
                checkProtocol.setSuccess( false );
                error++;
            } else {
                success++;
            }
        }

        cell = sheet.getRow(4).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *ОГРН  (13 цифр)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationOgrn(text);
        if(StringUtils.isBlank(text)) {
            log.info("setOrganizationOgrn setted success as false");
            postFormDto.setOrganizationOgrnStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            if(text.length() != 13 && text.length() != 15 ) {
                log.info("setOrganizationOgrn setted success as false");
                postFormDto.setOrganizationOgrnStatus("Должно быть длиной 13 или 15 символов");
                checkProtocol.setSuccess( false );
                error++;
            } else {
                success++;
            }
        }

        cell = sheet.getRow(5).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *e-mail
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationEmail(text);
        if(StringUtils.isBlank(text)) {
            log.info("setOrganizationEmail setted success as false");
            postFormDto.setOrganizationEmailStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            success++;
        }

        cell = sheet.getRow(6).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Телефон (любой формат)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationPhone(text);
        if(StringUtils.isBlank(text)) {
            log.info("setOrganizationPhoneStatus setted success as false");
            postFormDto.setOrganizationPhoneStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            success++;
        }


        /*===============================*/


        cell = sheet.getRow(1).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Основной вид осуществляемой деятельности (отрасль)
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationOkved(text);
        if(StringUtils.isBlank(text)) {
            log.info("setOrganizationOkved setted success as false");
            postFormDto.setOrganizationOkvedStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            success++;
        }

//        cell = sheet.getRow(2).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Основной вид осуществляемой деятельности (отрасль)
//        text = fmt.formatCellValue(cell).trim();
//        postFormDto.setOrganizationOkvedAdd(text);
//
//        cell = sheet.getRow(3).getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // *Номер Министерства, курирующее вашу деятельность (берется № из листа Справочник министерств)
//
//        text = fmt.formatCellValue(cell).trim();
//        if(StringUtils.isBlank(text)){
//            log.info("setDepartmentIdStatus setted success as false");
//            postFormDto.setDepartmentIdStatus(String.format("Не может быть пустым"));
//            checkProtocol.setSuccess(false);
//        } else {
//            try{
//                postFormDto.setDepartmentId(Long.valueOf(text));
//                ClsDepartment department = clsDepartmentRepo.findById(postFormDto.getDepartmentId()).orElse(null);
//                if(department == null) {
//                    log.info("setDepartmentIdStatus setted success as false");
//                    postFormDto.setDepartmentIdStatus(String.format("Нет министерства под номером %s", text));
//                    checkProtocol.setSuccess(false);
//                }
//            } catch (NumberFormatException e) {
//                log.info("setDepartmentIdStatus setted success as false");
//                checkProtocol.setSuccess( false ); 
        //	error++;

//                postFormDto.setDepartmentIdStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
//                postFormDto.setDepartmentId(null);
//            }
//        }

        /*================================*/

        cell = sheet.getRow(10).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Юридический адрес
        text = fmt.formatCellValue(cell).trim();
        postFormDto.setOrganizationAddressJur(text);
        if(StringUtils.isBlank(text)) {
            log.info("setOrganizationAddressJurStatus setted success as false");
            postFormDto.setOrganizationAddressJurStatus("Не может быть пустым");
            checkProtocol.setSuccess( false );
            error++;
        } else {
            success++;
        }

        cell = sheet.getRow(11).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы
        text = fmt.formatCellValue(cell).trim();
        if(StringUtils.isBlank(text)){
            log.info("setPersonSlrySaveCntStatus setted success as false");
            postFormDto.setPersonSlrySaveCntStatus(String.format("Не может быть пустым"));
            checkProtocol.setSuccess(false);
            error++;
        } else {
            try{
                postFormDto.setPersonSlrySaveCnt(Long.valueOf(text));
                success++;
            } catch (NumberFormatException e) {
                log.info("setPersonSlrySaveCnt setted success as false");
                checkProtocol.setSuccess( false );
                error++;
                postFormDto.setPersonSlrySaveCntStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                postFormDto.setPersonSlrySaveCnt(null);
            }
        }

        cell = sheet.getRow(12).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Суммарная численность работников, подлежащих переводу на дистанционный режим работы
        text = fmt.formatCellValue(cell).trim();
        if(StringUtils.isBlank(text)){
            postFormDto.setPersonRemoteCntStatus(String.format("Не может быть пустым"));
            log.info("setPersonRemoteCntStatus setted success as false");
            checkProtocol.setSuccess(false);
            error++;
        } else {
            try{
                postFormDto.setPersonRemoteCnt(Long.valueOf(text));
                success++;
            } catch (NumberFormatException e) {
                log.info("setPersonRemoteCntStatus setted success as false");
                checkProtocol.setSuccess( false );
                error++;
                postFormDto.setPersonRemoteCntStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                postFormDto.setPersonRemoteCnt(null);
            }
        }

        cell = sheet.getRow(13).getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // * Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)
        text = fmt.formatCellValue(cell).trim();
        if(StringUtils.isBlank(text)){
            postFormDto.setPersonOfficeCntStatus(String.format("Не может быть пустым"));
            log.info("setPersonOfficeCntStatus setted success as false");
            checkProtocol.setSuccess(false);
            error++;
        } else {
            try{
                postFormDto.setPersonOfficeCnt(Long.valueOf(text));
                success++;
            } catch (NumberFormatException e) {
                checkProtocol.setSuccess( false );
                error++;
                log.info("setPersonOfficeCntStatus setted success as false");
                postFormDto.setPersonOfficeCntStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                postFormDto.setPersonOfficeCnt(null);
            }
        }

        Map<String, Integer> statistic = new HashMap<>(12);
        statistic.put("success", success);
        statistic.put("error",error);
        checkProtocol.getStatistic().put("info", statistic);
        return checkProtocol;

    }

    private CheckProtocol parseSheetWithAddresses(Iterator<Row> rowIterator, CheckProtocol checkProtocol, DataFormatter fmt, StringBuilder warningStringBuilder) {
        int success = 0;
        int error = 0;
        Row row;
        StringBuilder errorString;
        List<Integer> emptyRows = new ArrayList<>(750);

        List<FactAddressDto> addresses = new ArrayList<>(1500);
        int i = 0;
        int emptyRowCounter = 0;
        while (rowIterator.hasNext()){
            FactAddressDto addressInfo = new FactAddressDto();
            if(emptyRowCounter >= ALLOWED_COUNT_OF_EMPTY_ROWS) break;
            row = rowIterator.next();
            for (int j = 0; j < ADDRESS_COLUMNS_NAMES.length; j++) {
                String text = fmt.formatCellValue(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
                switch (j){
                    case 0:
                        addressInfo.setAddressFact(text);
                        break;
                    case 1:
                        if(StringUtils.isBlank(text)){
                            addressInfo.setPersonOfficeFactCnt(null);
                        } else {
                            try{
                                addressInfo.setPersonOfficeFactCnt(Long.valueOf(text));
                            } catch (NumberFormatException e) {
                                addressInfo.setPersonOfficeFactCnt(null);
                            }
                        }
                        break;
                }
            }

            if(StringUtils.isBlank(addressInfo.getAddressFact()) && addressInfo.getPersonOfficeFactCnt() == null){
                emptyRows.add(i+1);
                emptyRowCounter++;

            } else{
                errorString = new StringBuilder();
                boolean skip = false;

                if(StringUtils.isBlank(addressInfo.getAddressFact())){
                    errorString.append(String.format("%s - не может быть пустым;", ADDRESS_COLUMNS_NAMES[0]));
                    checkProtocol.setSuccess(false);
                }

                if(addressInfo.getPersonOfficeFactCnt() == null){
                    errorString.append(String.format("%s - либо пустое, либо не может быть преобразовано в число", ADDRESS_COLUMNS_NAMES[1]));
                    checkProtocol.setSuccess(false);
                    skip=true;

                }

                if(skip) {
                    checkProtocol.setSuccess( false );
                    error++;
                    addressInfo.setStatus(errorString.toString());
                    continue;
                }

                success++;
                addressInfo.setStatus("OK");
                addresses.add(addressInfo);
                emptyRowCounter = 0;
            }
            ++i;
        }
        if(addresses.size() == 0 ) {
            checkProtocol.setSuccess( false );
            error++;
            checkProtocol.getPostFormDto().setAddressFactStatus("Список не может быть пустым!");
        }

        if(!emptyRows.isEmpty()){
            warningStringBuilder.append(String.format("На листе %s имеются пустые строки на позициях: ", SHEET_NAMES[SHEET_ADDRESSES_INDEX]));
            for (Integer rowInd: emptyRows) {
                warningStringBuilder.append(rowInd);
                warningStringBuilder.append(", ");
            }
            warningStringBuilder.replace(warningStringBuilder.length()-2, warningStringBuilder.length(), "; ");

        }

        Map<String, Integer> statistic = new HashMap<>(3);
        statistic.put("success", success);
        statistic.put("error", error);
        statistic.put("empty", emptyRows.size());


        checkProtocol.getStatistic().put("address", statistic);
        checkProtocol.getPostFormDto().setAddressFact(addresses);
        checkProtocol.setAddressesEmptyRowsInExcel(emptyRows);

        return checkProtocol;

    }

    private CheckProtocol parseSheetWithPeople(Iterator<Row> rowIterator, CheckProtocol checkProtocol, DataFormatter fmt, StringBuilder warningStringBuilder) {

        int success = 0;
        int error = 0;

        Row row = null;
        StringBuilder errorString;
        List<Integer> emptyRows = new ArrayList<>(750);

        List<PersonDto> persons = new ArrayList<>(1500);
        int i = 0;
        int emptyRowCounter = 0;
        while (rowIterator.hasNext()){
            if(emptyRowCounter >= ALLOWED_COUNT_OF_EMPTY_ROWS) break;
            PersonDto personInfo = new PersonDto();
            row = rowIterator.next();
            for (int j = 0; j < PEOPLE_COLUMNS_NAMES.length; j++) {
                String text = fmt.formatCellValue(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
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

            errorString = new StringBuilder();
            boolean skip = false;

            if(StringUtils.isBlank(personInfo.getFirstname()) && StringUtils.isBlank(personInfo.getLastname())){
                emptyRows.add(i+1);
                emptyRowCounter++;
            } else{

                if(StringUtils.isBlank(personInfo.getFirstname())) {
                    errorString.append(String.format("Поле \"%s\" не может быть пустым",PEOPLE_COLUMNS_NAMES[0]));
                    checkProtocol.setSuccess(false);
                    skip=true;
                }
                if(StringUtils.isBlank(personInfo.getLastname())) {

                    errorString.append(String.format("Поле \"%s\" не может быть пустым",PEOPLE_COLUMNS_NAMES[1]));
                    checkProtocol.setSuccess(false);
                    skip=true;
                }

                if(skip) {
                    checkProtocol.setSuccess( false );
                    error++;
                    personInfo.setStatus(errorString.toString());
                    continue;
                }

                success++;
                personInfo.setStatus("OK");

                persons.add(personInfo);
                emptyRowCounter = 0;
            }
            ++i;
        }

        if(persons.size() == 0 ) {
            checkProtocol.setSuccess( false );
            error++;
            checkProtocol.getPostFormDto().setPersonsStatus("Список не может быть пустым!");
        }



        Map<String, Integer> statistic = new HashMap<>(3);
        statistic.put("success", success);
        statistic.put("error", error);
        statistic.put("empty", emptyRows.size());
        if(!emptyRows.isEmpty()){
            warningStringBuilder.append(String.format("На листе %s имеются пустые строки на позициях: ", SHEET_NAMES[SHEET_PEOPLE_INDEX]));
            for (Integer rowInd: emptyRows) {
                warningStringBuilder.append(rowInd);
                warningStringBuilder.append(", ");
            }
            warningStringBuilder.replace(warningStringBuilder.length()-2, warningStringBuilder.length(), "; ");

        }

        checkProtocol.getStatistic().put("people", statistic);
        checkProtocol.getPostFormDto().setPersons(persons);
        checkProtocol.setPersonsEmptyRowsInExcel(emptyRows);
        return checkProtocol;

    }

    private CheckProtocol parseSheetWithDepartment(Iterator<Row> rowIterator, CheckProtocol checkProtocol, DataFormatter fmt) throws IOException {


        Row row;


        List<Map<String,String>> checkedDeparts = new ArrayList<>(16);

        while (rowIterator.hasNext()) {

            row = rowIterator.next();

            String text = fmt.formatCellValue(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
            if (!StringUtils.isBlank(text)) {
                text = fmt.formatCellValue(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
                if (StringUtils.isBlank(text)) {
                    checkProtocol.setSuccess(false);
                    throw new IOException(String.format("Неверное содержание файла. Страница %s - колонка № не должна быть пустой", SHEET_NAMES[SHEET_DEPARTMENT_INDEX]));
                } else {
                    try {
                        checkProtocol.getPostFormDto().setDepartmentId(Long.valueOf(text));
                        Map<String,String> dep = new HashMap<>(2);
                        dep.put("id", String.valueOf(checkProtocol.getPostFormDto().getDepartmentId()));
                        dep.put("name", fmt.formatCellValue(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim());
                        checkedDeparts.add(dep);
                    } catch (NumberFormatException e) {
                        checkProtocol.getPostFormDto().setDepartmentId(null);
                        throw new IOException(String.format("Неверное содержание файла. Страница %s - колонка № на позиции %d Должна содержать номер!", SHEET_NAMES[SHEET_DEPARTMENT_INDEX], row.getRowNum()+1));
                        /*checkProtocol.getPostFormDto().setDepartmentIdStatus(String.format("Значение \"%s\" не может быть преобразовано в число", text));
                        checkProtocol.getPostFormDto().setDepartmentId(null);*/
                        //checkProtocol.setSuccess(false);
                    }
                }
            }
        }


        if(checkedDeparts.size() == 0) {
            checkProtocol.getPostFormDto().setDepartmentId(null);
            checkProtocol.setSuccess( false );
            checkProtocol.getPostFormDto().setDepartmentIdStatus("Не выбрано Курирующее министерство");
        }
        if(checkedDeparts.size() > 1) {
            checkProtocol.getPostFormDto().setDepartmentId(null);
            checkProtocol.setSuccess( false );
            checkProtocol.getPostFormDto().setDepartmentIdStatus("Нельзя выбирать больше одного курирующего министерства");
        }

        checkProtocol.setCheckedDeparts(checkedDeparts);
        return checkProtocol;

    }







}
