package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_doc_request_file", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegDocRequestFile {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_DOC_REQ_FILE_SEQ_GEN", sequenceName = "reg_doc_request_file_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_DOC_REQ_FILE_SEQ_GEN")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_request", nullable = false)
    private DocRequest request;

    @ManyToOne
    @JoinColumn(name = "id_organization_file", nullable = false)
    private RegOrganizationFile organizationFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocRequest getRequest() {
        return request;
    }

    public void setRequest(DocRequest request) {
        this.request = request;
    }

    public RegOrganizationFile getOrganizationFile() {
        return organizationFile;
    }

    public void setOrganizationFile(RegOrganizationFile organizationFile) {
        this.organizationFile = organizationFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegDocRequestFile that = (RegDocRequestFile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
