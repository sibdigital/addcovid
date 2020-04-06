package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "doc_address_fact", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocAddressFact {


    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DOC_ADDR_SEQ_GEN", sequenceName = "doc_address_fact_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_ADDR_SEQ_GEN")
    private Long id;
    private String addressFact;
    private Long personOfficeFactCnt;

    @ManyToOne
    @JoinColumn(name="id_request", nullable=false)
    private DocRequest docRequestAddressFact;

//    @SequenceGenerator(name = "ADDRESS_SEQ", sequenceName = "doc_address_fact_id_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "address_fact", nullable = false, length = 255)
    public String getAddressFact() {
        return addressFact;
    }

    public void setAddressFact(String addressFact) {
        this.addressFact = addressFact;
    }

    @Basic
    @Column(name = "person_office_fact_cnt", nullable = false)
    public Long getPersonOfficeFactCnt() {
        return personOfficeFactCnt;
    }

    public void setPersonOfficeFactCnt(Long personOfficeFactCnt) {
        this.personOfficeFactCnt = personOfficeFactCnt;
    }


    public DocRequest getDocRequest() {
        return docRequestAddressFact;
    }

    public void setDocRequest(DocRequest docRequest) {
        this.docRequestAddressFact = docRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocAddressFact that = (DocAddressFact) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(addressFact, that.addressFact) &&
                Objects.equals(personOfficeFactCnt, that.personOfficeFactCnt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressFact, personOfficeFactCnt);
    }


}
