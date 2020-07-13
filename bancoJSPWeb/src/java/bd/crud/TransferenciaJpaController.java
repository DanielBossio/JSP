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
import bd.CuentaExt;
import bd.Transferencia;
import bd.Usuario;
import bd.crud.exceptions.NonexistentEntityException;
import bd.crud.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Win 8.1
 */
public class TransferenciaJpaController implements Serializable {

    public TransferenciaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Transferencia transferencia) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CuentaExt cuenta = transferencia.getCuenta();
            if (cuenta != null) {
                cuenta = em.getReference(cuenta.getClass(), cuenta.getNumcuenta());
                transferencia.setCuenta(cuenta);
            }
            Usuario usuario = transferencia.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getCuenta());
                transferencia.setUsuario(usuario);
            }
            em.persist(transferencia);
            if (cuenta != null) {
                cuenta.getTransferenciaList().add(transferencia);
                cuenta = em.merge(cuenta);
            }
            if (usuario != null) {
                usuario.getTransferenciaList().add(transferencia);
                usuario = em.merge(usuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransferencia(transferencia.getCodigo()) != null) {
                throw new PreexistingEntityException("Transferencia " + transferencia + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Transferencia transferencia) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transferencia persistentTransferencia = em.find(Transferencia.class, transferencia.getCodigo());
            CuentaExt cuentaOld = persistentTransferencia.getCuenta();
            CuentaExt cuentaNew = transferencia.getCuenta();
            Usuario usuarioOld = persistentTransferencia.getUsuario();
            Usuario usuarioNew = transferencia.getUsuario();
            if (cuentaNew != null) {
                cuentaNew = em.getReference(cuentaNew.getClass(), cuentaNew.getNumcuenta());
                transferencia.setCuenta(cuentaNew);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getCuenta());
                transferencia.setUsuario(usuarioNew);
            }
            transferencia = em.merge(transferencia);
            if (cuentaOld != null && !cuentaOld.equals(cuentaNew)) {
                cuentaOld.getTransferenciaList().remove(transferencia);
                cuentaOld = em.merge(cuentaOld);
            }
            if (cuentaNew != null && !cuentaNew.equals(cuentaOld)) {
                cuentaNew.getTransferenciaList().add(transferencia);
                cuentaNew = em.merge(cuentaNew);
            }
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getTransferenciaList().remove(transferencia);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getTransferenciaList().add(transferencia);
                usuarioNew = em.merge(usuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = transferencia.getCodigo();
                if (findTransferencia(id) == null) {
                    throw new NonexistentEntityException("The transferencia with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transferencia transferencia;
            try {
                transferencia = em.getReference(Transferencia.class, id);
                transferencia.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transferencia with id " + id + " no longer exists.", enfe);
            }
            CuentaExt cuenta = transferencia.getCuenta();
            if (cuenta != null) {
                cuenta.getTransferenciaList().remove(transferencia);
                cuenta = em.merge(cuenta);
            }
            Usuario usuario = transferencia.getUsuario();
            if (usuario != null) {
                usuario.getTransferenciaList().remove(transferencia);
                usuario = em.merge(usuario);
            }
            em.remove(transferencia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Transferencia> findTransferenciaEntities() {
        return findTransferenciaEntities(true, -1, -1);
    }

    public List<Transferencia> findTransferenciaEntities(int maxResults, int firstResult) {
        return findTransferenciaEntities(false, maxResults, firstResult);
    }

    private List<Transferencia> findTransferenciaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Transferencia.class));
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

    public Transferencia findTransferencia(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transferencia.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransferenciaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Transferencia> rt = cq.from(Transferencia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
