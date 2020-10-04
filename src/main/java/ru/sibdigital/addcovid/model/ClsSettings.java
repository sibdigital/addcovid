package ru.sibdigital.addcovid.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cls_settings", schema = "public")
@TypeDefs({
        @TypeDef(name = "JsonbType", typeClass = Jsonb.class)
})
public class ClsSettings {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_SETTINGS", sequenceName = "cls_settings_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_SETTINGS")
    private Integer id;

    @Basic
    @Column(name = "status")
    private Integer status;

    @Basic
    @Column(name = "messages", nullable = true, columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private String messages;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
