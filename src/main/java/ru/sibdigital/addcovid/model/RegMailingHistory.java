package ru.sibdigital.addcovid.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_mailing_history", schema = "public")
public class RegMailingHistory {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "REG_MAILING_HISTORY_SEQ_GEN", sequenceName = "reg_mailing_history_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_MAILING_HISTORY_SEQ_GEN")
    private Long id;
    private Timestamp timeSend;
    private Short status;
    private String email;

    @OneToOne
    @JoinColumn(name = "id_principal", referencedColumnName = "id")
    private ClsPrincipal clsPrincipal;


    @OneToOne
    @JoinColumn(name = "id_mailing_list", referencedColumnName = "id")
    private ClsMailingList clsMailingList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "time_send")
    public Timestamp getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Timestamp timeSend) {
        this.timeSend = timeSend;
    }

    @Basic
    @Column(name = "status")
    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public ClsPrincipal getClsPrincipal() {
        return clsPrincipal;
    }

    public void setClsPrincipal(ClsPrincipal clsPrincipal) {
        this.clsPrincipal = clsPrincipal;
    }

    public ClsMailingList getClsMailingList() {
        return clsMailingList;
    }

    public void setClsMailingList(ClsMailingList clsMailingList) {
        this.clsMailingList = clsMailingList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegMailingHistory that = (RegMailingHistory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(timeSend, that.timeSend) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeSend, status);
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
