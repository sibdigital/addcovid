package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cls_action_type", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsActionType {

    public static final String AUTH_CODE = "AUTH";

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "cls_action_type_id_seq", sequenceName = "cls_action_type_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cls_action_type_id_seq")
    private Long id;
    private Boolean isDeleted;
    private Timestamp timeCreate;
    private String name;
    private String shortName;
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "is_deleted", nullable = true)
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Basic
    @Column(name = "time_create", nullable = false)
    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
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
    @Column(name = "short_name", nullable = true, length = -1)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Basic
    @Column(name = "code", nullable = true, length = 15)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsActionType that = (ClsActionType) o;
        return (long)id == that.id && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(name, that.name) && Objects.equals(shortName, that.shortName) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, timeCreate, name, shortName, code);
    }
}
