package bank.service;

import bank.domain.Account;
import bank.dao.AccountDAO;
import bank.dao.AccountDAOJPAImpl;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class GoldenAccountMgr {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");

    public Account createGoldenAccount(Long accountNr) {
        EntityManager em = emf.createEntityManager();
        AccountDAO accountDAO = new AccountDAOJPAImpl(em);
        Account account = new Account(accountNr);
        account.setThreshold(-1000L);
        em.getTransaction().begin();
        try {
            accountDAO.create(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return account;
    }

    public Account updrade2GoldenAccount(Long accountNr) {
        EntityManager em = emf.createEntityManager();
        AccountDAO accountDAO = new AccountDAOJPAImpl(em);
        Account account = null;
        em.getTransaction().begin();
        try {
            account = accountDAO.findByAccountNr(accountNr);
            account.setThreshold(-1000L);
            accountDAO.edit(account);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return account;
    }
}
