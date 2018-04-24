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
        new DatabaseCleaner(emf.createEntityManager()).clean();
        em = emf.createEntityManager();
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
            //account werd niet gezien als entity omdat hij nog niet in de presistant.xml staat
            assertNull(account.getId());
            em.getTransaction().commit();
            System.out.println("AccountId: " + account.getId());
//TODO: verklaar en pas eventueel aan
            // het id bestond al, de database werd nog niet gecleand, kijk naar de @before, hier staat de databasecleaner
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
}
