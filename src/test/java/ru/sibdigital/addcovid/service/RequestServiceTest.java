package ru.sibdigital.addcovid.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.FactAddressDto;
import ru.sibdigital.addcovid.dto.PersonDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@SpringBootTest
class RequestServiceTest {

    @Autowired
    ClsDepartmentRepo departmentRepo;

    @Autowired
    RequestService requestService;


    @Test
    public void testAdd() {




        FactAddressDto factAddressDto = FactAddressDto.builder()
                .addressFact("ул. Домодедова 102")
                .personOfficeFactCnt(10L)
                .build();

        PersonDto personDto = PersonDto.builder()
                .firstname("Татьяна")
                .lastname("Михайлова")
                . patronymic("Анатольевна")
                .isAgree(true)
                .build();



        PostFormDto postForm = PostFormDto.builder()
                .departmentId(1L)
                .organizationName("МАОУ СОШ Школа №1")
                .organizationShortName("Школа №1")
                .organizationInn("1234567890")
                .organizationOgrn("1234567890123")
                .organizationAddressJur("ул. Домодедова 102")
                .organizationOkved("Подготовка школьников")
                .organizationOkvedAdd("Подготовка к ЕГЭ")
                .organizationEmail("shkola1@edu.ru")
                .organizationPhone("445566")
                .addressFact(new ArrayList<>(){{add(factAddressDto);}})
                .persons(new ArrayList<>(){{add(personDto);}})
                .personOfficeCnt(10L)
                .personRemoteCnt(3L)
                .personSlrySaveCnt(6L)
                .personOfficeFactCnt(1L)
                .attachment(null)
                .build();


        requestService.addNewRequst(postForm);



    }


    @Test
    public void testWithFile() throws IOException {


        Path path = Paths.get("F:/JavaProjects/addcovid/db_addcovid.sql");
        String name = "db_addcovid.sql";
        String originalFileName = "db_addcovid.sql";
        String contentType = "text/pdf";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile file = new MockMultipartFile(name,
                originalFileName, contentType, content);



        FactAddressDto factAddressDto = FactAddressDto.builder()
                .addressFact("ул. Домодедова 102")
                .personOfficeFactCnt(10L)
                .build();

        PersonDto personDto = PersonDto.builder()
                .firstname("Татьяна")
                .lastname("Михайлова")
                . patronymic("Анатольевна")
                .isAgree(true)
                .build();



        PostFormDto postForm = PostFormDto.builder()
                .departmentId(1L)
                .organizationName("МАОУ СОШ Школа №1")
                .organizationShortName("Школа №1")
                .organizationInn("1234567890")
                .organizationOgrn("1234567890123")
                .organizationAddressJur("ул. Домодедова 102")
                .organizationOkved("Подготовка школьников")
                .organizationOkvedAdd("Подготовка к ЕГЭ")
                .organizationEmail("shkola1@edu.ru")
                .organizationPhone("445566")
                .addressFact(new ArrayList<>(){{add(factAddressDto);}})
                .persons(new ArrayList<>(){{add(personDto);}})
                .personOfficeCnt(10L)
                .personRemoteCnt(3L)
                .personSlrySaveCnt(6L)
                .personOfficeFactCnt(1L)
                .attachment(file)
                .build();


        requestService.addNewRequst(postForm);



    }
}