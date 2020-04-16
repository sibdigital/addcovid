package ru.sibdigital.addcovid.service;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.parser.CheckProtocol;
import ru.sibdigital.addcovid.parser.ExcelParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Log4j2
@SpringBootTest
public class SecuredPOITest {
    @Autowired
    ExcelParser excelParser;
    File allOkFile;

    public SecuredPOITest() throws URISyntaxException {
        allOkFile = new File(this.getClass().getClassLoader().getResource("all_ok.xlsx").toURI());
    }

    @Test
    public void testAllOkFields() throws IOException, URISyntaxException {

        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        Assertions.assertTrue(checkProtocol.isSuccess());
    }

    @Test
    public void testDepartmentIdNotNull() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertNotNull(postFormDto.getDepartmentId());
    }

    @Test
    public void testPersonRemoteCntNotNull() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertNotNull(postFormDto.getPersonRemoteCnt());
    }

    @Test
    public void testPersonOfficeCntNotNull() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertNotNull(postFormDto.getPersonOfficeCnt());
    }

    @Test
    public void testPersonSlrySaveCntNotNull() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertNotNull(postFormDto.getPersonSlrySaveCnt());
    }

    @Test
    public void testOrganizationNameNotEmpty() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertFalse(StringUtils.isBlank(postFormDto.getOrganizationName()));
    }

    @Test
    public void testInnNotEmpty() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertFalse(StringUtils.isBlank(postFormDto.getOrganizationInn()));
    }

    @Test
    public void testOgrnNotEmpty() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertFalse(StringUtils.isBlank(postFormDto.getOrganizationOgrn()));
    }

    @Test
    public void testPhoneNotEmpty() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertFalse(StringUtils.isBlank(postFormDto.getOrganizationPhone()));
    }

    @Test
    public void testOkvedAddNotEmpty() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertFalse(StringUtils.isBlank(postFormDto.getOrganizationOkvedAdd()));
    }

    @Test
    public void testAddressJurNotEmpty() throws IOException {
        CheckProtocol checkProtocol = excelParser.parseFile(allOkFile);
        PostFormDto postFormDto = checkProtocol.getPostFormDto();
        Assertions.assertFalse(StringUtils.isBlank(postFormDto.getOrganizationAddressJur()));
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
    public void testCntOfficeBlankIsNullable() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_office_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertNull(checkProtocol.getPostFormDto().getPersonOfficeCnt());
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
    public void testCntOfficeTextIsNullable() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_office_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertNull(checkProtocol.getPostFormDto().getPersonOfficeCnt());
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
    public void testCntRmtBlankIsNullable() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_rmt_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertNull(checkProtocol.getPostFormDto().getPersonRemoteCnt());
    }

    @Test
    public void testCntRmtText() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_rmt_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonRemoteCntStatus().equals("OK"));
    }

    @Test
    public void testCntRmtTextIsNullable() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_rmt_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertNull(checkProtocol.getPostFormDto().getPersonRemoteCnt());
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
    public void testCntSaveBlankIsNullable() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_save_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertNull(checkProtocol.getPostFormDto().getPersonSlrySaveCnt());
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
    public void testCntSaveTextIsNullable() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("cnt_save_text_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertNull(checkProtocol.getPostFormDto().getPersonSlrySaveCnt());
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

    @Test
    public void testOkvedBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("okved_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationOkvedStatus().equals("OK"));
    }

    @Test
    public void testOkvedBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("okved_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testPhoneBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("phone_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationPhoneStatus().equals("OK"));
    }

    @Test
    public void testPhoneBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("phone_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testShortNameBlank() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("short_name_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getOrganizationShortNameStatus().equals("OK"));
    }

    @Test
    public void testShortNameSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("short_name_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }


    @Test
    public void testAddressFact1ColumnBlankError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_1_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getAddressFact().get(1).getStatus().equals("OK"));
    }
    @Test
    public void testAddressFact1ColumnBlankListSize3() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_1_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(3, checkProtocol.getPostFormDto().getAddressFact().size());
    }

    @Test
    public void testAddressFact1ColumnBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_1_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }


    @Test
    public void testAddressFact2ColumnBlankError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_2_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getAddressFact().get(2).getStatus().equals("OK"));
    }
    @Test
    public void testAddressFact2ColumnBlankListSize3() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_2_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(3, checkProtocol.getPostFormDto().getAddressFact().size());
    }

    @Test
    public void testAddressFact2ColumnBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_2_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testAddressFactAll1ColumnBlankError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_all_1_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);

        boolean firstRow = checkProtocol.getPostFormDto().getAddressFact().get(0).getStatus().equals("OK");
        boolean secondRow = checkProtocol.getPostFormDto().getAddressFact().get(1).getStatus().equals("OK");
        boolean thirdRow = checkProtocol.getPostFormDto().getAddressFact().get(2).getStatus().equals("OK");

        Assertions.assertEquals(false, firstRow || secondRow || thirdRow);
    }

    @Test
    public void testAddressFactAll1ColumnBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_all_1_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }


    @Test
    public void testAddressFactAll2ColumnBlankError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_all_2_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);

        boolean firstRow = checkProtocol.getPostFormDto().getAddressFact().get(0).getStatus().equals("OK");
        boolean secondRow = checkProtocol.getPostFormDto().getAddressFact().get(1).getStatus().equals("OK");
        boolean thirdRow = checkProtocol.getPostFormDto().getAddressFact().get(2).getStatus().equals("OK");

        Assertions.assertEquals(false, firstRow || secondRow || thirdRow);
    }

    @Test
    public void testAddressFactAll2ColumnBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_all_2_column_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testAddressFactEmptyError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_size_0_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getAddressFactStatus().equals("OK"));
    }

    @Test
    public void testAddressFactEmptySetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_size_0_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testAddressFactSize3Ok() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_size_3_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.getPostFormDto().getAddressFactStatus().equals("OK"));
    }

    @Test
    public void testAddressFactSize3SetSuccessTrue() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("fact_address_size_3_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.isSuccess());
    }




    @Test
    public void testPeople1ColumnBlankError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_firstname_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersons().get(4).getStatus().equals("OK"));
    }
    @Test
    public void testPeople1ColumnBlankListSize3() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_firstname_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(6, checkProtocol.getPostFormDto().getPersons().size());
    }

    @Test
    public void testPeople1ColumnBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_firstname_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }


    @Test
    public void testPeople2ColumnBlankError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_lastname_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersons().get(4).getStatus().equals("OK"));
    }

    @Test
    public void testPeople2ColumnBlankSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_lastname_blank_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }


    @Test
    public void testPeopleEmptyError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_empty_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersonsStatus().equals("OK"));
    }

    @Test
    public void testPeopleEmptySetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_empty_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testPeopleSize6Ok() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_all_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.getPostFormDto().getPersonsStatus().equals("OK"));
    }

    @Test
    public void testPeopleSize6SetSuccessTrue() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_all_ok.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(true, checkProtocol.isSuccess());
    }

    @Test
    public void testPeopleFullnameInLastnameError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_fio_lastname_error.xlsx").toURI());

        CheckProtocol checkProtocol = excelParser.parseFile(file);
        log.info("testPeopleFullnameInLastnameError: " + checkProtocol.getPostFormDto().getPersons().get(0).getStatus());
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersons().get(0).getStatus().equals("OK"));
    }
    @Test
    public void testPeopleFullnameInLastnameSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_fio_lastname_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        log.info("testPeopleFullnameInLastnameSetSuccessFalse: " + checkProtocol.getPostFormDto().getPersons().get(0).getStatus());
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testPeopleFullnameInFirstnameError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_fio_firstname_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersons().get(0).getStatus().equals("OK"));
    }
    @Test
    public void testPeopleFullnameInFirstnameSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_fio_firstname_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

    @Test
    public void testPeopleFullnameInPatronymicError() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_fio_patronymic_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.getPostFormDto().getPersons().get(0).getStatus().equals("OK"));
    }
    @Test
    public void testPeopleFullnameInPatronymicSetSuccessFalse() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getClassLoader().getResource("people_fio_patronymic_error.xlsx").toURI());
        CheckProtocol checkProtocol = excelParser.parseFile(file);
        Assertions.assertEquals(false, checkProtocol.isSuccess());
    }

}

