package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cls_department_contact", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsDepartmentContact {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_DEP_CONTACT_GEN", sequenceName = "cls_department_contact_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_DEP_CONTACT_GEN")
    private Long id;
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}


    @OneToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id")
    private ClsDepartment department;
    public ClsDepartment getDepartment() {return department;}
    public void setDepartment(ClsDepartment department) {this.department = department;}

    @Basic
    @Column(name = "type", nullable = true)
    private Integer type;

    @Basic
    @Column(name = "contact_value")
    private String contactValue;

    @Basic
    @Column(name = "description")
    private String description;

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getContactValue() { return contactValue; }
    public void setContactValue(String contactValue) { this.contactValue = contactValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsDepartmentContact departmentContact = (ClsDepartmentContact) o;
        return Objects.equals(id, departmentContact.id) &&
                Objects.equals(department, departmentContact.department) &&
                Objects.equals(type, departmentContact.type) &&
                Objects.equals(contactValue, departmentContact.contactValue) &&
                Objects.equals(description, departmentContact.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, department, type, contactValue, description);
    }
}
