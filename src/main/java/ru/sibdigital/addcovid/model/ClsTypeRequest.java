package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cls_type_request", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsTypeRequest {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String activityKind;
    private String prescription;
    private String prescriptionLink;
    private String settings;
    private int statusRegistration;
    private int statusVisible;

    @OneToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id")
    private ClsDepartment department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "activity_kind")
    public String getActivityKind() {
        return activityKind;
    }

    public void setActivityKind(String activityKind) {
        this.activityKind = activityKind;
    }

    @Basic
    @Column(name = "prescription")
    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    @Basic
    @Column(name = "prescription_link")
    public String getPrescriptionLink() {
        return prescriptionLink;
    }

    public void setPrescriptionLink(String prescriptionLink) {
        this.prescriptionLink = prescriptionLink;
    }

    @Basic
    @Column(name = "settings")
    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Basic
    @Column(name = "status_registration")
    public int getStatusRegistration() {
        return statusRegistration;
    }

    public void setStatusRegistration(int statusRegistration) {
        this.statusRegistration = statusRegistration;
    }

    @Basic
    @Column(name = "status_visible")
    public int getStatusVisible() {
        return statusVisible;
    }

    public void setStatusVisible(int statusVisible) {
        this.statusVisible = statusVisible;
    }

    public ClsDepartment getDepartment() {
        return department;
    }

    public void setDepartment(ClsDepartment department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsTypeRequest that = (ClsTypeRequest) o;
        return id == that.id &&
                statusRegistration == that.statusRegistration &&
                Objects.equals(activityKind, that.activityKind) &&
                Objects.equals(prescription, that.prescription) &&
                Objects.equals(prescriptionLink, that.prescriptionLink) &&
                Objects.equals(settings, that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activityKind, prescription, prescriptionLink, settings, statusRegistration);
    }
}
