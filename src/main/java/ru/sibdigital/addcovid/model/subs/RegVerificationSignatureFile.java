package ru.sibdigital.addcovid.model.subs;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_verification_signature_file", schema = "subs")
public class RegVerificationSignatureFile {
    //cov_prod_copy2.subs.reg_verification_signature_file_id_seq
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "reg_verification_signature_file_id_seq",
            sequenceName = "reg_verification_signature_file_id_seq", allocationSize = 1, schema = "subs"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reg_verification_signature_file_id_seq")
    private Long id;
    private Timestamp timeCreate;
    private Timestamp timeBeginVerification;
    private Timestamp timeEndVerification;
    private Integer verifyStatus;
    private String verifyResult;
    @ManyToOne
    @JoinColumn(name = "id_request", referencedColumnName = "id", nullable = false)
    private DocRequestSubsidy requestSubsidy;
    @ManyToOne
    @JoinColumn(name = "id_request_subsidy_file", referencedColumnName = "id", nullable = false)
    private TpRequestSubsidyFile requestSubsidyFile;
    @ManyToOne
    @JoinColumn(name = "id_request_subsidy_signature_file", referencedColumnName = "id", nullable = false)
    private TpRequestSubsidyFile requestSubsidySubsidySignatureFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "time_create")
    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Basic
    @Column(name = "time_begin_verification")
    public Timestamp getTimeBeginVerification() {
        return timeBeginVerification;
    }

    public void setTimeBeginVerification(Timestamp timeBeginVerification) {
        this.timeBeginVerification = timeBeginVerification;
    }

    @Basic
    @Column(name = "time_end_verification")
    public Timestamp getTimeEndVerification() {
        return timeEndVerification;
    }

    public void setTimeEndVerification(Timestamp timeEndVerification) {
        this.timeEndVerification = timeEndVerification;
    }

    @Basic
    @Column(name = "verify_status")
    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    @Basic
    @Column(name = "verify_result")
    public String getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(String verifyResult) {
        this.verifyResult = verifyResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegVerificationSignatureFile that = (RegVerificationSignatureFile) o;
        return id == that.id && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(timeBeginVerification, that.timeBeginVerification) && Objects.equals(timeEndVerification, that.timeEndVerification) && Objects.equals(verifyStatus, that.verifyStatus) && Objects.equals(verifyResult, that.verifyResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeCreate, timeBeginVerification, timeEndVerification, verifyStatus, verifyResult);
    }

    public DocRequestSubsidy getRequestSubsidy() {
        return requestSubsidy;
    }

    public void setRequestSubsidy(DocRequestSubsidy requestSubsidy) {
        this.requestSubsidy = requestSubsidy;
    }

    public TpRequestSubsidyFile getRequestSubsidyFile() {
        return requestSubsidyFile;
    }

    public void setRequestSubsidyFile(TpRequestSubsidyFile requestSubsidyFile) {
        this.requestSubsidyFile = requestSubsidyFile;
    }

    public TpRequestSubsidyFile getRequestSubsidySubsidySignatureFile() {
        return requestSubsidySubsidySignatureFile;
    }

    public void setRequestSubsidySubsidySignatureFile(TpRequestSubsidyFile requestSubsidySubsidySignatureFile) {
        this.requestSubsidySubsidySignatureFile = requestSubsidySubsidySignatureFile;
    }
}
