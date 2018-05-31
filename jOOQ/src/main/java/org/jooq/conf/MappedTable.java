
package org.jooq.conf;

import java.io.Serializable;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A table mapping configuration.
 *
 *
 *
 */


/* - Extract Superclass : Move similar method and field with MappedSchemaclass to superclass
 * - Self Encapsulate Field
 * - Added methods that checks empty variable for better understandability
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MappedTable", propOrder = {

})
@SuppressWarnings({
	"all"
})
public class MappedTable
extends MappedBase
implements Serializable, Cloneable{

	public MappedTable withInput(String value) {
		setInput(value);
		return this;
	}

	public MappedTable withInputExpression(Pattern value) {
		setInputExpression(value);
		return this;
	}

	public MappedTable withOutput(String value) {
		setOutput(value);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		System.out.println("MappedTable:toString Success");
		return sb.toString();
	}


	/* CY
	 * added method that checks empty variable for better understandability
	 * */
	public boolean isEmptyInput(MappedTable value) {
		return value.getInput()== null;
	}

	public boolean isEmptyOutput(MappedTable value) {
		return value.getOutput()== null;
	}

	public boolean isEmptyInputExpression(MappedTable value) {
		return value.getInputExpression()== null;
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
		MappedTable other = ((MappedTable) that);
		
		/* 
		 * this.input -> this.getInput()
		 * Self Encapsulate Field for making coupling weaker
		 */
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
			if (!getInputExpression().equals(other.getInputExpression())) {
				return false;
			}
		}
		if (isEmptyOutput(this)) {
			if (!isEmptyOutput(other)) {
				return false;
			}
		} else {
			if (!getOutput().equals(other.getOutput())) {
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
		return result;
	}

}
