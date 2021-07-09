package ru.sibdigital.addcovid.model.subs;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.sibdigital.addcovid.model.ClsDepartment;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cls_subsidy", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ClsSubsidy {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "cls_subsidy_id_seq", sequenceName = "cls_subsidy_id_seq", allocationSize = 1,
            schema = "subs"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cls_subsidy_id_seq")
    private Long id;
    private String name;
    private String shortName;
    private Boolean isDeleted;
    private String settings;
    private int statusRegistration;
    private Integer statusVisible;
    private Timestamp beginRegistration;
    private Timestamp endRegistration;
    private Timestamp beginVisible;
    private Timestamp endVisible;
    private Integer sortWeight;
    //private Object additionalFields;
    private Integer statusPublication;
    private Timestamp timePublication;
    private Timestamp timeCreate;
    private Integer calendarDayToResolution;
    private Integer workDayToResolution;
    @ManyToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id")
    private ClsDepartment department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "is_deleted")
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Basic
    @Column(name = "settings")
    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Basic
    @Column(name = "status_registration")
    public int getStatusRegistration() {
        return statusRegistration;
    }

    public void setStatusRegistration(int statusRegistration) {
        this.statusRegistration = statusRegistration;
    }

    @Basic
    @Column(name = "status_visible")
    public Integer getStatusVisible() {
        return statusVisible;
    }

    public void setStatusVisible(Integer statusVisible) {
        this.statusVisible = statusVisible;
    }

    @Basic
    @Column(name = "begin_registration")
    public Timestamp getBeginRegistration() {
        return beginRegistration;
    }

    public void setBeginRegistration(Timestamp beginRegistration) {
        this.beginRegistration = beginRegistration;
    }

    @Basic
    @Column(name = "end_registration")
    public Timestamp getEndRegistration() {
        return endRegistration;
    }

    public void setEndRegistration(Timestamp endRegistration) {
        this.endRegistration = endRegistration;
    }

    @Basic
    @Column(name = "begin_visible")
    public Timestamp getBeginVisible() {
        return beginVisible;
    }

    public void setBeginVisible(Timestamp beginVisible) {
        this.beginVisible = beginVisible;
    }

    @Basic
    @Column(name = "end_visible")
    public Timestamp getEndVisible() {
        return endVisible;
    }

    public void setEndVisible(Timestamp endVisible) {
        this.endVisible = endVisible;
    }

    @Basic
    @Column(name = "sort_weight")
    public Integer getSortWeight() {
        return sortWeight;
    }

    public void setSortWeight(Integer sortWeight) {
        this.sortWeight = sortWeight;
    }

//    @Basic
//    @Column(name = "additional_fields")
//    public Object getAdditionalFields() {
//        return additionalFields;
//    }
//
//    public void setAdditionalFields(Object additionalFields) {
//        this.additionalFields = additionalFields;
//    }

    @Basic
    @Column(name = "status_publication")
    public Integer getStatusPublication() {
        return statusPublication;
    }

    public void setStatusPublication(Integer statusPublication) {
        this.statusPublication = statusPublication;
    }

    @Basic
    @Column(name = "time_publication")
    public Timestamp getTimePublication() {
        return timePublication;
    }

    public void setTimePublication(Timestamp timePublication) {
        this.timePublication = timePublication;
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
    @Column(name = "calendar_day_to_resolution")
    public Integer getCalendarDayToResolution() {
        return calendarDayToResolution;
    }

    public void setCalendarDayToResolution(Integer calendarDayToResolution) {
        this.calendarDayToResolution = calendarDayToResolution;
    }

    @Basic
    @Column(name = "work_day_to_resolution")
    public Integer getWorkDayToResolution() {
        return workDayToResolution;
    }

    public void setWorkDayToResolution(Integer workDayToResolution) {
        this.workDayToResolution = workDayToResolution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsSubsidy that = (ClsSubsidy) o;
        return id == that.id && statusRegistration == that.statusRegistration && Objects.equals(name, that.name) && Objects.equals(shortName, that.shortName) && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(settings, that.settings) && Objects.equals(statusVisible, that.statusVisible) && Objects.equals(beginRegistration, that.beginRegistration) && Objects.equals(endRegistration, that.endRegistration) && Objects.equals(beginVisible, that.beginVisible) && Objects.equals(endVisible, that.endVisible) && Objects.equals(sortWeight, that.sortWeight) && Objects.equals(statusPublication, that.statusPublication) && Objects.equals(timePublication, that.timePublication) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(calendarDayToResolution, that.calendarDayToResolution) && Objects.equals(workDayToResolution, that.workDayToResolution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, shortName, isDeleted, settings, statusRegistration, statusVisible,
                beginRegistration, endRegistration, beginVisible, endVisible, sortWeight, statusPublication,
                timePublication, timeCreate, calendarDayToResolution, workDayToResolution);
    }

    public ClsDepartment getDepartment() {
        return department;
    }

    public void setDepartment(ClsDepartment department) {
        this.department = department;
    }
}
