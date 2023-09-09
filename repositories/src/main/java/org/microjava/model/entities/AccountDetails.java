package org.microjava.model.entities;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This file is part of pre-tested advancej library.
 * The UserDetails is for...
 *
 * @author  Gaurav J.
 * @version 1.0
 * @since   May 14, 2019
 *
 */

public class AccountDetails implements Serializable {

	private static final long serialVersionUID = -9072855204306864443L;
	
	private String name;
	
	private String user;
	
	private Double amount;
	
	private Double balance;
	
	private String type;
	
	private String histAmount;
	
	private String histUser;
	
	private String updated;
	
	private String active;
	
	public AccountDetails() {}
	
	//u.firstName, u.lastName, u.height, u.weight, u.gender, r.name, r.type, m.name as mem, m.type as memt, u.updated, u.active
	public AccountDetails(String name, String user, Double h, Double w, String  g, String amnt, String huser, String u, String a) {
		
		u = (u != null && u.indexOf(" ") > -1)?u.split("\\s+")[0] : u;
		
		this.name = name;
		this.user = user;
		amount = h;
		balance = w;
		type = g;
		histAmount = amnt;
		histUser = huser;
		
		updated = (null == u) ? String.valueOf(LocalDate.now()) : String.valueOf(u);
		
		active = a;
	}

	/**
	 * @return the firstName
	 */
	public String getName() { return name; }

	/**
	 * @return the lastName
	 */
	public String getUser() { return user; }

	/**
	 * @return the height
	 */
	public Double getAmonut() { return amount; }

	/**
	 * @return the weight
	 */
	public Double getBalance() { return balance; }

	/**
	 * @return the gender
	 */
	public String getType() { return type; }
	
	/**
	 * @return the membership
	 */
	public String getHistAmount() { return histAmount; }

	/**
	 * @return the membershipType
	 */
	public String getHistUser() { return histUser; }

	/**
	 * @return the updated
	 */
	public String getUpdated() { return updated; }

	/**
	 * @return the active
	 */
	public String getActive() { return active; }
}
