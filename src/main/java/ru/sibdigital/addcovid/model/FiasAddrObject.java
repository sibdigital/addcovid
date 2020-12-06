package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "addr_object", schema = "fias")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FiasAddrObject {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "ADDR_OBJ_SEQ_GEN", sequenceName = "addr_object_id_seq", allocationSize = 1, schema = "fias")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDR_OBJ_SEQ_GEN")
    private Long id;
    private Long objectId;
    private String objectGuid;
    private Long changeId;
    private String name;
    private String typeName;
    private String level;
    private Long operTypeId;
    private Long prevId;
    private Long nextId;
    private Timestamp updateDate;
    private Timestamp startDate;
    private Timestamp endDate;
    private short isActual;
    private short isActive;
    private Timestamp createDate;
    private Long levelId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "objectid")
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Basic
    @Column(name = "objectguid")
    public String getObjectGuid() {
        return objectGuid;
    }

    public void setObjectGuid(String objectGuid) {
        this.objectGuid = objectGuid;
    }

    @Basic
    @Column(name = "changeid")
    public Long getChangeId() {
        return changeId;
    }

    public void setChangeId(Long changeId) {
        this.changeId = changeId;
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
    @Column(name = "typename")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Basic
    @Column(name = "level")
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Basic
    @Column(name = "opertypeid")
    public Long getOperTypeId() {
        return operTypeId;
    }

    public void setOperTypeId(Long operTypeId) {
        this.operTypeId = operTypeId;
    }

    @Basic
    @Column(name = "previd")
    public Long getPrevId() {
        return prevId;
    }

    public void setPrevId(Long prevId) {
        this.prevId = prevId;
    }

    @Basic
    @Column(name = "nextid")
    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    @Basic
    @Column(name = "updatedate")
    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    @Basic
    @Column(name = "startdate")
    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "enddate")
    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Column(name = "createdate")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "isactual")
    public short getIsActual() {
        return isActual;
    }

    public void setIsActual(short isActual) {
        this.isActual = isActual;
    }

    @Basic
    @Column(name = "isactive")
    public short getIsActive() {
        return isActive;
    }

    public void setIsActive(short isActive) {
        this.isActive = isActive;
    }

    @Basic
    @Column(name = "levelid")
    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiasAddrObject that = (FiasAddrObject) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(objectGuid, that.objectGuid) &&
                Objects.equals(objectId, that.objectId) &&
                Objects.equals(changeId, that.changeId) &&
                Objects.equals(level, that.level) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(name, that.name);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, objectGuid, objectId, changeId, level, createDate, name);
    }

}
