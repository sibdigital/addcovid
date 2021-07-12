package ru.sibdigital.addcovid.model.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cls_subsidy_request_status", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsSubsidyRequestStatus {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "cls_subsidy_request_status_id_seq",
            sequenceName = "cls_subsidy_request_status_id_seq", allocationSize = 1, schema = "subs"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cls_subsidy_request_status_id_seq")
    private Long id;
    private Boolean isDeleted;
    private Timestamp timeCreate;
    private String name;
    private String shortName;
    private String code;
    private Boolean isBlockRequest;
    @ManyToOne
    @JoinColumn(name = "id_subsidy", referencedColumnName = "id")
    private ClsSubsidy subsidy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "is_deleted")
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "short_name")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "is_block_request")
    public Boolean getBlockRequest() {
        return isBlockRequest;
    }

    public void setBlockRequest(Boolean blockRequest) {
        isBlockRequest = blockRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsSubsidyRequestStatus that = (ClsSubsidyRequestStatus) o;
        return id == that.id && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(name, that.name) && Objects.equals(shortName, that.shortName) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, timeCreate, name, shortName, code);
    }

    public ClsSubsidy getSubsidy() {
        return subsidy;
    }

    public void setSubsidy(ClsSubsidy subsidy) {
        this.subsidy = subsidy;
    }
}
