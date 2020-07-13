/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.crud;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import bd.BancoExt;
import bd.CuentaExt;
import bd.Transferencia;
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
public class CuentaExtJpaController implements Serializable {

    public CuentaExtJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CuentaExt cuentaExt) throws PreexistingEntityException, Exception {
        if (cuentaExt.getTransferenciaList() == null) {
            cuentaExt.setTransferenciaList(new ArrayList<Transferencia>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            BancoExt banco = cuentaExt.getBanco();
            if (banco != null) {
                banco = em.getReference(banco.getClass(), banco.getNombre());
                cuentaExt.setBanco(banco);
            }
            List<Transferencia> attachedTransferenciaList = new ArrayList<Transferencia>();
            for (Transferencia transferenciaListTransferenciaToAttach : cuentaExt.getTransferenciaList()) {
                transferenciaListTransferenciaToAttach = em.getReference(transferenciaListTransferenciaToAttach.getClass(), transferenciaListTransferenciaToAttach.getCodigo());
                attachedTransferenciaList.add(transferenciaListTransferenciaToAttach);
            }
            cuentaExt.setTransferenciaList(attachedTransferenciaList);
            em.persist(cuentaExt);
            if (banco != null) {
                banco.getCuentaExtList().add(cuentaExt);
                banco = em.merge(banco);
            }
            for (Transferencia transferenciaListTransferencia : cuentaExt.getTransferenciaList()) {
                CuentaExt oldCuentaOfTransferenciaListTransferencia = transferenciaListTransferencia.getCuenta();
                transferenciaListTransferencia.setCuenta(cuentaExt);
                transferenciaListTransferencia = em.merge(transferenciaListTransferencia);
                if (oldCuentaOfTransferenciaListTransferencia != null) {
                    oldCuentaOfTransferenciaListTransferencia.getTransferenciaList().remove(transferenciaListTransferencia);
                    oldCuentaOfTransferenciaListTransferencia = em.merge(oldCuentaOfTransferenciaListTransferencia);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCuentaExt(cuentaExt.getNumcuenta()) != null) {
                throw new PreexistingEntityException("CuentaExt " + cuentaExt + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CuentaExt cuentaExt) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CuentaExt persistentCuentaExt = em.find(CuentaExt.class, cuentaExt.getNumcuenta());
            BancoExt bancoOld = persistentCuentaExt.getBanco();
            BancoExt bancoNew = cuentaExt.getBanco();
            List<Transferencia> transferenciaListOld = persistentCuentaExt.getTransferenciaList();
            List<Transferencia> transferenciaListNew = cuentaExt.getTransferenciaList();
            List<String> illegalOrphanMessages = null;
            for (Transferencia transferenciaListOldTransferencia : transferenciaListOld) {
                if (!transferenciaListNew.contains(transferenciaListOldTransferencia)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Transferencia " + transferenciaListOldTransferencia + " since its cuenta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (bancoNew != null) {
                bancoNew = em.getReference(bancoNew.getClass(), bancoNew.getNombre());
                cuentaExt.setBanco(bancoNew);
            }
            List<Transferencia> attachedTransferenciaListNew = new ArrayList<Transferencia>();
            for (Transferencia transferenciaListNewTransferenciaToAttach : transferenciaListNew) {
                transferenciaListNewTransferenciaToAttach = em.getReference(transferenciaListNewTransferenciaToAttach.getClass(), transferenciaListNewTransferenciaToAttach.getCodigo());
                attachedTransferenciaListNew.add(transferenciaListNewTransferenciaToAttach);
            }
            transferenciaListNew = attachedTransferenciaListNew;
            cuentaExt.setTransferenciaList(transferenciaListNew);
            cuentaExt = em.merge(cuentaExt);
            if (bancoOld != null && !bancoOld.equals(bancoNew)) {
                bancoOld.getCuentaExtList().remove(cuentaExt);
                bancoOld = em.merge(bancoOld);
            }
            if (bancoNew != null && !bancoNew.equals(bancoOld)) {
                bancoNew.getCuentaExtList().add(cuentaExt);
                bancoNew = em.merge(bancoNew);
            }
            for (Transferencia transferenciaListNewTransferencia : transferenciaListNew) {
                if (!transferenciaListOld.contains(transferenciaListNewTransferencia)) {
                    CuentaExt oldCuentaOfTransferenciaListNewTransferencia = transferenciaListNewTransferencia.getCuenta();
                    transferenciaListNewTransferencia.setCuenta(cuentaExt);
                    transferenciaListNewTransferencia = em.merge(transferenciaListNewTransferencia);
                    if (oldCuentaOfTransferenciaListNewTransferencia != null && !oldCuentaOfTransferenciaListNewTransferencia.equals(cuentaExt)) {
                        oldCuentaOfTransferenciaListNewTransferencia.getTransferenciaList().remove(transferenciaListNewTransferencia);
                        oldCuentaOfTransferenciaListNewTransferencia = em.merge(oldCuentaOfTransferenciaListNewTransferencia);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = cuentaExt.getNumcuenta();
                if (findCuentaExt(id) == null) {
                    throw new NonexistentEntityException("The cuentaExt with id " + id + " no longer exists.");
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
            CuentaExt cuentaExt;
            try {
                cuentaExt = em.getReference(CuentaExt.class, id);
                cuentaExt.getNumcuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cuentaExt with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Transferencia> transferenciaListOrphanCheck = cuentaExt.getTransferenciaList();
            for (Transferencia transferenciaListOrphanCheckTransferencia : transferenciaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CuentaExt (" + cuentaExt + ") cannot be destroyed since the Transferencia " + transferenciaListOrphanCheckTransferencia + " in its transferenciaList field has a non-nullable cuenta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            BancoExt banco = cuentaExt.getBanco();
            if (banco != null) {
                banco.getCuentaExtList().remove(cuentaExt);
                banco = em.merge(banco);
            }
            em.remove(cuentaExt);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CuentaExt> findCuentaExtEntities() {
        return findCuentaExtEntities(true, -1, -1);
    }

    public List<CuentaExt> findCuentaExtEntities(int maxResults, int firstResult) {
        return findCuentaExtEntities(false, maxResults, firstResult);
    }

    private List<CuentaExt> findCuentaExtEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CuentaExt.class));
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

    public CuentaExt findCuentaExt(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CuentaExt.class, id);
        } finally {
            em.close();
        }
    }

    public int getCuentaExtCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CuentaExt> rt = cq.from(CuentaExt.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
