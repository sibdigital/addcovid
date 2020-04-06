package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.FactAddressDto;
import ru.sibdigital.addcovid.dto.PersonDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@SpringBootTest
@Slf4j
class RequestServiceTest {

    @Autowired
    ClsDepartmentRepo departmentRepo;

    @Autowired
    RequestService requestService;

    PostFormDto postForm;
    PersonDto personDto;
    FactAddressDto factAddressDto;


   public RequestServiceTest(){
       factAddressDto = FactAddressDto.builder()
               .addressFact("ул. Домодедова 102")
               .personOfficeFactCnt(10L)
               .build();

       this.personDto = PersonDto.builder()
               .firstname("Татьяна")
               .lastname("Михайлова")
               . patronymic("Анатольевна")
               .isAgree(true)
               .build();



       this.postForm = PostFormDto.builder()
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
   }

    @Test
    public void testAdd() {

        requestService.addNewRequest(postForm);

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







        this.postForm.setAttachment(file);


        requestService.addNewRequest(postForm);



    }

    @Test
    void getLastRequestInfoByInnAndOgrnAndOrganizationName() {
        DocRequest docRequest =
                requestService.getLastRequestInfoByInnAndOgrnAndOrganizationName(
                        postForm.getOrganizationInn(),
                        postForm.getOrganizationOgrn(),
                        postForm.getOrganizationName()
                );

        log.info(docRequest.toString());

        Assertions.assertNotNull(docRequest);


    }
}