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

public class DatabaseTest
{
    private static final Logger LOGGER = Logger.getLogger(DatabaseTest.class.getName());
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");

    private EntityManager em;

    @Before
    public void before() throws Exception
    {
        try
        {
            new DatabaseCleaner(emf.createEntityManager()).clean();
            em = emf.createEntityManager();
        } catch (Exception e)
        {
            LOGGER.log(Level.CONFIG, "Could not initiate connection to database", e);
        }
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
            //account werd niet gezien als entity omdat hij nog niet in de presistant.xml staat
            assertNull(account.getId());
            em.getTransaction().commit();
            System.out.println("AccountId: " + account.getId());
//TODO: verklaar en pas eventueel aan
            // het id bestond al, de database werd nog niet gecleand, kijk naar de @before, hier staat de databasecleaner
            assertTrue(account.getId() > 0L);

        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 1", e);
            em.getTransaction().rollback();
        }
    }

    @Test
    public void vraag2() throws Exception
    {
        try
        {
            Account account = new Account(111L);
            em.getTransaction().begin();
            em.persist(account);
            assertNull(account.getId());
            em.getTransaction().rollback();
// TODO code om te testen dat table account geen records bevat. Hint: bestudeer/gebruik AccountDAOJPAImpl
            assertEquals(0, new AccountDAOJPAImpl(em).count());

        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 2", e);
            em.getTransaction().rollback();
        }
    }

    @Test
    public void vraag3() throws Exception
    {
        try
        {
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

        } catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong during question 3", e);
            em.getTransaction().rollback();
        }
    }

    @Test
    public void vraag4() throws Exception
    {
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
    public void vraag5() throws Exception
    {
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

        assertEquals(500L, found.getBalance().longValue()); //check if value changed in connection 2
    }

    @Test
    public void vraag6() throws Exception
    {
        AccountDAOJPAImpl accountDAOJPA = new AccountDAOJPAImpl(em);

        Account acc = new Account(1L);
        Account acc2 = new Account(2L);
        Account acc9 = new Account(9L);

// scenario 1

        assertEquals("Balance is not equal to initial value (0)", 0L, acc.getBalance().longValue()); //added
        Long balance1 = 100L;
        em.getTransaction().begin();
        em.persist(acc);
        acc.setBalance(balance1);
        em.getTransaction().commit();
        assertEquals("Balance is not equal to the set balance", balance1, acc.getBalance()); //added

//TODO: voeg asserties toe om je verwachte waarde van de attributen te verifieren.
//TODO: doe dit zowel voor de bovenstaande java objecten als voor opnieuw bij de entitymanager opgevraagde objecten met overeenkomstig Id.

        //check changes in database
        Long id = acc.getId();
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Account testAcc = em2.find(Account.class, id);
        assertEquals("Balance is not equal to the object in the database", balance1, testAcc.getBalance());



// scenario 2
        Long balance2a = 211L;
        acc = new Account(2L);
        em.getTransaction().begin();
        acc9 = em.merge(acc);
        acc.setBalance(balance2a);
        acc9.setBalance(balance2a+balance2a);
        em.getTransaction().commit();

//TODO: voeg asserties toe om je verwachte waarde van de attributen te verifiëren.
//TODO: doe dit zowel voor de bovenstaande java objecten als voor opnieuw bij de entitymanager opgevraagde objecten met overeenkomstig Id.
// HINT: gebruik acccountDAO.findByAccountNr

        assertEquals("Balance is not equal to the set balance", balance2a, acc.getBalance());
        assertEquals("Balance is not equal to the set balance (2)", balance2a+balance2a, acc9.getBalance().longValue());

        testAcc = accountDAOJPA.findByAccountNr(acc.getAccountNr());
        Account testAcc9 = accountDAOJPA.findByAccountNr(acc9.getAccountNr());

        assertNotEquals("Balance is equal to the incorrect balance in database", balance2a, testAcc.getBalance());
        assertEquals("Balance is not equal to the set balance in database (2)", balance2a+balance2a, testAcc9.getBalance().longValue());


// scenario 3
        // TODO: 30-Apr-18 continue working here
        Long balance3b = 322L;
        Long balance3c = 333L;
        acc = new Account(3L);
        em.getTransaction().begin();
        acc2 = em.merge(acc);

        assertTrue(em.contains(acc)); // verklaar
        assertTrue(em.contains(acc2)); // verklaar
        assertEquals(acc, acc2);  //verklaar

        acc2.setBalance(balance3b);
        acc.setBalance(balance3c);
        em.getTransaction().commit();
//TODO: voeg asserties toe om je verwachte waarde van de attributen te verifiëren.
//TODO: doe dit zowel voor de bovenstaande java objecten als voor opnieuw bij de entitymanager opgevraagde objecten met overeenkomstig Id.


// scenario 4
        Account account = new Account(114L) ;
        account.setBalance(450L) ;
        EntityManager em = emf.createEntityManager() ;
        em.getTransaction().begin() ;
        em.persist(account) ;
        em.getTransaction().commit() ;


        Account account2 = new Account(114L) ;
        Account tweedeAccountObject = account2 ;
        tweedeAccountObject.setBalance(650l) ;
        assertEquals((Long)650L,account2.getBalance()) ;  //verklaar
        account2.setId(account.getId()) ;
        em.getTransaction().begin() ;
        account2 = em.merge(account2) ;
        assertSame(account,account2) ;  //verklaar
        assertTrue(em.contains(account2)) ;  //verklaar
        assertFalse(em.contains(tweedeAccountObject)) ;  //verklaar
        tweedeAccountObject.setBalance(850l) ;
        assertEquals((Long)650L,account.getBalance()) ;  //verklaar
        assertEquals((Long)650L,account2.getBalance()) ;  //verklaar
        em.getTransaction().commit() ;
        em.close() ;

    }

    @Test
    public void vraag7() throws Exception
    {
        Account acc1 = new Account(77L);
        em.getTransaction().begin();
        em.persist(acc1);
        em.getTransaction().commit();
//Database bevat nu een account.

// scenario 1
        Account accF1;
        Account accF2;
        accF1 = em.find(Account.class, acc1.getId());
        accF2 = em.find(Account.class, acc1.getId());
        assertSame(accF1, accF2);

// scenario 2
        accF1 = em.find(Account.class, acc1.getId());
        em.clear();
        accF2 = em.find(Account.class, acc1.getId());
        assertSame(accF1, accF2);
//TODO verklaar verschil tussen beide scenario’s
        /*
        In scenario 1 zijn beide objecten uit de database gehaald waarbij het zelfde object wordt teruggestuurd maar op twee verschillende punten in het geheugen (pointers worden vergeleken '==')
        In scenario 2 wordt nadat accF1 is opgehaald de context van de EntityManager gecleared waarna het tweede object wordt opgehaald
        */
    }

    @Test
    public void vraag8() throws  Exception
    {
        Account acc1 = new Account(88L);
        em.getTransaction().begin();
        em.persist(acc1);
        em.getTransaction().commit();
        Long id = acc1.getId();
//Database bevat nu een account.

        em.remove(acc1); //changed "Em.remove(acc1)" to current
        assertEquals(id, acc1.getId()); //de data is uit de database gehaald maar lokaal leeft het object nog
        Account accFound = em.find(Account.class, id);
        assertNull(accFound); //de data is uit de database gehaald dus er wordt geen data gevonden waardoor null wordt teruggestuurd
//TODO: verklaar bovenstaande asserts
    }

    @Test
    public void vraag9() throws Exception
    {

    }
}
