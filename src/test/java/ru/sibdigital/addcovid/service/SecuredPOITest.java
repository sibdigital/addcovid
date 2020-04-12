package ru.sibdigital.addcovid.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sibdigital.addcovid.parser.CheckProtocol;
import ru.sibdigital.addcovid.parser.ExcelParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@SpringBootTest
public class SecuredPOITest {
@Autowired
    ExcelParser excelParser;

    @Test
    public void testAllOkFields() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("all_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);

        log.info(checkProtocol.getPostFormDto().getPersonsStatus());
        log.info(checkProtocol.getPostFormDto().getAddressFactStatus());
        log.info("getOrganizationNameStatus "+ checkProtocol.getPostFormDto().getOrganizationNameStatus());
        log.info("getOrganizationShortNameStatus "+ checkProtocol.getPostFormDto().getOrganizationShortNameStatus());
        log.info("getOrganizationInnStatus "+ checkProtocol.getPostFormDto().getOrganizationInnStatus());
        log.info("getOrganizationOgrnStatus "+ checkProtocol.getPostFormDto().getOrganizationOgrnStatus());
        log.info("getOrganizationPhoneStatus "+ checkProtocol.getPostFormDto().getOrganizationPhoneStatus());
        log.info("getOrganizationEmailStatus "+ checkProtocol.getPostFormDto().getOrganizationEmailStatus());
        log.info("getOrganizationOkvedStatus "+ checkProtocol.getPostFormDto().getOrganizationOkvedStatus());
        log.info("getDepartmentIdStatus "+ checkProtocol.getPostFormDto().getDepartmentIdStatus());


        Assertions.assertEquals(true, checkProtocol.isSuccess());
    }

    @Test
    public void testCntOfficeBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_office_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonOfficeCntStatus().equals("OK"));
    }

    @Test
    public void testCntOfficeBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_office_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testCntOfficeText() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_office_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonOfficeCntStatus().equals("OK"));
    }

    @Test
    public void testCntOfficeTextSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_office_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }




    @Test
    public void testCntRmtBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_rmt_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonRemoteCntStatus().equals("OK"));
    }

    @Test
    public void testCntRmtBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_rmt_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testCntRmtText() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_rmt_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonRemoteCntStatus().equals("OK"));
    }

    @Test
    public void testCntRmtTextSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_rmt_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }



    @Test
    public void testCntSaveBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_save_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonSlrySaveCntStatus().equals("OK"));
    }

    @Test
    public void testCntSaveBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_save_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testCntSaveText() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_save_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonSlrySaveCntStatus().equals("OK"));
    }

    @Test
    public void testCntSaveTextSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_save_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testIdDepartmentBlankError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getDepartmentIdStatus().equals("OK"));
    }

    @Test
    public void testIdDepartmentBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testIdDepartmentBlankCheckedListSize0() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(0, checkProtocol.getCheckedDeparts().size());
    }





    @Test
    public void testIdDepartment2CheckedError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_2_checked.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getDepartmentIdStatus().equals("OK"));
    }
    @Test
    public void testIdDepartment2CheckedListSize2() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_2_checked.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(2, checkProtocol.getCheckedDeparts().size());
    }


    @Test
    public void testIdDepartment2CheckedSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_2_checked.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testIdDepartmentAnySymbol() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_any_symbol_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.getPostFormDto().getDepartmentIdStatus().equals("OK"));
    }

    @Test
    public void testIdDepartmentAnySymbolSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_any_symbol_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.isSuccess());
    }

    @Test
    public void testIdDepartmentAnySymbolCheckedListSize1() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("department_any_symbol_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(1, checkProtocol.getCheckedDeparts().size());
    }

    @Test
    public void testEmailBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("email_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationEmailStatus().equals("OK"));
    }

    @Test
    public void testEmailBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("email_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testInnBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationInnStatus().equals("OK"));
    }

    @Test
    public void testInnBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testInn3() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_3_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationInnStatus().equals("OK"));
    }

    @Test
    public void testInn3SetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_3_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testInn11() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_11_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationInnStatus().equals("OK"));
    }

    @Test
    public void testInn11SetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_11_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testInn14() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_14_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationInnStatus().equals("OK"));
    }

    @Test
    public void testInn14SetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_14_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testInn10() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_10_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.getPostFormDto().getOrganizationInnStatus().equals("OK"));
    }

    @Test
    public void testInn110SetSuccessTrue() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_10_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.isSuccess());
    }

    @Test
    public void testInn12() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_12_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.getPostFormDto().getOrganizationInnStatus().equals("OK"));
    }

    @Test
    public void testInn112SetSuccessTrue() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("inn_12_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.isSuccess());
    }

    @Test
    public void testJurAddrBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("jur_addr_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationAddressJurStatus().equals("OK"));
    }

    @Test
    public void testJurAddrSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("jur_addr_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testOrgNameBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("name_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationName().equals("OK"));
    }

    @Test
    public void testOrgNameSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("name_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testOGRNBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("ogrn_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationOgrnStatus().equals("OK"));
    }

    @Test
    public void testOGRNBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("ogrn_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testOGRN10Error() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("ogrn_10_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationOgrnStatus().equals("OK"));
    }

    @Test
    public void testOGRN10ErrorSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("ogrn_10_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }




}
