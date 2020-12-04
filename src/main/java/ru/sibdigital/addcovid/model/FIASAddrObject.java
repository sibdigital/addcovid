package ru.sibdigital.addcovid.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "addr_object", schema = "fias")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FIASAddrObject {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "ADDR_OBJECT_SEQ_GEN", sequenceName = "addr_object_id_seq", allocationSize = 1, schema = "fias")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDR_OBJECT_SEQ_GEN")
    private Long id;
    private Long objectId;
    private String objectGuid;
    private Long changeId;
    private String name;
    private String typename;
    private String level;
    private Long opertypeId;
    private Long prevId;
    private Long nextId;
    private Date updateDate;
    private Date startDate;
    private Date endDate;
    private Integer isActual;
    private Integer isActive;
    private Date createDate;
    private Long levelId;

    public Long getId() {
        return id;
    }

    public Long getObjectId() {
        return objectId;
    }

    public String getObjectGuid() {
        return objectGuid;
    }

    public Long getChangeId() {
        return changeId;
    }

    public String getName() {
        return name;
    }

    public String getTypename() {
        return typename;
    }

    public String getLevel() {
        return level;
    }

    public Long getOpertypeId() {
        return opertypeId;
    }

    public Long getPrevId() {
        return prevId;
    }

    public Long getNextId() {
        return nextId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getIsActual() {
        return isActual;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public void setObjectGuid(String objectGuid) {
        this.objectGuid = objectGuid;
    }

    public void setChangeId(Long changeId) {
        this.changeId = changeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setOpertypeId(Long opertypeId) {
        this.opertypeId = opertypeId;
    }

    public void setPrevId(Long prevId) {
        this.prevId = prevId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setIsActual(Integer isActual) {
        this.isActual = isActual;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }
}

