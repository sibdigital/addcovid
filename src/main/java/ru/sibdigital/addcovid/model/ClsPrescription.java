package ru.sibdigital.addcovid.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cls_prescription", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ClsPrescription {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_PRESCRIPTION", sequenceName = "cls_prescription_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_PRESCRIPTION")
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private Timestamp timePublication;

    @OneToOne
    @JoinColumn(name = "id_type_request", referencedColumnName = "id")
    private ClsTypeRequest typeRequest;

    @OrderBy("num asc")
    @OneToMany(targetEntity = RegPrescriptionText.class, mappedBy = "prescription", fetch = FetchType.LAZY)
    private List<RegPrescriptionText> prescriptionTexts;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private AdditionalFields additionalFields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "status", nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName(){
        String result  = "";
        if (this.status == PrescriptionStatuses.NOT_PUBLISHED.getValue()) {
            result = "Новое";
        } else if (this.status == PrescriptionStatuses.PUBLISHED.getValue()) {
            result = "Опубликовано";
        }
        return result;
    }

    @Basic
    @Column(name = "time_publication", nullable = true)
    public Timestamp getTimePublication() {
        return timePublication;
    }

    public void setTimePublication(Timestamp timePublication) {
        this.timePublication = timePublication;
    }

    public ClsTypeRequest getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(ClsTypeRequest typeRequest) {
        this.typeRequest = typeRequest;
    }

    public List<RegPrescriptionText> getPrescriptionTexts() {
        return prescriptionTexts;
    }

    public void setPrescriptionTexts(List<RegPrescriptionText> prescriptionTexts) {
        this.prescriptionTexts = prescriptionTexts;
    }

    public AdditionalFields getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(AdditionalFields additionalFields) {
        this.additionalFields = additionalFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsPrescription that = (ClsPrescription) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(timePublication, that.timePublication);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, timePublication);
    }
}
