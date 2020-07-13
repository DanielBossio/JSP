/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.crud;

import bd.BancoExt;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import bd.CuentaExt;
import bd.crud.exceptions.IllegalOrphanException;
import bd.crud.exceptions.NonexistentEntityException;
import bd.crud.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Win 8.1
 */
public class BancoExtJpaController implements Serializable {

    public BancoExtJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(BancoExt bancoExt) throws PreexistingEntityException, Exception {
        if (bancoExt.getCuentaExtList() == null) {
            bancoExt.setCuentaExtList(new ArrayList<CuentaExt>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CuentaExt> attachedCuentaExtList = new ArrayList<CuentaExt>();
            for (CuentaExt cuentaExtListCuentaExtToAttach : bancoExt.getCuentaExtList()) {
                cuentaExtListCuentaExtToAttach = em.getReference(cuentaExtListCuentaExtToAttach.getClass(), cuentaExtListCuentaExtToAttach.getNumcuenta());
                attachedCuentaExtList.add(cuentaExtListCuentaExtToAttach);
            }
            bancoExt.setCuentaExtList(attachedCuentaExtList);
            em.persist(bancoExt);
            for (CuentaExt cuentaExtListCuentaExt : bancoExt.getCuentaExtList()) {
                BancoExt oldBancoOfCuentaExtListCuentaExt = cuentaExtListCuentaExt.getBanco();
                cuentaExtListCuentaExt.setBanco(bancoExt);
                cuentaExtListCuentaExt = em.merge(cuentaExtListCuentaExt);
                if (oldBancoOfCuentaExtListCuentaExt != null) {
                    oldBancoOfCuentaExtListCuentaExt.getCuentaExtList().remove(cuentaExtListCuentaExt);
                    oldBancoOfCuentaExtListCuentaExt = em.merge(oldBancoOfCuentaExtListCuentaExt);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBancoExt(bancoExt.getNombre()) != null) {
                throw new PreexistingEntityException("BancoExt " + bancoExt + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(BancoExt bancoExt) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            BancoExt persistentBancoExt = em.find(BancoExt.class, bancoExt.getNombre());
            List<CuentaExt> cuentaExtListOld = persistentBancoExt.getCuentaExtList();
            List<CuentaExt> cuentaExtListNew = bancoExt.getCuentaExtList();
            List<String> illegalOrphanMessages = null;
            for (CuentaExt cuentaExtListOldCuentaExt : cuentaExtListOld) {
                if (!cuentaExtListNew.contains(cuentaExtListOldCuentaExt)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CuentaExt " + cuentaExtListOldCuentaExt + " since its banco field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CuentaExt> attachedCuentaExtListNew = new ArrayList<CuentaExt>();
            for (CuentaExt cuentaExtListNewCuentaExtToAttach : cuentaExtListNew) {
                cuentaExtListNewCuentaExtToAttach = em.getReference(cuentaExtListNewCuentaExtToAttach.getClass(), cuentaExtListNewCuentaExtToAttach.getNumcuenta());
                attachedCuentaExtListNew.add(cuentaExtListNewCuentaExtToAttach);
            }
            cuentaExtListNew = attachedCuentaExtListNew;
            bancoExt.setCuentaExtList(cuentaExtListNew);
            bancoExt = em.merge(bancoExt);
            for (CuentaExt cuentaExtListNewCuentaExt : cuentaExtListNew) {
                if (!cuentaExtListOld.contains(cuentaExtListNewCuentaExt)) {
                    BancoExt oldBancoOfCuentaExtListNewCuentaExt = cuentaExtListNewCuentaExt.getBanco();
                    cuentaExtListNewCuentaExt.setBanco(bancoExt);
                    cuentaExtListNewCuentaExt = em.merge(cuentaExtListNewCuentaExt);
                    if (oldBancoOfCuentaExtListNewCuentaExt != null && !oldBancoOfCuentaExtListNewCuentaExt.equals(bancoExt)) {
                        oldBancoOfCuentaExtListNewCuentaExt.getCuentaExtList().remove(cuentaExtListNewCuentaExt);
                        oldBancoOfCuentaExtListNewCuentaExt = em.merge(oldBancoOfCuentaExtListNewCuentaExt);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = bancoExt.getNombre();
                if (findBancoExt(id) == null) {
                    throw new NonexistentEntityException("The bancoExt with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            BancoExt bancoExt;
            try {
                bancoExt = em.getReference(BancoExt.class, id);
                bancoExt.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bancoExt with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CuentaExt> cuentaExtListOrphanCheck = bancoExt.getCuentaExtList();
            for (CuentaExt cuentaExtListOrphanCheckCuentaExt : cuentaExtListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This BancoExt (" + bancoExt + ") cannot be destroyed since the CuentaExt " + cuentaExtListOrphanCheckCuentaExt + " in its cuentaExtList field has a non-nullable banco field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(bancoExt);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<BancoExt> findBancoExtEntities() {
        return findBancoExtEntities(true, -1, -1);
    }

    public List<BancoExt> findBancoExtEntities(int maxResults, int firstResult) {
        return findBancoExtEntities(false, maxResults, firstResult);
    }

    private List<BancoExt> findBancoExtEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(BancoExt.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public BancoExt findBancoExt(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BancoExt.class, id);
        } finally {
            em.close();
        }
    }

    public int getBancoExtCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<BancoExt> rt = cq.from(BancoExt.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
