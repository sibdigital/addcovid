package ru.sibdigital.addcovid.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sibdigital.addcovid.parser.CheckProtocol;
import ru.sibdigital.addcovid.parser.ExcelParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@SpringBootTest
public class SecuredPOITest {


    //@Test
    public void readSecuredCells() throws IOException, URISyntaxException {
        ExcelParser excelParser = new ExcelParser();
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
        log.info("getOrganizationNameStatus "+ checkProtocol.getPostFormDto().getOrganizationNameStatus());


        Assertions.assertEquals(true, checkProtocol.isSuccess());
    }



}
