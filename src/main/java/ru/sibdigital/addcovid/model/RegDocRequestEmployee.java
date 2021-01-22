package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_doc_request_employee", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegDocRequestEmployee {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_DOC_REQ_EMPLOYEE_SEQ_GEN", sequenceName = "reg_doc_request_employee_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_DOC_REQ_EMPLOYEE_SEQ_GEN")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_request", nullable = false)
    private DocRequest request;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private DocEmployee docEmployee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DocRequest getRequest() {
        return request;
    }

    public void setRequest(DocRequest request) {
        this.request = request;
    }

    public DocEmployee getDocEmployee() {
        return docEmployee;
    }

    public void setDocEmployee(DocEmployee docEmployee) {
        this.docEmployee = docEmployee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegDocRequestEmployee that = (RegDocRequestEmployee) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
