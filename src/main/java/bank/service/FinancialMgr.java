package bank.service;

import bank.domain.Account;
import bank.dao.AccountDAO;
import bank.dao.AccountDAOJPAImpl;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class FinancialMgr {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");

    public Boolean transfer(Long fromAccountNr, Long toAccountNr, Long amount) {
        EntityManager em = emf.createEntityManager();
        AccountDAO accountDAO = new AccountDAOJPAImpl(em);
        Boolean success = null;
        em.getTransaction().begin();
        try {
            Account fromAccount = accountDAO.findByAccountNr(fromAccountNr);
            Account toAccount = accountDAO.findByAccountNr(toAccountNr);
            if (fromAccount.add(0L - amount)) {
                success = toAccount.add(amount);
            } else {
                success = false;
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return success;
    }

    public Boolean deposit(Long accountNr, Long amount) {
        EntityManager em = emf.createEntityManager();
        AccountDAO accountDAO = new AccountDAOJPAImpl(em);
        Boolean success = null;
        em.getTransaction().begin();
        try {
            Account account = accountDAO.findByAccountNr(accountNr);
            success = account.add(amount);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return success;
    }

    public Boolean withDraw(Long accountNr, Long amount) {
        EntityManager em = emf.createEntityManager();
        AccountDAO accountDAO = new AccountDAOJPAImpl(em);
        Boolean success = null;
        em.getTransaction().begin();
        try {
            Account account = accountDAO.findByAccountNr(accountNr);
            success = account.add(0L - amount);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return success;
    }
}
