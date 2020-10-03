package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_actualization_history", schema = "public")
public class RegActualizationHistory {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_ACTUAL_HIST_SEQ_GEN", sequenceName = "reg_actualization_history_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_ACTUAL_HIST_SEQ_GEN")
    private Integer id;
    private Timestamp timeActualization;
    private String inn;

    @OneToOne
    @JoinColumn(name="id_request", nullable=false)
    @JsonIgnore
    private DocRequest docRequest;

    @ManyToOne
    @JoinColumn(name="id_actualized_request", nullable=false)
    @JsonIgnore
    private DocRequest actualizedDocRequest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "time_actualization", nullable = false)
    public Timestamp getTimeActualization() {
        return timeActualization;
    }

    public void setTimeActualization(Timestamp timeActualization) {
        this.timeActualization = timeActualization;
    }

    @Basic
    @Column(name = "inn", nullable = true, length = 12)
    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public DocRequest getDocRequest() {
        return docRequest;
    }

    public void setDocRequest(DocRequest docRequest) {
        this.docRequest = docRequest;
    }

    public DocRequest getActualizedDocRequest() {
        return actualizedDocRequest;
    }

    public void setActualizedDocRequest(DocRequest actualizedDocRequest) {
        this.actualizedDocRequest = actualizedDocRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegActualizationHistory that = (RegActualizationHistory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(timeActualization, that.timeActualization) &&
                Objects.equals(inn, that.inn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeActualization, inn);
    }
}
