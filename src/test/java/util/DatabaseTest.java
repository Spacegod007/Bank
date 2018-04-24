package util;

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

public class DatabaseTest
{
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");

    private EntityManager em;

    @Before
    public void before() throws Exception
    {
        new DatabaseCleaner(emf.createEntityManager()).clean();
        em = emf.createEntityManager();
    }

    @After
    public void after() throws Exception
    {
        em.close();
    }

    @Test
    public void vraag1() throws Exception
    {
        try
        {
            Account account = new Account(111L);
            em.getTransaction().begin();
            em.persist(account);
//TODO: verklaar en pas eventueel aan
            assertNull(account.getId());
            em.getTransaction().commit();
            System.out.println("AccountId: " + account.getId());
//TODO: verklaar en pas eventueel aan
            assertTrue(account.getId() > 0L);

        }
        catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 1", e);
            em.getTransaction().rollback();
        }
    }
}
