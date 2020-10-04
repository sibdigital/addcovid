package ru.sibdigital.addcovid.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@Entity
@Table(name = "cls_settings", schema = "public")
@TypeDefs({
        @TypeDef(name = "JsonbType", typeClass = Jsonb.class)
})
public class ClsSettings {

    public static String MESSAGES_KEY = "messages";

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_SETTINGS", sequenceName = "cls_settings_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_SETTINGS")
    private Integer id;

    @Basic
    @Column(name = "status")
    private Integer status;

    @Basic
    @Column(name = "value", nullable = true, columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private String value;

    @Basic
    @Column(name = "key", nullable = true)
    private String key;

    @Basic
    @Column(name = "string_value", nullable = true)
    private String stringValue;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
