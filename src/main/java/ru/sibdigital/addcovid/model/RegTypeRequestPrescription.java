package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reg_type_request_prescription", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegTypeRequestPrescription {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_TYPE_REQUEST_PRESCRIPTION_SEQ_GEN", sequenceName = "reg_type_request_prescription_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_TYPE_REQUEST_PRESCRIPTION_SEQ_GEN")
    private Long id;
    private Short num;
    private String content;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_type_request", nullable = false)
    private ClsTypeRequest typeRequest;

    @OneToMany(targetEntity = RegTypeRequestPrescriptionFile.class, mappedBy="typeRequestPrescription", fetch = FetchType.LAZY)
    private List<RegTypeRequestPrescriptionFile> regTypeRequestPrescriptionFiles;

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

    public ClsTypeRequest getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(ClsTypeRequest typeRequest) {
        this.typeRequest = typeRequest;
    }

    public List<RegTypeRequestPrescriptionFile> getRegTypeRequestPrescriptionFiles() {
        return regTypeRequestPrescriptionFiles;
    }

    public void setRegTypeRequestPrescriptionFiles(List<RegTypeRequestPrescriptionFile> regTypeRequestPrescriptionFiles) {
        this.regTypeRequestPrescriptionFiles = regTypeRequestPrescriptionFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegTypeRequestPrescription that = (RegTypeRequestPrescription) o;
        return Objects.equals(id, that.id) && Objects.equals(num, that.num) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num, content);
    }
}
