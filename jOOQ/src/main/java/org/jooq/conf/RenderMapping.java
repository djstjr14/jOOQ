package org.jooq.conf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;



/* Refactoring
 * - Self Encapsulate Field
 * - Added methods that checks empty variable for better understandability
 * */

/**
 * The runtime schema and table mapping.
 *
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RenderMapping", propOrder = {

})
@SuppressWarnings({
    "all"
})
public class RenderMapping
    extends SettingsBase
    implements Serializable, Cloneable
{

    private final static long serialVersionUID = 31100L;
    protected String defaultSchema;
    @XmlElementWrapper(name = "schemata")
    @XmlElement(name = "schema")
    protected List<MappedSchema> schemata;

    /**
     * The default schema as defined in {@link org.jooq.Schema#getName()}.
     * <p>
     * This schema will be omitted in rendered SQL.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDefaultSchema() {
        return defaultSchema;
    }

    /**
     * Sets the value of the defaultSchema property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDefaultSchema(String value) {
        this.defaultSchema = value;
    }

    public List<MappedSchema> getSchemata() {
        if (schemata == null) {
            schemata = new ArrayList<MappedSchema>();
        }
        return schemata;
    }

    public void setSchemata(List<MappedSchema> schemata) {
        this.schemata = schemata;
    }

    public RenderMapping withDefaultSchema(String value) {
        setDefaultSchema(value);
        return this;
    }

    public RenderMapping withSchemata(MappedSchema... values) {
        if (values!= null) {
            for (MappedSchema value: values) {
                getSchemata().add(value);
            }
        }
        return this;
    }

    public RenderMapping withSchemata(Collection<MappedSchema> values) {
        if (values!= null) {
            getSchemata().addAll(values);
        }
        return this;
    }

    public RenderMapping withSchemata(List<MappedSchema> schemata) {
        setSchemata(schemata);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (defaultSchema!= null) {
            sb.append("<defaultSchema>");
            sb.append(defaultSchema);
            sb.append("</defaultSchema>");
        }
        if (schemata!= null) {
            sb.append("<schemata>");
            sb.append(schemata);
            sb.append("</schemata>");
        }
        return sb.toString();
    }
    
    public boolean isEmptySchema(RenderMapping value) {
    	return value.getSchemata()==null;
    }
    public boolean isEmptyDefaultSchema(RenderMapping value) {
    	return value.getDefaultSchema()==null;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass()!= that.getClass()) {
            return false;
        }
        RenderMapping other = ((RenderMapping) that);
        if (isEmptyDefaultSchema(this)) {
            if (!isEmptyDefaultSchema(other)) {
                return false;
            }
        } else {
            if (!defaultSchema.equals(other.defaultSchema)) {
                return false;
            }
        }
        if (isEmptySchema(this)) {
            if (!isEmptySchema(other)) {
                return false;
            }
        } else {
            if (!schemata.equals(other.schemata)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((this.getDefaultSchema() == null)? 0 :this.getDefaultSchema().hashCode()));
        result = ((prime*result)+((this.getSchemata() == null)? 0 :this.getSchemata().hashCode()));
        return result;
    }

}
