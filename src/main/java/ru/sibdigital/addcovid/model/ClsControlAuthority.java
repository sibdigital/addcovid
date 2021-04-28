package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "cls_control_authority", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsControlAuthority {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_AUTHORITY_GEN", sequenceName = "cls_control_authority_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_AUTHORITY_GEN")
    private Long id;
    private Long idParent;
    private String name;
    private String shortName;
    private Boolean isDeleted;
    private Integer weight;

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public void setName(String name) { this.name = name; }

    @Basic
    @Column(name = "name", nullable = false)
    public String getName() { return name; }

    public void setIdParent(Long parentId) { this.idParent = idParent; }

    @Basic
    @Column(name = "id_parent", nullable = true)
    public Long getIdParent() { return idParent; }

    public void setShortName(String shortName) { this.shortName = shortName; }

    @Basic
    @Column(name = "short_name")
    public String getShortName() { return shortName; }


    @Basic
    @Column(name = "is_deleted")
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Basic
    @Column(name = "weight")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
