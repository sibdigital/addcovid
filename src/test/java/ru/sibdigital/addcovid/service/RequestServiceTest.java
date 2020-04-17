package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sibdigital.addcovid.dto.FactAddressDto;
import ru.sibdigital.addcovid.dto.PersonDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.model.RequestTypes;
import ru.sibdigital.addcovid.model.ReviewStatuses;
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    @Value("${upload.path:/uploads}")
    String filePath;



    public RequestServiceTest(){
        factAddressDto = FactAddressDto.builder()
                .addressFact("ул. Домодедова 102")
                .personOfficeFactCnt(10L)
                .build();

        this.personDto = PersonDto.builder()
                .firstname("Татьяна")
                .lastname("Михайлова")
                . patronymic("Анатольевна")
                /*.isAgree(true)*/
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
                .isAgree(true)
                .isProtect(true)
                .reqBasis("Потому что")
                .build();
    }

    //@Test
    public void testAdd() {

        DocRequest docRequest = requestService.addNewRequest(postForm, RequestTypes.ORGANIZATION);
        Assertions.assertEquals(docRequest.getAttachmentPath(), "error while upload");

    }


    //@Test
    public void testWithFile() throws IOException {


        File file = new File("db_addcovid.sql");





        Base64.Encoder enc = Base64.getEncoder();
        StringBuilder stringBuilder = new StringBuilder();
        byte[] encbytes = enc.encode(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        for (int i = 0; i < encbytes.length; i++)
        {
            stringBuilder.append((char)encbytes[i]);
        }
        this.postForm.setAttachmentFilename("db_addcovid.sql");
        this.postForm.setAttachment(stringBuilder.toString());
        DocRequest docRequest = requestService.addNewRequest(postForm, RequestTypes.ORGANIZATION);
        Assertions.assertNotEquals(docRequest.getAttachmentPath(), "error while upload");


    }

    //@Test
    void getLastRequestInfoByInnAndOgrnAndOrganizationName() {
        DocRequest lastRequest =
                requestService.getLastRequestInfoByInnAndOgrnAndOrganizationName(
                        postForm.getOrganizationInn(),
                        postForm.getOrganizationOgrn(),
                        postForm.getOrganizationName()
                );

        log.info(lastRequest.toString());

        Assertions.assertNotNull(lastRequest);


    }

    //@Test
    void getRequestToBeWatchedByDepartment() {
        List<DocRequest> docRequests = requestService.getRequestToBeWatchedByDepartment(1L);
        Assertions.assertNotNull(docRequests);
    }


    //@Test
    void setReviewStatus() {
        DocRequest lastRequest = requestService.getLastRequestInfoByInnAndOgrnAndOrganizationName(postForm.getOrganizationInn(), postForm.getOrganizationOgrn(), postForm.getOrganizationName());
        DocRequest docRequest = requestService.setReviewStatus(lastRequest, ReviewStatuses.CONFIRMED);
        Assertions.assertNotEquals(docRequest.getStatusReview(), ReviewStatuses.OPENED.getValue());


    }

    //@Test
    void getLastOpenedRequestInfoByInn() {
        DocRequest reqInfo = requestService.getLastOpenedRequestInfoByInn("1234567890");

        Assertions.assertNotNull(reqInfo);
    }

    //@Test
    void getLastOpenedRequestInfoByOgrn() {
        DocRequest reqInfo = requestService.getLastOpenedRequestInfoByOgrn("1234567890123");

        Assertions.assertNotNull(reqInfo);
    }

    //@Test
    void getLasRequestInfoByInn() {
        DocRequest reqInfo = requestService.getLasRequestInfoByInn("1234567890");

        Assertions.assertNotNull(reqInfo);
    }

    //@Test
    void getLastRequestInfoByOgrn() {
        DocRequest reqInfo = requestService.getLastRequestInfoByOgrn("1234567890123");

        Assertions.assertNotNull(reqInfo);
    }
}