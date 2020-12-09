//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.27 at 05:20:15 PM IRKT 
//


package ru.sibdigital.addcovid.dto.egrip;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Сведения о причине внесения записи в реестр (ЕГРЮЛ или ЕГРИП) 
 * 
 * <p>Java class for ВидЗапТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ВидЗапТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="КодСПВЗ" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="5"/&gt;
 *             &lt;pattern value="[0-9]{5}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="НаимВидЗап" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="5"/&gt;
 *             &lt;maxLength value="500"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "\u0412\u0438\u0434\u0417\u0430\u043f\u0422\u0438\u043f")
public class ВидЗапТип {

    @XmlAttribute(name = "\u041a\u043e\u0434\u0421\u041f\u0412\u0417", required = true)
    protected String кодСПВЗ;
    @XmlAttribute(name = "\u041d\u0430\u0438\u043c\u0412\u0438\u0434\u0417\u0430\u043f", required = true)
    protected String наимВидЗап;

    /**
     * Gets the value of the кодСПВЗ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getКодСПВЗ() {
        return кодСПВЗ;
    }

    /**
     * Sets the value of the кодСПВЗ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setКодСПВЗ(String value) {
        this.кодСПВЗ = value;
    }

    /**
     * Gets the value of the наимВидЗап property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getНаимВидЗап() {
        return наимВидЗап;
    }

    /**
     * Sets the value of the наимВидЗап property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setНаимВидЗап(String value) {
        this.наимВидЗап = value;
    }

}
