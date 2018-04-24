package util;

import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseTest
{
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");

    private EntityManager em;

    @Test
    public void vraag1() throws Exception
    {
        em = emf.createEntityManager();

        try
        {
            //todo test something
        }
        catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 1", e);
            em.getTransaction().rollback();
        }
        finally
        {
            em.close();
        }
    }
}
