package org.microjava.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.microjava.business.AccountProcessor;
import org.microjava.dao.AccountDao;
import org.microjava.model.entities.Account;
import org.microjava.model.entities.AccountDetails;
import org.microjava.rest.query.AccountQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountProcessorImpl implements AccountProcessor {

	private final Logger log = LoggerFactory.getLogger(AccountProcessor.class);
	
	@Autowired
	private AccountDao accounts;
	
	@Override
	public List<Account> findAll() {
		return accounts.findAll();
	}
	
	@Override
	public Account findById(Long id) {
		Optional<Account> account = accounts.findById(id); 
		return account.isPresent() ? account.get() : new Account().withId(-1L).withName("Not Applicable");
	}
	
	@Override
	public List<Account> findAccounts(AccountQuery account) {
		Optional<List<Account>> accountList = accounts.nameAndUserAndAmount(account.getName(), account.getUser(), account.getAmount());
		
		accountList = accountList.isPresent() ? accountList : accounts.nameAndUser(account.getName(), account.getUser());
		
		accountList = accountList.isPresent() ? accountList : accounts.userAndAmount(account.getUser(), account.getAmount());
		
		accountList = accountList.isPresent() ? accountList : accounts.name(account.getName());
		
		accountList = accountList.isPresent() ? accountList : accounts.user(account.getUser());
		
		return accountList.isPresent() ? accountList.get() : new ArrayList<>();
	}
	
	@Override
	public List<Account> findByName(String name) {
		log.info("account name --> "+name);
		
		Optional<List<Account>> accountList = accounts.name(name);
		
		return accountList.isPresent() ? accountList.get() : new ArrayList<>();
	}
	
	@Override
	public List<AccountDetails> findAllById(Long id) {
		List<Object[]> queryResults = accounts.findDetails(id);
		log.info("results --> "+(queryResults==null?0:queryResults.size()));
		
		//u.firstName, u.lastName, u.amount, u.balance, u.type, m.amount as amnt, m.type as ttype, u.updated, u.active
		return queryResults.stream().map(details ->
			new AccountDetails(String.valueOf(details[0]), String.valueOf(details[1]), 
					Double.parseDouble(String.valueOf(details[2])), Double.parseDouble(String.valueOf(details[3])),
					String.valueOf(details[4]),String.valueOf(details[5]), String.valueOf(details[6]),
					String.valueOf(details[7]), String.valueOf(details[8]))
		).collect(Collectors.toList());
	}
	
	@Override
	public Map<String, Map<String, List<AccountDetails>>> findHistoryById(Long id) {
		List<Object[]> queryResults = accounts.findAccountDetails(id);
		log.info("results --> "+(queryResults==null?0:queryResults.size()));
		
		Map<String, Map<String, List<AccountDetails>>> accountDetails = new HashMap<>();
		
		  queryResults.stream().map(details ->
		  	//u.firstName, u.lastName, u.amount, u.balance, u.type, m.amount as amnt, m.type as ttype, u.updated, u.active
			new AccountDetails(String.valueOf(details[0]), String.valueOf(details[1]), 
				Double.parseDouble(String.valueOf(details[2])), Double.parseDouble(String.valueOf(details[3])),
				String.valueOf(details[4]),String.valueOf(details[5]), String.valueOf(details[6]),
				String.valueOf(details[7]), String.valueOf(details[8]))
		).
		forEach(details -> {
			accountDetails.computeIfAbsent(details.getHistAmount(), m -> new HashMap<>())
			.merge(details.getHistUser(), 
				new ArrayList<>(Arrays.asList(details)), 
				(l1,l2) -> {
					l1.addAll(l2);
					return l1;
			});
		});
		
		  return accountDetails;
	}

	@Override
	public Account save(Account account) {
		return accounts.save(account);
	}
	
	@Override
	public void delete(Account account) {
		accounts.deleteById(account.getId());
	}
}