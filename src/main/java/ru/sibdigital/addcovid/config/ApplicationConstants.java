package ru.sibdigital.addcovid.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "constants.yml", encoding = "UTF-8")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApplicationConstants {

    @Value("${application-name}")
    private String applicationName;

    @Value("${contacts}")
    private String contacts;

    @Value("${ref-hot-line}")
    private String refHotLine;

    @Value("${ref-form-fill-instruction}")
    private String refFormFillInstruction;

    @Value("${ref-faq}")
    private String refFaq;

    @Value("${ref-download-xlsx-template}")
    private String refDownloadXlsxTemplate;

    @Value("${ref-xlsx-fill-instruction}")
    private String refXlsxFillInstruction;

    @Value("${subdomain-form}")
    private String subdomainForm;

    @Value("${subdomain-work}")
    private String subdomainWork;

    @Value("${link-prefix}")
    private String linkPrefix;

    @Value("${link-suffix:}")
    private String linkSuffix;

    @Value("${ref-prescription-rospotrebnadzor-administration}")
    private String prescriptionOfRospotrepnadzorAdministration;

    @Value("${ref-working-portal}")
    private String workingPortal;

    @Value("${ref-agreement}")
    private String refAgreement;






}
