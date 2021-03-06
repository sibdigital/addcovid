//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.27 at 05:01:36 PM IRKT 
//


package ru.sibdigital.addcovid.dto.egrul;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Сведения о решении суда
 * 
 * <p>Java class for РешСудТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="РешСудТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="НаимСуда" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="1000"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Номер" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="255"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Дата" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}date"&gt;
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
@XmlType(name = "\u0420\u0435\u0448\u0421\u0443\u0434\u0422\u0438\u043f")
public class РешСудТип {

    @XmlAttribute(name = "\u041d\u0430\u0438\u043c\u0421\u0443\u0434\u0430", required = true)
    protected String наимСуда;
    @XmlAttribute(name = "\u041d\u043e\u043c\u0435\u0440", required = true)
    protected String номер;
    @XmlAttribute(name = "\u0414\u0430\u0442\u0430", required = true)
    protected XMLGregorianCalendar дата;

    /**
     * Gets the value of the наимСуда property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getНаимСуда() {
        return наимСуда;
    }

    /**
     * Sets the value of the наимСуда property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setНаимСуда(String value) {
        this.наимСуда = value;
    }

    /**
     * Gets the value of the номер property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getНомер() {
        return номер;
    }

    /**
     * Sets the value of the номер property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setНомер(String value) {
        this.номер = value;
    }

    /**
     * Gets the value of the дата property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getДата() {
        return дата;
    }

    /**
     * Sets the value of the дата property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setДата(XMLGregorianCalendar value) {
        this.дата = value;
    }

}
