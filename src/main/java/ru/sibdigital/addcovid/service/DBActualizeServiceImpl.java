package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.classifier.gov.*;
import ru.sibdigital.addcovid.model.egr.EgripTypes;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.repository.classifier.gov.OkvedRepo;
import ru.sibdigital.addcovid.service.crassifier.EgrulService;
//import ru.sibdigital.addcovid.repository.classifier.ClsOrganizationRepo;
//import ru.sibdigital.addcovid.repository.classifier.ClsPrescriptionRepo;
//import ru.sibdigital.addcovid.repository.classifier.ClsPrincipalRepo;
//import ru.sibdigital.addcovid.repository.document.DocEmployeeRepo;
//import ru.sibdigital.addcovid.repository.document.DocRequestRepo;
//import ru.sibdigital.addcovid.repository.regisrty.*;
//import ru.sibdigital.addcovid.service.EgrulService;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Сервис для актуализации БД под новую схему данных
 *
 */
@Slf4j
@Service
public class DBActualizeServiceImpl implements DBActualizeService {

    @Value("${upload.path}")
    private String uploadingDir;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private EgrulService egrulService;

    @Autowired
    private OkvedRepo okvedRepo;

    @Autowired
    private RegOrganizationOkvedRepo regOrganizationOkvedRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private DocEmployeeRepo docEmployeeRepo;

    @Autowired
    private RegOrganizationAddressFactRepo regOrganizationAddressFactRepo;

    @Autowired
    private RegDocRequestEmployeeRepo regDocRequestEmployeeRepo;

    @Autowired
    private ClsPrescriptionRepo clsPrescriptionRepo;

    @Autowired
    private RegDocRequestPrescriptionRepo regDocRequestPrescriptionRepo;

    @Autowired
    private RegOrganizationPrescriptionRepo regOrganizationPrescriptionRepo;

//    @Transactional
//    public long actualizeOrganizations(List<ClsOrganization> organizations) {
//        long countActualized = 0;
//        if (organizations == null) {
//            return countActualized;
//        }
//        for (ClsOrganization organization : organizations) {
//            if (organization.getActualized() != null && organization.getActualized()) {
//                continue;
//            }
//            // добавим окведы
//            addDataFromEgrul(organization);
//            // актуализируем заявки
//            List<DocRequest> actualRequests = actualizeRequests(organization);
//            // добавим сотрудников, адреса
//            addDataFromRequests(organization, actualRequests);
//            // создадим принципала
//            if (organization.getPrincipal() == null) {
//                ClsPrincipal principal = ClsPrincipal.builder()
//                        .organization(organization)
////                        .password(passwordEncoder.encode(PasswordGenerator.generatePassword(8)))
//                        .password("")
//                        .build();
//                clsPrincipalRepo.save(principal);
//                organization.setPrincipal(principal);
//            }
//
//            organization.setActivated(true);
//            organization.setActualized(true);
//            organization.setTimeActualization(new Timestamp(System.currentTimeMillis()));
//            clsOrganizationRepo.save(organization);
//            countActualized++;
//        }
//        return countActualized;
//    }

//    /**
//     * Метод для переноса данных из заявок, таких как сотрудники и адреса
//     * @param organization
//     * @param requests
//     */
//    private void addDataFromRequests(ClsOrganization organization, List<DocRequest> requests) {
//        if (requests == null) {
//            return;
//        }
//        Map<Integer, DocEmployee> employees = new HashMap<>();
//        Set<Integer> addresses = new HashSet<>();
//        for (DocRequest request: requests) {
//            // добавим сотрудников
//            for (DocPerson docPerson: request.getDocPersonList()) {
//                DocEmployee employee = employees.get(docPerson.hashCode());
//                if (employee == null) {
//                    employee = DocEmployee.builder()
//                            .organization(organization)
//                            .person(docPerson)
//                            .isDeleted(false)
//                            .build();
//                    docEmployeeRepo.save(employee);
//                    employees.put(docPerson.hashCode(), employee);
//                }
//                RegDocRequestEmployee regDocRequestEmployee = RegDocRequestEmployee.builder()
//                        .request(request)
//                        .docEmployee(employee)
//                        .build();
//                regDocRequestEmployeeRepo.save(regDocRequestEmployee);
//            }
//            // добавим адреса
//            for (DocAddressFact docAddressFact: request.getDocAddressFact()) {
//                if (!addresses.contains(docAddressFact.hashCode())) {
//                    RegOrganizationAddressFact regOrganizationAddressFact = RegOrganizationAddressFact.builder()
//                            .clsOrganization(organization)
//                            .docRequest(docAddressFact.getDocRequest())
//                            .fullAddress(docAddressFact.getAddressFact())
//                            .isHand(true)
//                            .timeCreate(new Timestamp(System.currentTimeMillis()))
//                            .build();
//                    regOrganizationAddressFactRepo.save(regOrganizationAddressFact);
//                    addresses.add(docAddressFact.hashCode());
//                }
//            }
//            // добавим предписания и согласия
//            ClsPrescription prescription = clsPrescriptionRepo.findByTypeRequestId(request.getTypeRequest().getId());
//            if (prescription != null) {
//                List<ConsentPrescription> consentPrescriptions = new ArrayList<>();
//                if (prescription.getPrescriptionTexts() != null) {
//                    for (RegPrescriptionText regPrescriptionText : prescription.getPrescriptionTexts()) {
//                        consentPrescriptions.add(new ConsentPrescription(regPrescriptionText.getId().toString(), "1"));
//                    }
//                }
//                RegDocRequestPrescriptionAttributes regDocRequestPrescriptionAttributes = new RegDocRequestPrescriptionAttributes(consentPrescriptions.toArray(new ConsentPrescription[0]));
//                RegDocRequestPrescription regDocRequestPrescription = RegDocRequestPrescription.builder()
//                        .request(request)
//                        .prescription(prescription)
//                        .additionalAttributes(regDocRequestPrescriptionAttributes)
//                        .build();
//                regDocRequestPrescriptionRepo.save(regDocRequestPrescription);
//                //
//                RegOrganizationPrescriptionAttributes regOrganizationPrescriptionAttributes = new RegOrganizationPrescriptionAttributes(consentPrescriptions.toArray(new ConsentPrescription[0]));
//                RegOrganizationPrescription regOrganizationPrescription = RegOrganizationPrescription.builder()
//                        .organization(organization)
//                        .prescription(prescription)
//                        .additionalAttributes(regOrganizationPrescriptionAttributes)
//                        .build();
//                regOrganizationPrescriptionRepo.save(regOrganizationPrescription);
//            }
//        }
//    }

//    private List<DocRequest> actualizeRequests(ClsOrganization organization) {
//        // найдем утвержденные заявки по ИНН
//        List<DocRequest> allRequestsByOrgInn = docRequestRepo.getRequestsByInn(organization.getInn()).orElse(null);
//        if (allRequestsByOrgInn == null) {
//            return null;
//        }
//        // сгруппируем заявки по виду деятельности
//        Map<ClsTypeRequest, List<DocRequest>> requestMap = allRequestsByOrgInn.stream().collect(Collectors.groupingBy(DocRequest::getTypeRequest));
//        // в каждой группе найдем одну актуальную заявку и сформируем список заявок организации
//        List<DocRequest> actualRequests = new ArrayList<>();
//        for (Map.Entry<ClsTypeRequest, List<DocRequest>> entry: requestMap.entrySet()) {
//            // заявки ИД текущей организации делаем историческими
//            List<DocRequest> requests = entry.getValue().stream().filter(request -> Objects.equals(request.getOrganization().getId(), organization.getId())).collect(Collectors.toList());
//            for (DocRequest request : requests) {
//                request.setStatusActivity(ActivityStatuses.HISTORICAL.getValue());
//                docRequestRepo.save(request);
//            }
//            // найдем последнюю утвержденную заявку среди заявок по ИНН
//            DocRequest lastRequest = entry.getValue().stream().filter(request -> request.getStatusReview() == ReviewStatuses.CONFIRMED.getValue())
//                    .max(Comparator.comparing(DocRequest::getTimeReview)).orElse(null);
//            // если не нашли, ищем открытую
//            if (lastRequest == null) {
//                lastRequest = entry.getValue().stream().filter(request -> request.getStatusReview() == ReviewStatuses.OPENED.getValue())
//                        .max(Comparator.comparing(DocRequest::getTimeCreate)).orElse(null);
//            }
//            // если не нашли, ищем прочую
//            if (lastRequest == null) {
//                lastRequest = entry.getValue().stream().filter(request -> request.getStatusReview() == ReviewStatuses.ACCEPTED.getValue())
//                        .max(Comparator.comparing(DocRequest::getTimeCreate)).orElse(null);
//            }
//            if (lastRequest != null) {
//                if (!Objects.equals(lastRequest.getOrganization().getId(), organization.getId())) {
//                    lastRequest.setOrganization(organization);
//                }
//                lastRequest.setStatusActivity(ActivityStatuses.ACTIVE.getValue());
//                docRequestRepo.save(lastRequest);
//                actualRequests.add(lastRequest);
//            }
//        }
//        return actualRequests;
//    }

    /**
     * Метод для добавления данных к организации из ЕГРЮЛ/ЕГРИП
     * @param organization
     */
    public void addDataFromEgrul(ClsOrganization organization) {
        String inn = organization.getInn();
        if (inn.length() == 10) {
            RegEgrul regEgrul = egrulService.getEgrul(inn);
            if (regEgrul != null) {

                if (regEgrul.getRegEgrulOkveds().isEmpty() == false) {
                    final Optional<List<RegOrganizationOkved>> okveds = regOrganizationOkvedRepo
                            .findAllByIdOrganization(organization.getId());
                    if (okveds.isPresent()) {
                        regOrganizationOkvedRepo.deleteAll(okveds.get());
                    }
                }

                for (RegEgrulOkved regEgrulOkved : regEgrul.getRegEgrulOkveds()) {
                    Okved okved = okvedRepo.findOkvedByIdSerial(regEgrulOkved.getIdOkved());
                    RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                            .clsOrganization(organization)
                            .okved(okved)
                            .build();
                    RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                            .regOrganizationOkvedId(regOrganizationOkvedId)
                            .isMain(regEgrulOkved.getMain())
                            .build();
                    regOrganizationOkvedRepo.save(regOrganizationOkved);
                }
                organization.setIdTypeOrganization(OrganizationTypes.JURIDICAL.getValue());
                organization.setKpp(regEgrul.getKpp());
                organization.setRegOrganizationClassifier(new RegOrganizationClassifier(null, regEgrul, null, null));
            }
        } else {
            List<RegEgrip> regEgrips = egrulService.getEgrip(inn);
            if (regEgrips != null && regEgrips.size() > 0) {
                RegEgrip regEgrip = regEgrips.stream().findFirst().get();

                if (regEgrip.getRegEgripOkveds().isEmpty() == false) {
                    final Optional<List<RegOrganizationOkved>> okveds = regOrganizationOkvedRepo
                            .findAllByIdOrganization(organization.getId());
                    if (okveds.isPresent()) {
                        regOrganizationOkvedRepo.deleteAll(okveds.get());
                    }
                }

                for (RegEgripOkved regEgripOkved : regEgrip.getRegEgripOkveds()) {
                    Okved okved = okvedRepo.findOkvedByIdSerial(regEgripOkved.getIdOkved());
                    RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                            .clsOrganization(organization)
                            .okved(okved)
                            .build();
                    RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                            .regOrganizationOkvedId(regOrganizationOkvedId)
                            .isMain(regEgripOkved.getMain())
                            .build();
                    regOrganizationOkvedRepo.save(regOrganizationOkved);
                }
                if (regEgrip.getTypeEgrip().equals(EgripTypes.INDIVIDUAL_ENTREPRENEUR)) {
                    organization.setIdTypeOrganization(OrganizationTypes.IP.getValue());
                } else {
                    organization.setIdTypeOrganization(OrganizationTypes.KFH.getValue());
                }
                organization.setRegOrganizationClassifier(new RegOrganizationClassifier(null, null, regEgrip, null));
            }
        }
    }

//    @Transactional
//    public void markOrganizationAsDeleted(){
//        final List<ClsOrganization> organizations = clsOrganizationRepo.getNotActualOrganization(ReviewStatuses.CONFIRMED.getValue());
//
//        for (ClsOrganization org : organizations){
//            org.setDeleted(true);
//        }
//        clsOrganizationRepo.saveAll(organizations);
//    }
}
