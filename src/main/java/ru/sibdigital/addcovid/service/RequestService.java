package ru.sibdigital.addcovid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.*;
import ru.sibdigital.addcovid.dto.egrip.EGRIP;
import ru.sibdigital.addcovid.dto.egrul.EGRUL;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.classifier.gov.*;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.repository.classifier.gov.OkvedRepo;
import ru.sibdigital.addcovid.repository.classifier.gov.RegEgripRepo;
import ru.sibdigital.addcovid.repository.classifier.gov.RegEgrulRepo;
import ru.sibdigital.addcovid.utils.JuridicalUtils;
import ru.sibdigital.addcovid.utils.PasswordGenerator;
import ru.sibdigital.addcovid.utils.SHA256Generator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    private ClsUserRepo clsUserRepo;

    @Autowired
    private ClsTypeRequestRepo clsTypeRequestRepo;

    @Autowired
    private ClsDistrictRepo districtRepo;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private ClsOrganizationContactRepo clsOrganizationContactRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OkvedRepo okvedRepo;

    @Autowired
    private RegOrganizationOkvedRepo regOrganizationOkvedRepo;

    @Autowired
    private RegActualizationHistoryRepo regActualizationHistoryRepo;

    @Autowired
    private DocEmployeeRepo docEmployeeRepo;

    @Autowired
    private RegMailingListFollowerRepo regMailingListFollowerRepo;

    @Autowired
    private ClsMailingListRepo clsMailingListRepo;

    @Autowired
    private RegOrganizationAddressFactRepo regOrganizationAddressFactRepo;

    @Autowired
    private ClsNewsRepo clsNewsRepo;

    @Autowired
    private RegNewsLinkClicksRepo regNewsLinkClicksRepo;

    @Autowired
    private RegEgripRepo regEgripRepo;

    @Autowired
    private RegEgrulRepo regEgrulRepo;

    @Autowired
    private RegPersonCountRepo regPersonCountRepo;

    @Autowired
    private DocRequestPrsRepo docRequestPrsRepo;

    @Autowired
    private ClsPrescriptionRepo clsPrescriptionRepo;

    @Autowired
    private RegOrganizationPrescriptionRepo regOrganizationPrescriptionRepo;

    @Autowired
    private RegDocRequestFileRepo regDocRequestFileRepo;

    @Autowired
    private RegDocRequestPrescriptionRepo regDocRequestPrescriptionRepo;

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    private static final int BUFFER_SIZE = 4096;

    private static ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public DocRequest addNewRequest(PostFormDto postForm, int requestType) {

        String sha256 = null;
        DocRequest docRequest;
        ClsOrganization organization;

        if (postForm.getOrganizationId() == null) {

            sha256 = SHA256Generator.generate(postForm.getOrganizationInn(), postForm.getOrganizationOgrn(), postForm.getOrganizationName());

            try {
                docRequest = docRequestRepo.getTopByOrgHashCode(sha256).orElseGet(() -> null);
            } catch (Exception e) {
                docRequest = null;
            }

            if (docRequest != null) {
                organization = docRequest.getOrganization();
            } else {
                int typeOrganization = OrganizationTypes.JURIDICAL.getValue();
                if (postForm.getIsSelfEmployed() != null && postForm.getIsSelfEmployed()) {
                    typeOrganization = OrganizationTypes.SELF_EMPLOYED.getValue();
                }

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
                        .idTypeRequest(requestType)
                        .idTypeOrganization(typeOrganization)
                        .build();
                organization = clsOrganizationRepo.save(organization);
            }

        } else {
            organization = clsOrganizationRepo.findById(postForm.getOrganizationId()).orElse(null);
        }

            List<DocPerson> personList = postForm.getPersons()
                    .stream()
                    .map(personDto -> personDto.convertToPersonEntity())
                    .collect(Collectors.toList());

            List<DocAddressFact> docAddressFactList = postForm.getAddressFact()
                    .stream()
                    .map(personDto -> personDto.convertToDocAddressFact())
                    .collect(Collectors.toList());

            String files = postForm.getAttachment();

        DocRequest actualizedRequest = null;
        if (postForm.getActualizedRequestId() != null) {
            actualizedRequest = docRequestRepo.getOne(postForm.getActualizedRequestId());
        } else {
            List<DocRequest> requests = docRequestRepo.getRequestsByInnAndStatusReviewOrderByTimeReviewDesc(
                    postForm.getOrganizationInn(), ReviewStatuses.CONFIRMED.getValue()).orElse(null);
            if (requests != null && requests.size() > 0) {
                actualizedRequest = requests.get(0);
            }
        }

        int statusReview = actualizedRequest != null ? ReviewStatuses.ACTUALIZED.getValue() : ReviewStatuses.OPENED.getValue();

        docRequest = DocRequest.builder()
                    .organization(organization)
                    .department(departmentRepo.getOne(postForm.getDepartmentId()))
                    .personOfficeCnt(postForm.getPersonOfficeCnt())
                    //.personOfficeFactCnt(postForm.getPersonOfficeFactCnt())
                    .personRemoteCnt(postForm.getPersonRemoteCnt())
                    .personSlrySaveCnt(postForm.getPersonSlrySaveCnt())
                    .attachmentPath(files)
                    .docPersonList(personList)
                    .docAddressFact(docAddressFactList)
                    .statusReview(statusReview)
                    .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                    .statusImport(0)
                    .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                    .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                    .isAgree(postForm.getIsAgree())
                    .isProtect(postForm.getIsProtect())
                    .reqBasis(postForm.getReqBasis())
                    .orgHashCode(sha256)
                    .typeRequest(clsTypeRequestRepo.findById((long) requestType).orElse(null))
                    .additionalAttributes(postForm.getAdditionalAttributes())
                    .isActualization(postForm.getIsActualization())
                    .actualizedRequest(actualizedRequest)
                    .build();

            docRequest = docRequestRepo.save(docRequest);

            DocRequest finalDocRequest = docRequest;
            docRequest.getDocAddressFact().forEach(docAddressFact -> {
                docAddressFact.setDocRequest(finalDocRequest);
            });

            docRequest.getDocPersonList().forEach(docPerson -> {
                docPerson.setDocRequest(finalDocRequest);
            });

            docAddressFactRepo.saveAll(docRequest.getDocAddressFact());
            docPersonRepo.saveAll(docRequest.getDocPersonList());

            if (actualizedRequest != null) {
                RegActualizationHistory history = new RegActualizationHistory();
                history.setDocRequest(docRequest);
                history.setActualizedDocRequest(actualizedRequest);
                history.setInn(organization.getInn());
                history.setTimeActualization(Timestamp.valueOf(LocalDateTime.now()));
                regActualizationHistoryRepo.save(history);
            }

            return docRequest;
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

        //TODO сделать проверку на наличие нескольких вложений
        // и собрать в архив

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
        Iterator<ClsUser> iter = clsUserRepo.findAll().iterator();
        while(iter.hasNext()) {
            if(hash_code == iter.next().hashCode()) return true;
        }
        return false;
    }

    public String uploadFile(MultipartFile part){
        try {

            File uploadFolder = new File(uploadingDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            File file = new File(String.format("%s/%s_%s", uploadFolder.getAbsolutePath(),
                    UUID.randomUUID(), part.getOriginalFilename().replaceAll("[!@#$&~%*()\\^\\[\\]{}'\"\\:>< ,;/?|`]", "_")));
            part.transferTo(file);

            return "{ \"status\": \"server\", \"sname\": \"" + String.format("/%s/%s", uploadingDir, file.getName()) + "\" }";

        } catch (IOException ex){
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return "{ \"status\": \"error\" }";
    }

    public ClsTypeRequest getClsTypeRequestById(Integer id) {
        return clsTypeRequestRepo.findById(Long.valueOf(id)).orElseGet(() -> null);
    }

    public List<ClsTypeRequest> getClsTypeRequests() {
        return StreamSupport.stream(clsTypeRequestRepo.findAllByOrderBySortWeightDesc().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocRequest addPersonalRequest(PostFormDto postForm, int requestType) {

        String sha256 = SHA256Generator.generate(postForm.getOrganizationInn()); // TODO

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
                    .name(postForm.getPerson().getFIO())
                    .shortName(postForm.getPerson().getFIO())
                    .inn(postForm.getOrganizationInn())
                    .email(postForm.getOrganizationEmail())
                    .phone(postForm.getOrganizationPhone())
                    .typeTaxReporting(Integer.valueOf(postForm.getTypeTaxReporting()))
                    .statusImport(0)
                    .idTypeRequest(requestType)
                    .idTypeOrganization(OrganizationTypes.PHYSICAL.getValue())
                    .build();
            organization = clsOrganizationRepo.save(organization);
        }

        DocPerson docPerson = postForm.getPerson().convertToPersonEntity();
        docPerson.setOrganization(organization);

        List<DocAddressFact> docAddressFactList = postForm.getAddressFact()
                .stream()
                .map(personDto -> personDto.convertToDocAddressFact())
                .collect(Collectors.toList());

        String files = postForm.getAttachment();

        docRequest = DocRequest.builder()
                .organization(organization)
                .department(departmentRepo.getOne(postForm.getDepartmentId()))
                .district(districtRepo.getOne(postForm.getDistrictId()))
                .attachmentPath(files)
                .docAddressFact(docAddressFactList)
                .statusReview(ReviewStatuses.ACCEPTED.getValue())
                .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                .isAgree(postForm.getIsAgree())
                .isProtect(postForm.getIsProtect())
                .reqBasis(postForm.getReqBasis())
                .orgHashCode(sha256)
                .typeRequest(clsTypeRequestRepo.findById((long) requestType).orElse(null))
                .additionalAttributes(postForm.getAdditionalAttributes())
                .build();

        docRequest = docRequestRepo.save(docRequest);

        DocRequest finalDocRequest = docRequest;
        docRequest.getDocAddressFact().forEach(docAddressFact -> {
            docAddressFact.setDocRequest(finalDocRequest);
        });

        docPerson.setDocRequest(finalDocRequest);

        docAddressFactRepo.saveAll(docRequest.getDocAddressFact());
        docPersonRepo.save(docPerson);

        return docRequest;
    }

    public ClsOrganization findOrganizationByInn(String inn) {
        return clsOrganizationRepo.findByInnAndPrincipalIsNotNull(inn);
    }

    @Transactional
    public ClsOrganization saveClsOrganization(OrganizationDto organizationDto) {
        RegEgrul regEgrul = null;
        RegEgrip regEgrip = null;
        ClsPrincipal clsPrincipal = ClsPrincipal.builder()
                .password(passwordEncoder.encode(organizationDto.getPassword()))
                .build();

        clsPrincipalRepo.save(clsPrincipal);

        int typeOrganization = organizationDto.getOrganizationType();

        String code = SHA256Generator.generate(organizationDto.getOrganizationInn(), organizationDto.getOrganizationName(), String.valueOf(System.currentTimeMillis()));

        String juradress = "";
        try {
            if (typeOrganization == OrganizationTypes.JURIDICAL.getValue()) {
                regEgrul = regEgrulRepo.findByInn(organizationDto.getOrganizationInn());
                final EGRUL.СвЮЛ svedul = mapper.readValue(regEgrul.getData(), EGRUL.СвЮЛ.class);
                juradress = JuridicalUtils.constructJuridicalAdress(svedul);
            } else if (typeOrganization == OrganizationTypes.PHYSICAL.getValue()) {
                regEgrip = regEgripRepo.findByInn(organizationDto.getOrganizationInn());
                final EGRIP.СвИП svip = mapper.readValue(regEgrip.getData(), EGRIP.СвИП.class);
                juradress = JuridicalUtils.constructJuridicalAdress(svip);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        String name = organizationDto.getOrganizationName() != null && !organizationDto.getOrganizationName().isEmpty()
                ? organizationDto.getOrganizationName() : organizationDto.getOrganizationShortName();
        ClsOrganization clsOrganization = ClsOrganization.builder()
                .name(name)
                .shortName(organizationDto.getOrganizationShortName())
                .inn(organizationDto.getOrganizationInn())
                .ogrn(organizationDto.getOrganizationOgrn())
                .addressJur(juradress)
                .okvedAdd(organizationDto.getOrganizationOkvedAdd())
                .okved(organizationDto.getOrganizationOkved())
                .email(organizationDto.getOrganizationEmail())
                .phone(organizationDto.getOrganizationPhone())
                .statusImport(0)
                .idTypeOrganization(typeOrganization)
                .principal(clsPrincipal)
                .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                .isDeleted(false)
                .isActivated(false)
                .hashCode(code)
                .build();

        clsOrganizationRepo.save(clsOrganization);

        if (organizationDto.getOrganizationPhone() != null && !organizationDto.getOrganizationPhone().isBlank()) {
            ClsOrganizationContact clsOrganizationContact = ClsOrganizationContact.builder()
                    .organization(clsOrganization)
                    .type(1)
                    .contactValue(organizationDto.getOrganizationPhone())
                    .contactPerson("")
                    .build();
            clsOrganizationContactRepo.save(clsOrganizationContact);
        }

        if (organizationDto.getOrganizationEmail() != null && !organizationDto.getOrganizationEmail().isBlank()) {
            ClsOrganizationContact clsOrganizationContact = ClsOrganizationContact.builder()
                    .organization(clsOrganization)
                    .type(2)
                    .contactValue(organizationDto.getOrganizationEmail())
                    .contactPerson("")
                    .build();
            clsOrganizationContactRepo.save(clsOrganizationContact);
        }

        if (typeOrganization == OrganizationTypes.JURIDICAL.getValue()) {
            for (RegEgrulOkved regEgrulOkved: regEgrul.getRegEgrulOkveds()) {
                Okved okved = okvedRepo.findOkvedByIdSerial(regEgrulOkved.getIdOkved());
                RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                        .clsOrganization(clsOrganization)
                        .okved(okved)
                        .build();
                RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                        .regOrganizationOkvedId(regOrganizationOkvedId)
                        .isMain(regEgrulOkved.getMain())
                        .build();
                regOrganizationOkvedRepo.save(regOrganizationOkved);
            }
        } else if (typeOrganization == OrganizationTypes.PHYSICAL.getValue()) {
            for (RegEgripOkved regEgripOkved: regEgrip.getRegEgripOkveds()) {
                Okved okved = okvedRepo.findOkvedByIdSerial(regEgripOkved.getIdOkved());
                RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                        .clsOrganization(clsOrganization)
                        .okved(okved)
                        .build();
                RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                        .regOrganizationOkvedId(regOrganizationOkvedId)
                        .isMain(regEgripOkved.getMain())
                        .build();
                regOrganizationOkvedRepo.save(regOrganizationOkved);
            }
        }

        return clsOrganization;
    }

    @Transactional
    public String changeOrganizationPassword(String inn) {

        ClsOrganization clsOrganization = clsOrganizationRepo.findByInnAndPrincipalIsNotNull(inn);
        if (clsOrganization != null) {
            ClsPrincipal clsPrincipal = clsOrganization.getPrincipal();
            if (clsPrincipal == null) {
                clsPrincipal = new ClsPrincipal();
            }

            String newPassword = PasswordGenerator.generatePassword(8);

            clsPrincipal.setPassword(passwordEncoder.encode(newPassword));
            clsPrincipalRepo.save(clsPrincipal);

            clsOrganization.setPrincipal(clsPrincipal);
            clsOrganizationRepo.save(clsOrganization);

            return newPassword;
        }

        return null;
    }

    public List<DocEmployee> getEmployeesByOrganizationId(Long id) {
        return docEmployeeRepo.findAllByOrganization(id).orElse(null);
    }

    public List<RegOrganizationOkved> getRegOrganizationOkvedAddByIdOrganization(Long id) {
        return regOrganizationOkvedRepo.findAllByIdOrganizationIsNotMain(id).orElse(new ArrayList<>());
    }

    public RegOrganizationOkved getRegOrganizationOkvedByIdOrganization(Long id) {
        return regOrganizationOkvedRepo.findAllByIdOrganizationIsMain(id).orElse(null);
    }

    @Transactional
    public ClsOrganizationContact saveOrgContact(OrganizationContactDto organizationContactDto) {
        ClsOrganization clsOrganization = clsOrganizationRepo.findById(organizationContactDto.getOrganizationId()).orElse(null);
        ClsOrganizationContact clsOrganizationContact = ClsOrganizationContact.builder()
                .id(organizationContactDto.getId())
                .organization(clsOrganization)
                .type(organizationContactDto.getType())
                .contactValue(organizationContactDto.getContactValue().trim())
                .contactPerson(organizationContactDto.getContactPerson().trim())
                .build();

        clsOrganizationContactRepo.save(clsOrganizationContact);
        return clsOrganizationContact;
    }

    @Transactional
    public void deleteOrgContact(OrganizationContactDto organizationContactDto){
        clsOrganizationContactRepo.deleteById(organizationContactDto.getId());
    }

    @Transactional
    public RegOrganizationAddressFact saveRegOrgAddressFact(RegOrganizationAddressFact regOrganizationAddressFactToSave, Long organizationId) {
//        RegOrganizationAddressFact regOrganizationAddressFact = regOrganizationAddressFactRepo.findById(regOrganizationAddressFactToSave.getId()).orElse(null);
        ClsOrganization clsOrganization = clsOrganizationRepo.findById(organizationId).orElse(null);
        //DocRequest docRequest = docRequestRepo.findById(8422L).orElse(null);
        //RegOrganizationAddressFact regOrgAddrSave = regOrganizationAddressFact == null ? regOrganizationAddressFactToSave : regOrganizationAddressFact;
        RegOrganizationAddressFact regOrgAddrSave = RegOrganizationAddressFact.builder()
                .organizationId(Integer.parseInt(organizationId.toString()))
                .docRequestAddressFact(null)
                .isDeleted(false)
                .timeCreate(new Timestamp(System.currentTimeMillis()))
                .fiasRegionObjectId(regOrganizationAddressFactToSave.getFiasRegionObjectId())
                .fiasRaionObjectId(regOrganizationAddressFactToSave.getFiasRaionObjectId())
                .fiasCityObjectId(regOrganizationAddressFactToSave.getFiasCityObjectId())
                .fiasStreetObjectId(regOrganizationAddressFactToSave.getFiasStreetObjectId())
                .fiasHouseObjectId(regOrganizationAddressFactToSave.getFiasHouseObjectId())
                .fullAddress(regOrganizationAddressFactToSave.getFullAddress())
                .isHand(false)
                .streetHand(regOrganizationAddressFactToSave.getStreetHand())
                .houseHand(regOrganizationAddressFactToSave.getHouseHand())
                .apartmentHand(regOrganizationAddressFactToSave.getApartmentHand())
                .build();
        //regOrganizationAddressFactRepo.save(regOrgAddrSave);
        if (regOrganizationAddressFactToSave.getId() == null) {
            regOrganizationAddressFactRepo.insertOrg(
                    regOrgAddrSave.getOrganizationId(),
                    regOrgAddrSave.getFiasRegionObjectId(),
                    regOrgAddrSave.getFiasRaionObjectId(),
                    regOrgAddrSave.getFiasCityObjectId(),
                    regOrgAddrSave.getFiasStreetObjectId(),
                    regOrgAddrSave.getFiasHouseObjectId(),
                    //regOrgAddrSave.getFiasObjectId(),
                    regOrgAddrSave.getStreetHand(),
                    regOrgAddrSave.getHouseHand(),
                    regOrgAddrSave.getApartmentHand(),
                    regOrgAddrSave.getFullAddress()
            );
        } else {
            regOrganizationAddressFactRepo.updateOrg(
                    regOrganizationAddressFactToSave.getId(),
                    regOrgAddrSave.getOrganizationId(),
                    regOrgAddrSave.getFiasRegionObjectId(),
                    regOrgAddrSave.getFiasRaionObjectId(),
                    regOrgAddrSave.getFiasCityObjectId(),
                    regOrgAddrSave.getFiasStreetObjectId(),
                    regOrgAddrSave.getFiasHouseObjectId(),
                    //regOrgAddrSave.getFiasObjectId(),
                    regOrgAddrSave.getStreetHand(),
                    regOrgAddrSave.getHouseHand(),
                    regOrgAddrSave.getApartmentHand(),
                    regOrgAddrSave.getFullAddress()
            );
        }
        return regOrgAddrSave;
    }

    @Transactional
    public void deleteRegOrgAddressFact(RegOrganizationAddressFact regOrganizationAddressFact) {
        regOrganizationAddressFactRepo.customDeleteById(regOrganizationAddressFact.getId());
    }

    @Transactional
    public DocEmployee saveEmployee(EmployeeDto employeeDto) {
        DocPerson docPerson = employeeDto.getPerson().convertToPersonEntity();

        ClsOrganization clsOrganization = clsOrganizationRepo.findById(employeeDto.getOrganizationId()).orElse(null);
        //DocRequest docRequest = docRequestRepo.findOneByOrganizationId(clsOrganization.getId()).get().get(0);
        DocEmployee docEmployee;
        Long personId = employeeDto.getPerson().getId();

        if(personId!=null){          //Если существует person с personId в таблице doc_person
            docPerson.setId(personId);
            docEmployee = constructUpdatePerson(docPerson,employeeDto,clsOrganization);
        }else{
            docPerson.setDeleted(false);
            docEmployee = constructNewPerson(docPerson,employeeDto,clsOrganization);
        }

        docEmployeeRepo.save(docEmployee);

        return docEmployee;
    }

    //Добавление нового сотрудника
    public DocEmployee constructNewPerson(DocPerson docPerson, EmployeeDto employeeDto, ClsOrganization clsOrganization){
        docPersonRepo.save(docPerson);

        DocEmployee newEmployee = DocEmployee.builder()
                .id(employeeDto.getId())
                .organization(clsOrganization)
                .person(docPerson)
                .isVaccinatedFlu(employeeDto.getIsVaccinatedFlu())
                .isVaccinatedCovid(employeeDto.getIsVaccinatedCovid())
                .isDeleted(false)
                .build();

        return newEmployee;
     }

    //Добавление отредактированного сотрудника
    public DocEmployee constructUpdatePerson(DocPerson docPerson, EmployeeDto employeeDto, ClsOrganization clsOrganization){

        Long personId = docPerson.getId();
        DocPerson updatePerson = new DocPerson();
        Optional<DocPerson> optionalDocPerson = docPersonRepo.findById(personId);
        if(optionalDocPerson.isPresent()){

            updatePerson = optionalDocPerson.get();
        }
        updatePerson.setId(personId);
        updatePerson.setFirstname(docPerson.getFirstname());
        updatePerson.setLastname(docPerson.getLastname());
        updatePerson.setPatronymic(docPerson.getPatronymic());
        docPersonRepo.save(updatePerson);

        DocEmployee updatedEmployee = DocEmployee.builder()
                .id(employeeDto.getId())
                .organization(clsOrganization)
                .person(updatePerson)
                .isVaccinatedFlu(employeeDto.getIsVaccinatedFlu())
                .isVaccinatedCovid(employeeDto.getIsVaccinatedCovid())
                .build();

        return updatedEmployee;
    }

    @Transactional
    public void deleteEmployee(EmployeeDto employeeDto){
        //update reg_organization_file set is_deleted = true where id=:id
        Long personId = employeeDto.getPerson().getId();

        docEmployeeRepo.setEmployeeIsDeletedTrueById(employeeDto.getId());
        docPersonRepo.setPersonIsDeletedTrueById(personId);

    }

    @Transactional
    public void saveMailing(ClsMailingListDto clsMailingListDto, ClsPrincipal principal){
        Long id_clsMailingList = clsMailingListDto.getId();
        ClsMailingList clsMailingList = clsMailingListRepo.findById(id_clsMailingList).orElse(null);
        RegMailingListFollower regMailingListFollower = regMailingListFollowerRepo.findByPrincipalAndMailingList(principal, clsMailingList);
        if (regMailingListFollower == null) {
            regMailingListFollower = new RegMailingListFollower();
            regMailingListFollower.setPrincipal(principal);
            regMailingListFollower.setMailingList(clsMailingList);
            regMailingListFollower.setActivationDate(new Timestamp(new Date().getTime()));
            regMailingListFollowerRepo.save(regMailingListFollower);
        }
        else {
            // Если был деактивирован
            if (regMailingListFollower.getDectivationDate() != null) {
                regMailingListFollower.setDectivationDate(null);
                regMailingListFollower.setActivationDate(new Timestamp(new Date().getTime()));
                regMailingListFollowerRepo.save(regMailingListFollower);
            }
        }
    }

    @Transactional
    public void deactivateMailing(ClsMailingListDto clsMailingListDto, ClsPrincipal principal){
        Long id_clsMailingList = clsMailingListDto.getId();
        ClsMailingList clsMailingList = clsMailingListRepo.findById(id_clsMailingList).orElse(null);
        RegMailingListFollower regMailingListFollower = regMailingListFollowerRepo.findByPrincipalAndMailingList(principal, clsMailingList);
        if (regMailingListFollower != null) {
            if (regMailingListFollower.getDectivationDate() == null) {
                regMailingListFollower.setDectivationDate(new Timestamp(new Date().getTime()));
                regMailingListFollowerRepo.save(regMailingListFollower);
            }
        }
    }

    public List<ClsOrganizationContact> getAllClsOrganizationContactByOrganizationId(Long id){
        return clsOrganizationContactRepo.findAllByOrganization(id).orElse(null);
    }

    /**
     * Метод предназначен для сохранения заявки по предписанию
     *
     * @param postForm
     * @return
     */
    @Transactional
    public DocRequest saveNewRequest(PostFormDto postForm) {
        DocRequest docRequest;

        ClsOrganization organization = clsOrganizationRepo.findById(postForm.getOrganizationId()).orElse(null);

        ClsTypeRequest typeRequest = clsTypeRequestRepo.findById(postForm.getTypeRequestId()).orElse(null);

        if (postForm.getRequestId() != null) {
            docRequest = docRequestRepo.findById(postForm.getRequestId()).orElse(null);
            docRequest.setStatusActivity(ActivityStatuses.HISTORICAL.getValue());
            docRequestRepo.save(docRequest);

            DocRequest newDocRequest = DocRequest.builder()
                    .organization(organization)
                    .department(departmentRepo.getOne(postForm.getDepartmentId()))
                    .personOfficeCnt(0L)
                    .personRemoteCnt(0L)
                    .personSlrySaveCnt(0L)
                    .statusReview(ReviewStatuses.OPENED.getValue())
                    .statusImport(0)
                    .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                    .typeRequest(typeRequest)
                    .actualizedRequest(docRequest)
                    .isActualization(true)
                    .statusActivity(ActivityStatuses.ACTIVE.getValue())
                    .build();

            docRequestRepo.save(newDocRequest);

            docRequest = newDocRequest;
        } else {
            docRequest = DocRequest.builder()
                    .organization(organization)
                    .department(departmentRepo.getOne(postForm.getDepartmentId()))
                    .personOfficeCnt(0L)
                    .personRemoteCnt(0L)
                    .personSlrySaveCnt(0L)
                    .statusReview(ReviewStatuses.OPENED.getValue())
                    .statusImport(0)
                    .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                    .typeRequest(typeRequest)
                    .statusActivity(ActivityStatuses.ACTIVE.getValue())
                    .build();
        }

        docRequest.setReqBasis(postForm.getReqBasis());

        docRequestRepo.save(docRequest);

        if (postForm.getOrganizationFileIds() != null) {
            for (int organizationFileId : postForm.getOrganizationFileIds()) {
                RegDocRequestFile regDocRequestFile = RegDocRequestFile.builder()
                        .request(docRequest)
                        .organizationFile(new RegOrganizationFile(organizationFileId))
                        .build();
                regDocRequestFileRepo.save(regDocRequestFile);
            }
        }

        if (postForm.getDocRequestPrescriptions() != null && postForm.getDocRequestPrescriptions().size() > 0) {
            for (DocRequestPrescriptionDto dto : postForm.getDocRequestPrescriptions()) {
                RegDocRequestPrescription regDocRequestPrescription = RegDocRequestPrescription.builder()
                        .request(docRequest)
                        .prescription(new ClsPrescription(dto.getPrescriptionId()))
                        .additionalAttributes(dto.getAdditionalAttributes())
                        .build();
                regDocRequestPrescriptionRepo.save(regDocRequestPrescription);
            }
        }

        return docRequest;
    }

    public String activateOrganization(String inn, String code) {
        String message = "";
        try {
            ClsOrganization clsOrganization = clsOrganizationRepo.findByInnAndHashCode(inn, code);
            if (clsOrganization != null) {
                if (!clsOrganization.getActivated().booleanValue()) {
                    clsOrganization.setActivated(true);
                    clsOrganizationRepo.save(clsOrganization);
                    message = "Учётная запись успешно активирована.";
                } else {
                    message = "Активация не требуется.";
                }
            } else {
                message = "Учетная запись не найдена!";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = "Не удалось активировать учётную запись!";
        }
        return message;
    }

    public Page<ClsNews> findNewsArchiveByOrganization_Id(Long id, int page, int size) {
        List<ClsNews> newsList = clsNewsRepo.getNewsArchiveByOrganization_Id(id, new Timestamp(System.currentTimeMillis())).stream().collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        long start = pageable.getOffset();
        long end = (start + pageable.getPageSize()) > newsList.size() ? newsList.size() : (start + pageable.getPageSize());

        Page<ClsNews> pages = new PageImpl<ClsNews>(newsList.subList((int) start, (int) end), pageable, newsList.size());
        return pages;
    }

    public void saveLinkClicks(HttpServletRequest request, ClsNews clsNews) {
        try {
            String ip = getClientIp(request);
            RegNewsLinkClicks rnlc =  RegNewsLinkClicks.builder()
                    .news(clsNews)
                    .ip(ip)
                    .time(new Timestamp(System.currentTimeMillis()))
                    .build();
            regNewsLinkClicksRepo.save(rnlc);
        }
        catch (Exception e) {
            log.error(String.format("Переход по новости id =%s не сохранен", ((clsNews!=null)?clsNews.getId():"null")));
        }

    }

    private static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    public void saveRegPersonCount(ClsOrganization organization,
                                             Integer personOfficeCnt,
                                             Integer personRemoteCnt) {
        RegPersonCount rpc = RegPersonCount.builder()
                .organization(organization)
                .timeEdit(new Timestamp(System.currentTimeMillis()))
                .personOfficeCnt(personOfficeCnt)
                .personRemoteCnt(personRemoteCnt)
                .build();

        regPersonCountRepo.save(rpc);
    }

    public List<ClsPrescriptionDto> getClsPrescriptionsByOrganizationId(Long id) {
        List<DocRequestPrs> requests = docRequestPrsRepo.getActualizedRequestsByOrganizationId(id);
        Set<Long> typeRequestIds = requests.stream().map(request -> request.getTypeRequest().getId()).collect(Collectors.toSet());
        List<ClsPrescription> newPrescriptions = clsPrescriptionRepo.getPrescriptionsByTypeRequestIds(typeRequestIds, id, PublicationStatuses.PUBLISHED.getValue());
        List<ClsPrescriptionDto> newDtos = newPrescriptions.stream().map(prescription ->
                new ClsPrescriptionDto(prescription.getId(), prescription.getName(), prescription.getTypeRequest().getActivityKind(), prescription.getTimePublication(), false))
                .collect(Collectors.toList());
        // предписания, с которыми организация ознакомлена
        List<ClsPrescription> orgPrescriptions = clsPrescriptionRepo.getPrescriptionsByOrganizationId(id);
        List<ClsPrescriptionDto> orgDtos = orgPrescriptions.stream().map(prescription ->
                new ClsPrescriptionDto(prescription.getId(), prescription.getName(), prescription.getTypeRequest().getActivityKind(), prescription.getTimePublication(), true))
                .collect(Collectors.toList());
        newDtos.addAll(orgDtos);
        return newDtos;
    }

    public Integer getCountOfNewClsPrescriptionsByOrgId(Long id) {
        List<DocRequestPrs> requests = docRequestPrsRepo.getActualizedRequestsByOrganizationId(id);
        Set<Long> typeRequestIds = requests.stream().map(request -> request.getTypeRequest().getId()).collect(Collectors.toSet());
        List<ClsPrescription> newPrescriptions = clsPrescriptionRepo.getPrescriptionsByTypeRequestIds(typeRequestIds, id, PublicationStatuses.PUBLISHED.getValue());
        if (newPrescriptions != null) {
            return newPrescriptions.size();
        }
        else {
            return 0;
        }
    }

    public ClsPrescription getClsPrescriptionById(Long id) {
        return clsPrescriptionRepo.findById(id).orElse(null);
    }

    public RegOrganizationPrescription addOrganizationPrescription(Long id, OrganizationPrescriptionDto dto) {
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        ClsPrescription prescription = clsPrescriptionRepo.findById(dto.getPrescriptionId()).orElse(null);

        RegOrganizationPrescription organizationPrescription = RegOrganizationPrescription.builder()
                .organization(organization)
                .prescription(prescription)
                .additionalAttributes(dto.getAdditionalAttributes())
                .build();
        regOrganizationPrescriptionRepo.save(organizationPrescription);

        return organizationPrescription;
    }

    public List<ClsPrescription> getClsPrescriptionsByTypeRequestId(Long idTypeRequest) {
        List<ClsPrescription> prescriptions = clsPrescriptionRepo.getPrescriptionsByTypeRequestId(idTypeRequest, PublicationStatuses.PUBLISHED.getValue());
        return prescriptions;
    }

    public Long getRequestByOrganizationIdAndTypeRequestId(Long orgId, Long typeRequestId, Integer status) {
        return docRequestRepo.getRequestByOrganizationIdAndTypeRequestId(orgId, typeRequestId, status);
    }
}
