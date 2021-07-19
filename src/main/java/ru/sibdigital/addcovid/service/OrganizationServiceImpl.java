package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.sibdigital.addcovid.dto.EgripResponse;
import ru.sibdigital.addcovid.dto.EgrulResponse;
import ru.sibdigital.addcovid.dto.OrganizationDto;
import ru.sibdigital.addcovid.dto.esia.Organization;
import ru.sibdigital.addcovid.dto.esia.User;
import ru.sibdigital.addcovid.dto.esia.UserOrganization;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.classifier.gov.*;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.repository.classifier.gov.OkvedRepo;
import ru.sibdigital.addcovid.repository.classifier.gov.RegEgripRepo;
import ru.sibdigital.addcovid.repository.classifier.gov.RegEgrulRepo;
import ru.sibdigital.addcovid.repository.classifier.gov.RegFilialRepo;
import ru.sibdigital.addcovid.service.crassifier.EgrulService;
import ru.sibdigital.addcovid.service.esia.EsiaService;
import ru.sibdigital.addcovid.utils.SHA256Generator;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private RegEgrulRepo regEgrulRepo;

    @Autowired
    private RegEgripRepo regEgripRepo;

    @Autowired
    private RegFilialRepo regFilialRepo;

    @Autowired
    private ClsOrganizationContactRepo clsOrganizationContactRepo;

    @Autowired
    private OkvedRepo okvedRepo;

    @Autowired
    private RegOrganizationOkvedRepo regOrganizationOkvedRepo;

    @Autowired
    private EsiaService esiaService;

    @Autowired
    private EgrulService egrulService;

    @Autowired
    private DBActualizeService dbActualizeService;

    @Autowired
    private ClsSettingsRepo  clsSettingsRepo;

    @Transactional
    public ClsOrganization saveNewClsOrganization(OrganizationDto organizationDto) {
        RegEgrul regEgrul = null;
        RegEgrip regEgrip = null;

        ClsPrincipal clsPrincipal = ClsPrincipal.builder()
                .password(organizationDto.getPassword() != null ? passwordEncoder.encode(organizationDto.getPassword()) : "")
                .build();

        clsPrincipalRepo.save(clsPrincipal);

        int typeOrganization = organizationDto.getOrganizationType();

        String code = SHA256Generator.generate(organizationDto.getOrganizationInn(), organizationDto.getOrganizationName(), String.valueOf(System.currentTimeMillis()));

        RegOrganizationClassifier regOrganizationClassifier = null;
        if (organizationDto.hasEgrulOrEgrip()) {
            if (typeOrganization == OrganizationTypes.JURIDICAL.getValue() || typeOrganization == OrganizationTypes.FILIATION.getValue()
                    || typeOrganization == OrganizationTypes.REPRESENTATION.getValue() || typeOrganization == OrganizationTypes.DETACHED.getValue()) {
                regEgrul = regEgrulRepo.findById(organizationDto.getEgrulId()).orElse(null);
                if (regEgrul != null) {
                    RegFilial regFilial = null;
                    if (typeOrganization == OrganizationTypes.FILIATION.getValue() || typeOrganization == OrganizationTypes.REPRESENTATION.getValue()) {
                        regFilial = regFilialRepo.findById(organizationDto.getFilialId()).orElse(null);
                    }
                    regOrganizationClassifier = RegOrganizationClassifier.builder()
                            .regEgrul(regEgrul)
                            .regFilial(regFilial)
                            .build();
                }
            } else if (typeOrganization == OrganizationTypes.IP.getValue() || typeOrganization == OrganizationTypes.KFH.getValue()) {
                regEgrip = regEgripRepo.findById(organizationDto.getEgripId()).orElse(null);
                if (regEgrip != null) {
                    regOrganizationClassifier = RegOrganizationClassifier.builder()
                            .regEgrip(regEgrip)
                            .build();
                }
            }
        }

        String name = organizationDto.getOrganizationName() != null && !organizationDto.getOrganizationName().isEmpty()
                ? organizationDto.getOrganizationName() : organizationDto.getOrganizationShortName();
        ClsOrganization clsOrganization = ClsOrganization.builder()
                .name(name)
                .shortName(organizationDto.getOrganizationShortName())
                .inn(organizationDto.getOrganizationInn())
                .ogrn(organizationDto.getOrganizationOgrn())
                .kpp(organizationDto.getOrganizationKpp())
                .addressJur(organizationDto.getOrganizationAddressJur())
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
                .regOrganizationClassifier(regOrganizationClassifier)
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

        if (organizationDto.hasEgrulOrEgrip()) {
            if (typeOrganization == OrganizationTypes.JURIDICAL.getValue() || typeOrganization == OrganizationTypes.FILIATION.getValue()
                    || typeOrganization == OrganizationTypes.REPRESENTATION.getValue() || typeOrganization == OrganizationTypes.DETACHED.getValue()) {
                for (RegEgrulOkved regEgrulOkved : regEgrul.getRegEgrulOkveds()) {
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
            } else if (typeOrganization == OrganizationTypes.IP.getValue() || typeOrganization == OrganizationTypes.KFH.getValue()) {
                for (RegEgripOkved regEgripOkved : regEgrip.getRegEgripOkveds()) {
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
        }

        return clsOrganization;
    }

    @Transactional
    public ClsOrganization saveNewClsOrganizationAsActivated(OrganizationDto organizationDto) {
        ClsOrganization clsOrganization = saveNewClsOrganization(organizationDto);
        clsOrganization.setActivated(true);
        saveClsOrganization(clsOrganization);
        return clsOrganization;
    }

    public ClsOrganization saveClsOrganization(ClsOrganization organization) {
        return clsOrganizationRepo.save(organization);
    }

    public ClsOrganization findById(Long id) {
        return clsOrganizationRepo.findById(id).orElse(null);
    }

    public ClsOrganization findByInnAndPrincipalIsNotNull(String inn, List<Integer> typeOrganizations) {
        return clsOrganizationRepo.findByInnAndPrincipalIsNotNull(inn, typeOrganizations);
    }

    public List<OrganizationDto> getOrganizationsByEsia() {
        User user = esiaService.getUser();
        if (user == null) {
            return new ArrayList<>(0);
        }

        if (user.getSelfEmployed().getConfirmed() != null && user.getSelfEmployed().getConfirmed().booleanValue()) {
            OrganizationDto organization = OrganizationDto.builder()
                    .organizationShortName(user.getFIO())
                    .organizationName(user.getFIO())
                    .organizationInn(user.getInn())
                    .organizationEmail(user.getEmail())
                    .organizationType(OrganizationTypes.SELF_EMPLOYED.getValue())
                    .build();
            return Arrays.asList(organization);
        }

        List<Organization> organizations = esiaService.getUserOrganizations();

        organizations = organizations.stream()
                .filter(o -> o.getInn() != null && !o.getInn().isBlank())
                .collect(Collectors.toList());

        return convertToOrganizationDtos(organizations);
    }

    private List<OrganizationDto> convertToOrganizationDtos(List<Organization> esiaOrgs) {
        List<OrganizationDto> organizations = new ArrayList<>();
        for (Organization esiaOrg : esiaOrgs) {
            Long egrulId = null;
            Long egripId = null;
            String kpp = "";
            String jurAddress = "";
            String email = "";

            UserOrganization userOrganization = esiaOrg.getUserOrganization();
            email = userOrganization.getEmail();

            int organizationType = OrganizationTypes.JURIDICAL.getValue();

            if (userOrganization.getType().equals("BUSINESS")) {
                organizationType = OrganizationTypes.IP.getValue();

                List<RegEgrip> egrips = egrulService.getEgrip(esiaOrg.getInn());
                if (egrips != null && egrips.size() > 0) {
                    EgripResponse egripResponse = new EgripResponse();
                    egripResponse.build(egrips);
                    EgripResponse.Data data = egripResponse.getData().stream()
                            .filter(egrip -> egrip.getType() == OrganizationTypes.IP.getValue())
                            .findFirst().orElse(null);
                    if (data != null) {
                        egripId = data.getId();
                        jurAddress = data.getJurAddress();
                        if (!StringUtils.isEmpty(data.getEmail())) {
                            email = data.getEmail();
                        }
                    }
                }
            } else {
                RegEgrul egrul = egrulService.getEgrul(esiaOrg.getInn());
                if (egrul != null) {
                    EgrulResponse egrulResponse = new EgrulResponse();
                    egrulResponse.build(egrul);
                    EgrulResponse.Data data = egrulResponse.getData();

                    egrulId = data.getId();
                    kpp = data.getKpp();
                    jurAddress = data.getJurAddress();
                    if (!StringUtils.isEmpty(data.getEmail())) {
                        email = data.getEmail();
                    }
                }
            }

            OrganizationDto organization = OrganizationDto.builder()
                    .organizationShortName(userOrganization.getShortName())
                    .organizationName(userOrganization.getFullName())
                    .organizationInn(esiaOrg.getInn())
                    .organizationOgrn(esiaOrg.getOgrn())
                    .organizationKpp(kpp)
                    .organizationAddressJur(jurAddress)
                    .organizationEmail(email)
                    .organizationType(organizationType)
                    .egrulId(egrulId)
                    .egripId(egripId)
                    .filialId(null)
                    .esiaId(userOrganization.getOid())
                    .build();

            organizations.add(organization);
        }
        return organizations;
    }

    public ClsOrganization updateClsOrganizationByEgrul(EgrulResponse.Data egrulData, Long id_organization){
        ClsOrganization clsOrganization = clsOrganizationRepo.findById(id_organization).orElse(null);
        int oldOrgType = clsOrganization.getIdTypeOrganization();

        clsOrganization.setName(egrulData.getName());
        clsOrganization.setShortName(egrulData.getShortName());
        //clsOrganization.setInn(egrulData.getInn());
        clsOrganization.setOgrn(egrulData.getOgrn());
        clsOrganization.setAddressJur(egrulData.getJurAddress());
        clsOrganization.setKpp(egrulData.getKpp());
        //clsOrganization.setIdTypeOrganization(egrulData.getType());

        dbActualizeService.addDataFromEgrul(clsOrganization);

        clsOrganizationRepo.save(clsOrganization);
        log.warn("Refresh from EGRUL: " + clsOrganization.getId()
                + "inn " + clsOrganization.getInn() + " type org: " + clsOrganization.getIdTypeOrganization()
                + " old type org: " + oldOrgType);

        return clsOrganization;
    }

    public ClsOrganization updateClsOrganizationByEgrip(EgripResponse.Data egripData, Long id_organization){
        ClsOrganization clsOrganization = clsOrganizationRepo.findById(id_organization).orElse(null);
        int oldOrgType = clsOrganization.getIdTypeOrganization();

        clsOrganization.setName(egripData.getName());
        clsOrganization.setShortName(egripData.getName());
        clsOrganization.setOgrn(egripData.getOgrn());
        clsOrganization.setAddressJur(egripData.getJurAddress());
        //clsOrganization.setIdTypeOrganization(egripData.getType());

        dbActualizeService.addDataFromEgrul(clsOrganization);

        clsOrganizationRepo.save(clsOrganization);
        log.warn("Refresh from EGRIP: " + clsOrganization.getId()
                + "inn " + clsOrganization.getInn() + " type org: " + clsOrganization.getIdTypeOrganization()
                + " old type org: " + oldOrgType);

        return clsOrganization;
    }
}
