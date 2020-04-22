package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cls_department", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsDepartment {


    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;
    private Integer statusImport;
    private Timestamp timeImport;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "status_import", nullable = false)
    public Integer getStatusImport() {
        return statusImport;
    }

    public void setStatusImport(Integer statusImport) {
        this.statusImport = statusImport;
    }

    @Basic
    @Column(name = "time_import", nullable = true)
    public Timestamp getTimeImport() {
        return timeImport;
    }

    public void setTimeImport(Timestamp timeImport) {
        this.timeImport = timeImport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsDepartment that = (ClsDepartment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(statusImport, that.statusImport) &&
                Objects.equals(timeImport, that.timeImport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, statusImport, timeImport);
    }

    public String getValue() {
        try {
            String descr = this.getDescription().length() > 91 ? (this.getDescription().substring(0, 91) + "...") : this.getDescription();
            return (this.getName() + " (" + descr + ")");
        }catch (Exception ex)
        {

        }
        return this.getName();
    }

}
