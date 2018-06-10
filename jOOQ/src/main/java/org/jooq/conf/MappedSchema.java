package org.jooq.conf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/*
 * - Added methods that checks empty variable for better understandability
 * - Extract Superclass : Move similar method and field with MappedTableclass to superclass
 * - Self Encapsulate Field
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MappedSchema", propOrder = {

})
@SuppressWarnings({
    "all"
})
public class MappedSchema
    extends MappedBase
    implements Serializable, Cloneable
{
    @XmlElementWrapper(name = "tables")
    @XmlElement(name = "table")
    protected List<MappedTable> tables;

    public List<MappedTable> getTables() {
        if (tables == null) {
            tables = new ArrayList<MappedTable>();
        }
        return tables;
    }

    public void setTables(List<MappedTable> tables) {
        this.tables = tables;
    }

    public MappedSchema withInput(String value) {
        setInput(value);
        return this;
    }

    public MappedSchema withInputExpression(Pattern value) {
        setInputExpression(value);
        return this;
    }

    public MappedSchema withOutput(String value) {
        setOutput(value);
        return this;
    }

    public MappedSchema withTables(MappedTable... values) {
        if (values!= null) {
            for (MappedTable value: values) {
                getTables().add(value);
            }
        }
        return this;
    }

    public MappedSchema withTables(Collection<MappedTable> values) {
        if (values!= null) {
            getTables().addAll(values);
        }
        return this;
    }

    public MappedSchema withTables(List<MappedTable> tables) {
        setTables(tables);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (tables!= null) {
            sb.append("<tables>");
            sb.append(tables);
            sb.append("</tables>");
        }
        System.out.println("MappedSchema:toString Success");
        return sb.toString();
    }

    /*
     * added method that checks empty variable for better understandability
     * */
    public boolean isEmptyInput(MappedSchema value) {
    	return value.getInput()== null;
    }
    
    public boolean isEmptyOutput(MappedSchema value) {
    	return value.getOutput()== null;
    }

    public boolean isEmptyInputExpression(MappedSchema value) {
    	return value.getInputExpression()== null;
    }
    
    public boolean isEmptyTable(MappedSchema value) {
    	return value.getTables()== null;
    }
    
	/* 
	 * Self Encapsulate Field for making coupling weaker
	 */
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
        MappedSchema other = ((MappedSchema) that);
        if (isEmptyInput(this)) {
            if (!isEmptyInput(other)) {
                return false;
            }
        } else {
            if (!input.equals(other.input)) {
                return false;
            }
        }
        if (isEmptyInputExpression(this)) {
            if (!isEmptyInputExpression(other)) {
                return false;
            }
        } else {
            if (!inputExpression.equals(other.inputExpression)) {
                return false;
            }
        }
        if (isEmptyOutput(this)) {
            if (!isEmptyOutput(other)) {
                return false;
            }
        } else {
            if (!output.equals(other.output)) {
                return false;
            }
        }
        if (isEmptyTable(this)) {
            if (!isEmptyTable(other)) {
                return false;
            }
        } else {
            if (!tables.equals(other.tables)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((input == null)? 0 :input.hashCode()));
        result = ((prime*result)+((inputExpression == null)? 0 :inputExpression.hashCode()));
        result = ((prime*result)+((output == null)? 0 :output.hashCode()));
        result = ((prime*result)+((tables == null)? 0 :tables.hashCode()));
        return result;
    }

}
