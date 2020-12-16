package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_doc_request_prescription", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class RegDocRequestPrescription {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_DOC_REQ_PRESCRIPTION_SEQ_GEN", sequenceName = "reg_doc_request_prescription_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_DOC_REQ_PRESCRIPTION_SEQ_GEN")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_request", nullable = false)
    private DocRequest request;

    @ManyToOne
    @JoinColumn(name = "id_prescription", nullable = false)
    private ClsPrescription prescription;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private AdditionalAttributes additionalAttributes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClsPrescription getPrescription() {
        return prescription;
    }

    public void setPrescription(ClsPrescription prescription) {
        this.prescription = prescription;
    }

    public AdditionalAttributes getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(AdditionalAttributes additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegDocRequestPrescription that = (RegDocRequestPrescription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
