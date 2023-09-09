package org.microjava.model.command;

import java.time.LocalDate;

/**
 * This file is part of pre-tested advancej library.
 * The Builder is for...
 *
 * @author  Gaurav J.
 * @version 1.0
 * @since   May 20, 2019
 *
 */
public class Builder {

	private final Person p;
	
	public Builder(Person person) {
		this.p = person;
	}
	
	public Builder withName(String name){
		String [] nam = name.split("\\s+");
		p.setFirstName(nam[0]);
		p.setLastName(nam[1]);
		return this;
	}
	
	public Builder withDob(String birthDate){
		p.setDob(LocalDate.parse(birthDate));
		return this;
	}
	
	public Builder withGender(Character gender){
		p.setGender(gender);
		return this;
	}
	
	public Builder withHeight(float height) {
		p.setHeight(height);
		return this;
	}
	
	public Builder withWeight(float weight) {
		p.setWeight(weight);
		return this;
	}
	
	public Person build() {
		return p;
	}
}
