package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cls_excel", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class ClsExcel {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_EXCEL_GEN", sequenceName = "cls_excel_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_EXCEL_GEN")
    private Long id;
    private String name;
    private Integer status;
    private String description;
    private Timestamp timeUpload;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "status", nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
    @Column(name = "time_upload", nullable = false)
    public Timestamp getTimeUpload() {
        return timeUpload;
    }

    public void setTimeUpload(Timestamp timeUpload) {
        this.timeUpload = timeUpload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsExcel clsExcel = (ClsExcel) o;
        return Objects.equals(id, clsExcel.id) &&
                Objects.equals(name, clsExcel.name) &&
                Objects.equals(status, clsExcel.status) &&
                Objects.equals(description, clsExcel.description) &&
                Objects.equals(timeUpload, clsExcel.timeUpload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description, timeUpload);
    }
}
