package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.utils.SHA256Generator;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestService {

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    DocAddressFactRepo docAddressFactRepo;

    @Autowired
    DocPersonRepo docPersonRepo;

    @Autowired
    DocRequestRepo docRequestRepo;

    @Autowired
    ClsDepartmentRepo departmentRepo;

    @Autowired
    private DepUserRepo depUserRepo;

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    private static final int BUFFER_SIZE = 4096;

    public String addNewRequest(PostFormDto postForm) {



        String sha256 = SHA256Generator.generate(postForm.getOrganizationInn(), postForm.getOrganizationOgrn(), postForm.getOrganizationName());

        DocRequest docRequest = null;
        try {
            docRequest = docRequestRepo.getTopByOrgHashCode(sha256).orElseGet(() -> null);

        } catch (Exception e) {
            docRequest = null;
        }
        ClsOrganization organization;


        if (docRequest != null) {
            organization = docRequest.getOrganization();
        } else {
            organization = ClsOrganization.builder()
                    .name(postForm.getOrganizationName())
                    .shortName(postForm.getOrganizationShortName())
                    .inn(postForm.getOrganizationInn())
                    .ogrn(postForm.getOrganizationOgrn())
                    .addressJur(postForm.getOrganizationAddressJur())
                    .okvedAdd(postForm.getOrganizationOkvedAdd())
                    .okved(postForm.getOrganizationOkved())
                    .email(postForm.getOrganizationEmail())
                    .phone(postForm.getOrganizationPhone())
                    .statusImport(0)
                    .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            organization = clsOrganizationRepo.save(organization);
        }

            Set<DocPerson> personSet = postForm.getPersons()
                    .stream()
                    .map(personDto -> personDto.convertToPersonEntity())
                    .collect(Collectors.toSet());

            Set<DocAddressFact> docAddressFactSet = postForm.getAddressFact()
                    .stream()
                    .map(personDto -> personDto.convertToDocAddressFact())
                    .collect(Collectors.toSet());


        //int importStatus = ImportStatuses.SUCCESS.getValue();
        String filename = "error while upload";
        try {
            File uploadFolder = new File(uploadingDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }
/*
            File file = new File(String.format("%s\\%s_%s",uploadingDir, UUID.randomUUID(),postForm.getAttachment().getOriginalFilename()));
            postForm.getAttachment().transferTo(file);
            filename = file.getName();
*/

            byte[] valueDecoded = Base64.getDecoder().decode(postForm.getAttachment());

            String inputFilename = String.format("%s/%s_%s", uploadFolder.getAbsolutePath(), UUID.randomUUID(), postForm.getAttachmentFilename());
            FileOutputStream fos;

            fos = new FileOutputStream(inputFilename);
            fos.write(valueDecoded);
            fos.close();
            filename = inputFilename;

        } catch (IOException ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }

            docRequest = DocRequest.builder()
                    .organization(organization)
                    .department(departmentRepo.getOne(postForm.getDepartmentId()))
                    .personOfficeCnt(postForm.getPersonOfficeCnt())
                    //.personOfficeFactCnt(postForm.getPersonOfficeFactCnt())
                    .personRemoteCnt(postForm.getPersonRemoteCnt())
                    .personSlrySaveCnt(postForm.getPersonSlrySaveCnt())
                    .attachmentPath(filename)
                    .docPersonSet(personSet)
                    .docAddressFact(docAddressFactSet)
                    .statusReview(0)
                    .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                    .statusImport(0)
                    .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                    .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                    .isAgree(postForm.getIsAgree())
                    .isProtect(postForm.getIsProtect())
                    .reqBasis(postForm.getReqBasis())
                    .orgHashCode(sha256)
                    .build();

            docRequest = docRequestRepo.save(docRequest);

            DocRequest finalDocRequest = docRequest;
            docRequest.getDocAddressFact().forEach(docAddressFact -> {
                docAddressFact.setDocRequest(finalDocRequest);
            });

            docRequest.getDocPersonSet().forEach(docPerson -> {
                docPerson.setDocRequest(finalDocRequest);
            });

            docAddressFactRepo.saveAll(docRequest.getDocAddressFact());
            docPersonRepo.saveAll(docRequest.getDocPersonSet());

            return docRequest.getOrgHashCode();
        }


    public DocRequest getLastRequestInfoByInnAndOgrnAndOrganizationName(String inn, String ogrn, String organizationName){

        String sha256 = SHA256Generator.generate(inn, ogrn, organizationName);
        /*ClsOrganization organization = clsOrganizationRepo.getFirstByHashCode(sha256).orElseGet(() -> null);
        if(organization == null) {
            return null;
        }*/

        DocRequest docRequest = docRequestRepo.getTopByOrgHashCode(sha256).orElseGet(() -> null);


        return docRequest;
    }


    public List<DocRequest> getRequestToBeWatchedByDepartment(Long id){
        return this.docRequestRepo.getAllByDepartmentId(id, ReviewStatuses.OPENED.getValue()).orElseGet(()->null);
    }


    @AfterReturning
    public DocRequest setReviewStatus(DocRequest docRequest, ReviewStatuses status){
        docRequest.setTimeReview(Timestamp.valueOf(LocalDateTime.now()));
        docRequest.setStatusReview(status.getValue());
        return docRequestRepo.save(docRequest);
    }




    public DocRequest getLastOpenedRequestInfoByInn(String inn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByInnAndStatus(inn, ReviewStatuses.OPENED.getValue()).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };


    public DocRequest getLastOpenedRequestInfoByOgrn(String ogrn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByOgrnAndStatus(ogrn, ReviewStatuses.OPENED.getValue()).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };

    public DocRequest getLasRequestInfoByInn(String inn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByInn(inn).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };

    public Optional<List<DocRequest>> getFirst100RequestInfoByDepartmentIdAndStatusAndInnOrName(Long departmentId, Integer status, String innOrName){
        //return docRequestRepo.getFirst100RequestByDepartmentIdAndStatusAndInnOrName(departmentId, status, innOrName).orElseGet(() -> null);
        return docRequestRepo.getFirst100RequestByDepartmentIdAndStatusAndInnOrName(departmentId, status, innOrName);
    };


    public DocRequest getLastRequestInfoByOgrn(String ogrn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByOgrn(ogrn).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };

    public void downloadFile(HttpServletResponse response, DocRequest DocRequest) throws Exception {
        File downloadFile = new File(DocRequest.getAttachmentPath());

        FileInputStream inputStream = new FileInputStream(downloadFile);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        response.setContentType("application/pdf");

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
    }

    public boolean isTokenValid(Integer hash_code){
        Iterator<DepUser> iter = depUserRepo.findAll().iterator();
        while(iter.hasNext()) {
            if(hash_code == iter.next().hashCode()) return true;
        }
        return false;
    }
}
