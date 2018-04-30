package util;

import bank.dao.AccountDAOJPAImpl;
import bank.domain.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNotEquals;

public class DatabaseTest {
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");

    private EntityManager em;

    @Before
    public void before() throws Exception {
        try {
            new DatabaseCleaner(emf.createEntityManager()).clean();
            em = emf.createEntityManager();
        } catch (Exception e) {
            LOGGER.log(Level.CONFIG, "Could not initiate connection to database", e);
        }
    }

    @After
    public void after() throws Exception {
        em.close();
    }

    @Test
    public void vraag1() throws Exception {
        try {
            Account account = new Account(111L);
            em.getTransaction().begin();
            em.persist(account);
//TODO: verklaar en pas eventueel aan
            assertNull(account.getId());
            em.getTransaction().commit();
            System.out.println("AccountId: " + account.getId());
//TODO: verklaar en pas eventueel aan
            assertTrue(account.getId() > 0L);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 1", e);
            em.getTransaction().rollback();
        }
    }

    @Test
    public void vraag2() throws Exception {
        try {
            Account account = new Account(111L);
            em.getTransaction().begin();
            em.persist(account);
            assertNull(account.getId());
            em.getTransaction().rollback();
// TODO code om te testen dat table account geen records bevat. Hint: bestudeer/gebruik AccountDAOJPAImpl
            assertEquals(0, new AccountDAOJPAImpl(em).count());
            assertTrue(new AccountDAOJPAImpl(em).findAll().isEmpty());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 2", e);
            em.getTransaction().rollback();
        }
    }

    @Test
    public void vraag3() throws Exception {
        try {
            Long expected = -100L;
            Account account = new Account(111L);
            account.setId(expected);
            em.getTransaction().begin();
            em.persist(account);
//TODO: verklaar en pas eventueel aan
            em.flush();
            assertNotEquals(expected, account.getId());

//TODO: verklaar en pas eventueel aan
            em.getTransaction().commit();
            assertNotEquals(expected, account.getId());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 3", e);
            em.getTransaction().rollback();
        }
    }

    @Test
    public void vraag4() throws Exception {
        Long expectedBalance = 400L;
        Account account = new Account(114L);
        em.getTransaction().begin();
        em.persist(account);
        account.setBalance(expectedBalance);
        em.getTransaction().commit();
        assertEquals(expectedBalance, account.getBalance()); // account balance wordt geset bij "account.setBalance(expectedBalance)"
//TODO: verklaar de waarde van account.getBalance
        Long cid = account.getId();
        account = null;
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Account found = em2.find(Account.class, cid);
//TODO: verklaar de waarde van found.getBalance
        assertEquals(expectedBalance, found.getBalance()); // account balance wordt opgehaald om te zien of in een andere stream hetzelfde getal wordt gevonden en teruggestuurd

        em2.close(); // added

    }

    @Test
    public void vraag5() throws Exception {
        Account account = new Account(114L); //create account
        account.setBalance(0L); //set initial balance

        em.getTransaction().begin();
        em.persist(account); //put the object in the database
        em.getTransaction().commit();
        Long cid = account.getId(); //hold the Id of the account to retrieve it later

        EntityManager em2 = emf.createEntityManager(); //create a second connection
        em2.getTransaction().begin();
        Account found = em2.find(Account.class, cid); //get the same object from the database

        em.getTransaction().begin();
        account.setBalance(500L); //change the value from connection A
        em.getTransaction().commit();

        em2.refresh(found); //refresh connection 2

        assertEquals(500L, found.getBalance().longValue());
        //check if value changed in connection 2
    }

    @Test
    public void vraag6() throws Exception {

    }

    @Test
    public void vraag7() throws Exception {

    }

    @Test
    public void vraag8() throws Exception {

    }

    @Test
    public void vraag9() throws Exception {

    }
}
