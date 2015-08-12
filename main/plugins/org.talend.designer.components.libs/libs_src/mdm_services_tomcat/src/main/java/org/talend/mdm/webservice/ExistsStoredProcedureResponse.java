
package org.talend.mdm.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for existsStoredProcedureResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="existsStoredProcedureResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSBoolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "existsStoredProcedureResponse", propOrder = {
    "_return"
})
public class ExistsStoredProcedureResponse {

    @XmlElement(name = "return")
    protected WSBoolean _return;

    /**
     * Default no-arg constructor
     * 
     */
    public ExistsStoredProcedureResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public ExistsStoredProcedureResponse(final WSBoolean _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSBoolean }
     *     
     */
    public WSBoolean getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSBoolean }
     *     
     */
    public void setReturn(WSBoolean value) {
        this._return = value;
    }

}
