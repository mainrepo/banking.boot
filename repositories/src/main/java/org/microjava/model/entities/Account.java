package org.microjava.model.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

@Entity
@Table(name = "account")
public class Account implements Serializable {

	private static final long serialVersionUID = 6851895163408334308L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Column(name = "name")
	private String name;

	@Column(name = "user")
	private String user;

	@Nullable
	@Column(name = "amount")
	private Double amount;

	@Nullable
	@Column(name = "balance")
	private Double balance;

	@NotNull
	@Column(name = "type")
	private String type;

	@NotNull
	@Column(name = "updated")
	private String updated;

	@Nullable
	@Column(name = "active")
	private String active = "TRUE";
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinTable(	name = "history_link", joinColumns = @JoinColumn(name = "account_id"),
				inverseJoinColumns = @JoinColumn(name = "history_id"))
	private List<AccountHistory> history;

	@Transient
	private Map<String, String> attributes = new HashMap<String, String>();
	
	public Account with(String key, String value) {
		attributes.putIfAbsent(key, value);
		return this;
	}
	
	public Account withReason(String text) {
		attributes.putIfAbsent("reason", text);
		return this;
	}
	
	/**
	 * @return the attributes
	 */
	@JsonAnyGetter
	public Map<String, String> getAttributes() { return attributes; }

	/**
	 * @return the memberships
	 */
	public List<AccountHistory> getHistory() { return history; }

	/**
	 * @param memberships the memberships to set
	 */
	public void setHistory(List<AccountHistory> history) {
		this.history = history;
	}

	public Long getId() { return id; }

	public void setId(Long id) { this.id = id; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getUser() { return user; }

	public void setUser(String user) { this.user = user; }

	public Double getAmount() { return amount; }

	public void setAmount(Double amount) { this.amount = amount; }

	public Double getBalance() { return balance; }

	public void setBalance(Double balance) { this.balance = balance; }

	public String getType() { return type; }

	public void setType(String type) { this.type = type; }

	public String getUpdated() { return updated; }

	public void setUpdated(String updated) { this.updated = updated; }

	public String getActive() { return active; }

	public void setActive(String active) { this.active = active; }

	public Account withId(Long id) {
		this.setId(id);
		return this;
	}

	public Account withName(String name) {
		this.setName(name);
		return this;
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "{", "}");
		sj.add("\"name\":\""+name+"\"");
		sj.add("\"user\":\""+user+"\"");
		sj.add("\"amount\":\""+amount+"\"");
		sj.add("\"balance\":\""+balance+"\"");
		sj.add("\"type\":\""+type+"\"");
		sj.add("\"updated\":\""+updated+"\"");
		sj.add("\"active\":\""+active+"\"");
		
		return sj.toString(); 
	}
}
