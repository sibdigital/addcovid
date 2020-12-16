package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reg_prescription_text", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegPrescriptionText {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_PRESCRIPTION_TEXT_SEQ_GEN", sequenceName = "reg_prescription_text_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_PRESCRIPTION_TEXT_SEQ_GEN")
    private Long id;
    private Short num;
    private String content;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_prescription", nullable = false)
    private ClsPrescription prescription;

    @Where(clause = "not is_deleted")
    @OrderBy("id asc")
    @OneToMany(mappedBy="prescriptionText")
    private List<RegPrescriptionTextFile> prescriptionTextFiles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "num", nullable = true, length = -1)
    public Short getNum() {
        return num;
    }

    public void setNum(Short num) {
        this.num = num;
    }

    @Basic
    @Column(name = "content", nullable = true, length = -1)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ClsPrescription getPrescription() {
        return prescription;
    }

    public void setPrescription(ClsPrescription prescription) {
        this.prescription = prescription;
    }

    public List<RegPrescriptionTextFile> getPrescriptionTextFiles() {
        return prescriptionTextFiles;
    }

    public void setPrescriptionTextFiles(List<RegPrescriptionTextFile> prescriptionTextFiles) {
        this.prescriptionTextFiles = prescriptionTextFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegPrescriptionText that = (RegPrescriptionText) o;
        return Objects.equals(id, that.id) && Objects.equals(num, that.num) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num, content);
    }
}
