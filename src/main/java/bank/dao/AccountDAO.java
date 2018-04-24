package bank.dao;

import bank.domain.Account;
import java.util.List;

public interface AccountDAO {

    /**
     *
     * @return number of account instances
     */
    int count();

    /**
     * The account is persisted. If a account with the same id allready exists an EntityExistsException is thrown
     * @param account
     */
    void create(Account account);

    /**
     * Merge the state of the given account into persistant context. If the account did not exist an IllegalArgumentException is thrown
     * @param account
     */
    void edit(Account account);

    /**
     *
     * @param id
     * @return the found entity instance or null if the entity does not exist
     */
    Account find(Long id);

    /**
     *
     * @return list of account instances
     */
    List<Account> findAll();

    /**
     *
     * @param email
     * @return unique account instance with parameter email or null if such account doesn't exist
     */
    Account findByAccountNr(Long accountNr);

    /**
     * Remove the entity instance
     * @param account - entity instance
     */
    void remove(Account account);
}
