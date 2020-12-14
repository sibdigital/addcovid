package ru.sibdigital.addcovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_mailing_list_follower", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegMailingListFollower {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_MAIL_FOLLOWER_GEN", sequenceName = "reg_mailing_list_follower_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_MAIL_FOLLOWER_GEN")
    private Long id;

    private Timestamp activationDate;
    private Timestamp deactivationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "id_principal", referencedColumnName = "id")
    private ClsPrincipal principal;

    @JsonIgnore
    public ClsPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(ClsPrincipal principal) {
        this.principal = principal;
    }

    @OneToOne
    @JoinColumn(name = "id_mailing_list", referencedColumnName = "id")
    private ClsMailingList mailingList;

    @JsonIgnore
    public ClsMailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(ClsMailingList mailingList) {
        this.mailingList = mailingList;
    }

    @Basic
    @Column(name = "activation_date")
    public Timestamp getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    @Basic
    @Column(name = "deactivation_date")
    public Timestamp getDectivationDate() {
        return deactivationDate;
    }

    public void setDectivationDate(Timestamp deactivationDate) {
        this.deactivationDate = deactivationDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegMailingListFollower that = (RegMailingListFollower) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(principal, that.principal) &&
                Objects.equals(mailingList, that.mailingList) &&
                Objects.equals(activationDate, that.activationDate) &&
                Objects.equals(deactivationDate, that.deactivationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, principal, mailingList, activationDate, deactivationDate);
    }

}