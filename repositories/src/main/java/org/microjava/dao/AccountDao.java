package org.microjava.dao;

import java.util.List;
import java.util.Optional;

import org.microjava.model.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDao extends JpaRepository<Account, Long> {

	Optional<Account> findByName(String x);
	
	Optional<List<Account>> name(String x);
	
	Optional<List<Account>> user(String x);
	
	Optional<List<Account>> userAndAmount(String y, Double z);
	
	Optional<List<Account>> nameAndUser(String x, String y);
	
	Optional<List<Account>> nameAndUserAndAmount(String x, String y, Double z);
	
	  @Query(value =
	  "select u.name, u.user, u.amount, u.balance, u.type, m.amount as amnt, m.user as huser, u.updated, u.active "
	  + "from account u inner join " +
	  "history_link ug on ug.account_id = u.id inner join " +
	  "account_history m on ug.history_id = m.id " +
	  "where u.id = :id", nativeQuery = true)
	  List<Object[]> findAccountDetails(@Param("id") Long id);
	
	@Query(value = "select u.name, u.user, u.amount, u.balance, u.type, m.amount as amnt, m.user as huser, u.updated, u.active "
			+ "from Account u join AccountHistory m where u.id = :id")
	List<Object[]> findDetails(@Param("id") Long id);
	 
	
}
