package org.microjava.model.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This file is part of pre-tested advancej library.
 * The Person is a domain model pojo.
 * 
 * @author  Gaurav J.
 * @version 1.0
 * @since   May 10, 2019
 *
 */
public class Person {
	private String firstName;
	private String lastName;
	private LocalDate dob;
	private float height;
	private float weight;
	private char sex;

	/**
	 * name as Gaurav Joshi
	 * birthDate as yyyy-mm-dd
	 * 
	 * @param name
	 * @param birthDate
	 */
	public Person(String name, String birthDate, Character gender) {
		dob = LocalDate.parse(birthDate);
		String [] nam = name.split("\\s+");
		firstName = nam[0];
		lastName = nam[1];
		sex = gender;
	}
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() { return firstName; }
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) { this.firstName = firstName; }
	/**
	 * @return the lastName
	 */
	public String getLastName() { return lastName; }
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) { this.lastName = lastName; }
	/**
	 * @return the dob
	 */
	public LocalDate getDob() { return dob; }
	/**
	 * @param dob the dob to set
	 */
	public void setDob(LocalDate dob) { this.dob = dob; }
	/**
	 * @return the height
	 */
	public float getHeight() { return height; }
	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) { this.height = height; }
	/**
	 * @return the weight
	 */
	public float getWeight() { return weight; }
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(float weight) { this.weight = weight; }
	/**
	 * @return the gender
	 */
	public char getGender() { return sex; }
	/**
	 * @param gender the gender to set
	 */
	public void setGender(char gender) { this.sex = gender; }
	/**
	 * 
	 * @return int
	 */
	public int getAge() {
		Period p = dob.until(LocalDate.now());
		return p.getYears();
	}
	/**
	 * @return detailedAge
	 */
	public Period getDetailedAge() {
		return dob.until(LocalDate.now());
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "{", "}") ;
		sj.setEmptyValue("");
		Period p = dob.until(LocalDate.now());
		
		sj.add("\"firstName\":\""+firstName+"\"");
		sj.add("\"lastName\":\""+lastName+"\"");
		sj.add("\"dob\":\""+dob+"\"");
		sj.add("\"age\":\""+getAge()+"\"");
		sj.add("\"weight\":\""+weight+"\"");
		sj.add("\"height\":\""+height+"\"");
		sj.add("\"gender\":\""+(sex == 'F'?"Female":"Male")+"\"");
		sj.add("\"ageDetail\":\""+String.format("%d years, %d months & %d days: total %d days", p.getYears(), p.getMonths(), p.getDays(), dob.until(LocalDate.now(), ChronoUnit.DAYS))+"\"");
		
		return sj.toString(); 
	}
	
	public static final Builder newBuilder(String name, String birthDate, Character gender) {
		Person p = new Person(name, birthDate, gender);
		return new Builder(p);
	}
	
	public static Stream<Person> lines (Path path) throws IOException{
		Spliterator<Person> peopleSplit = new PersonSpliterator(Files.lines(path).spliterator());
		return StreamSupport.stream(peopleSplit, false);
	}
}