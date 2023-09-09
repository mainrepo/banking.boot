package org.microjava.rest.query;

/**
 *
 * @author  Gaurav J.
 * @version 1.0
 * @since   Jun 19, 2019
 *
 */
public class AccountQuery {
	private String name;
	private String user;
	private Double amount;
	
	/**
	 * @return the firstName
	 */
	public String getName() { return name; }
	/**
	 * @param firstName the firstName to set
	 */
	public void setName(String name) { this.name = name; }
	/**
	 * @return the lastName
	 */
	public String getUser() { return user; }
	/**
	 * @param lastName the lastName to set
	 */
	public void setUser(String user) { this.user = user; }
	/**
	 * @return the height
	 */
	public Double getAmount() { return amount; }
	/**
	 * @param height the height to set
	 */
	public void setAmount(Double amount) { this.amount = amount; }
}
