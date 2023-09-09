package org.microjava.business;

import java.util.List;
import java.util.Map;

import org.microjava.model.entities.Account;
import org.microjava.model.entities.AccountDetails;
import org.microjava.rest.query.AccountQuery;

public interface AccountProcessor {

	List<Account> findAll();
	
	Account findById(Long id);
	
	List<Account> findByName(String name);
	
	List<AccountDetails> findAllById(Long id);
	
	List<Account> findAccounts(AccountQuery account);
	
	Map<String, Map<String, List<AccountDetails>>> findHistoryById(Long id);
	
	Account save(Account account);
	
	void delete(Account account);
}
