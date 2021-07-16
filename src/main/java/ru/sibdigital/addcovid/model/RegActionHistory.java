package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_action_history", schema = "hist")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegActionHistory {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "reg_action_history_id_seq", sequenceName = "reg_action_history_id_seq", allocationSize = 1, schema = "hist")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reg_action_history_id_seq")
    private Long id;
    @Basic
    @Column(name = "time_action", insertable=false)
    private Timestamp timeAction;
    @ManyToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id")
    private ClsOrganization organization;
    @ManyToOne
    @JoinColumn(name = "id_principal", referencedColumnName = "id")
    private ClsPrincipal principal;
    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private ClsUser user;
    @ManyToOne
    @JoinColumn(name = "id_action_type", referencedColumnName = "id")
    private ClsActionType actionType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getTimeAction() {
        return timeAction;
    }

    public void setTimeAction(Timestamp timeAction) {
        this.timeAction = timeAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegActionHistory that = (RegActionHistory) o;
        return (long)id == that.id && Objects.equals(timeAction, that.timeAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeAction);
    }

    public ClsOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }

    public ClsPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(ClsPrincipal principal) {
        this.principal = principal;
    }

    public ClsUser getUser() {
        return user;
    }

    public void setUser(ClsUser user) {
        this.user = user;
    }

    public ClsActionType getActionType() {
        return actionType;
    }

    public void setActionType(ClsActionType actionType) {
        this.actionType = actionType;
    }
}
