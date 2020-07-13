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
import bd.Transferencia;
import java.util.ArrayList;
import java.util.List;
import bd.Pago;
import bd.Usuario;
import bd.crud.exceptions.IllegalOrphanException;
import bd.crud.exceptions.NonexistentEntityException;
import bd.crud.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Win 8.1
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getTransferenciaList() == null) {
            usuario.setTransferenciaList(new ArrayList<Transferencia>());
        }
        if (usuario.getPagoList() == null) {
            usuario.setPagoList(new ArrayList<Pago>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Transferencia> attachedTransferenciaList = new ArrayList<Transferencia>();
            for (Transferencia transferenciaListTransferenciaToAttach : usuario.getTransferenciaList()) {
                transferenciaListTransferenciaToAttach = em.getReference(transferenciaListTransferenciaToAttach.getClass(), transferenciaListTransferenciaToAttach.getCodigo());
                attachedTransferenciaList.add(transferenciaListTransferenciaToAttach);
            }
            usuario.setTransferenciaList(attachedTransferenciaList);
            List<Pago> attachedPagoList = new ArrayList<Pago>();
            for (Pago pagoListPagoToAttach : usuario.getPagoList()) {
                pagoListPagoToAttach = em.getReference(pagoListPagoToAttach.getClass(), pagoListPagoToAttach.getCodigo());
                attachedPagoList.add(pagoListPagoToAttach);
            }
            usuario.setPagoList(attachedPagoList);
            em.persist(usuario);
            for (Transferencia transferenciaListTransferencia : usuario.getTransferenciaList()) {
                Usuario oldUsuarioOfTransferenciaListTransferencia = transferenciaListTransferencia.getUsuario();
                transferenciaListTransferencia.setUsuario(usuario);
                transferenciaListTransferencia = em.merge(transferenciaListTransferencia);
                if (oldUsuarioOfTransferenciaListTransferencia != null) {
                    oldUsuarioOfTransferenciaListTransferencia.getTransferenciaList().remove(transferenciaListTransferencia);
                    oldUsuarioOfTransferenciaListTransferencia = em.merge(oldUsuarioOfTransferenciaListTransferencia);
                }
            }
            for (Pago pagoListPago : usuario.getPagoList()) {
                Usuario oldUsuarioOfPagoListPago = pagoListPago.getUsuario();
                pagoListPago.setUsuario(usuario);
                pagoListPago = em.merge(pagoListPago);
                if (oldUsuarioOfPagoListPago != null) {
                    oldUsuarioOfPagoListPago.getPagoList().remove(pagoListPago);
                    oldUsuarioOfPagoListPago = em.merge(oldUsuarioOfPagoListPago);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getCuenta()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getCuenta());
            List<Transferencia> transferenciaListOld = persistentUsuario.getTransferenciaList();
            List<Transferencia> transferenciaListNew = usuario.getTransferenciaList();
            List<Pago> pagoListOld = persistentUsuario.getPagoList();
            List<Pago> pagoListNew = usuario.getPagoList();
            List<String> illegalOrphanMessages = null;
            for (Transferencia transferenciaListOldTransferencia : transferenciaListOld) {
                if (!transferenciaListNew.contains(transferenciaListOldTransferencia)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Transferencia " + transferenciaListOldTransferencia + " since its usuario field is not nullable.");
                }
            }
            for (Pago pagoListOldPago : pagoListOld) {
                if (!pagoListNew.contains(pagoListOldPago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pago " + pagoListOldPago + " since its usuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Transferencia> attachedTransferenciaListNew = new ArrayList<Transferencia>();
            for (Transferencia transferenciaListNewTransferenciaToAttach : transferenciaListNew) {
                transferenciaListNewTransferenciaToAttach = em.getReference(transferenciaListNewTransferenciaToAttach.getClass(), transferenciaListNewTransferenciaToAttach.getCodigo());
                attachedTransferenciaListNew.add(transferenciaListNewTransferenciaToAttach);
            }
            transferenciaListNew = attachedTransferenciaListNew;
            usuario.setTransferenciaList(transferenciaListNew);
            List<Pago> attachedPagoListNew = new ArrayList<Pago>();
            for (Pago pagoListNewPagoToAttach : pagoListNew) {
                pagoListNewPagoToAttach = em.getReference(pagoListNewPagoToAttach.getClass(), pagoListNewPagoToAttach.getCodigo());
                attachedPagoListNew.add(pagoListNewPagoToAttach);
            }
            pagoListNew = attachedPagoListNew;
            usuario.setPagoList(pagoListNew);
            usuario = em.merge(usuario);
            for (Transferencia transferenciaListNewTransferencia : transferenciaListNew) {
                if (!transferenciaListOld.contains(transferenciaListNewTransferencia)) {
                    Usuario oldUsuarioOfTransferenciaListNewTransferencia = transferenciaListNewTransferencia.getUsuario();
                    transferenciaListNewTransferencia.setUsuario(usuario);
                    transferenciaListNewTransferencia = em.merge(transferenciaListNewTransferencia);
                    if (oldUsuarioOfTransferenciaListNewTransferencia != null && !oldUsuarioOfTransferenciaListNewTransferencia.equals(usuario)) {
                        oldUsuarioOfTransferenciaListNewTransferencia.getTransferenciaList().remove(transferenciaListNewTransferencia);
                        oldUsuarioOfTransferenciaListNewTransferencia = em.merge(oldUsuarioOfTransferenciaListNewTransferencia);
                    }
                }
            }
            for (Pago pagoListNewPago : pagoListNew) {
                if (!pagoListOld.contains(pagoListNewPago)) {
                    Usuario oldUsuarioOfPagoListNewPago = pagoListNewPago.getUsuario();
                    pagoListNewPago.setUsuario(usuario);
                    pagoListNewPago = em.merge(pagoListNewPago);
                    if (oldUsuarioOfPagoListNewPago != null && !oldUsuarioOfPagoListNewPago.equals(usuario)) {
                        oldUsuarioOfPagoListNewPago.getPagoList().remove(pagoListNewPago);
                        oldUsuarioOfPagoListNewPago = em.merge(oldUsuarioOfPagoListNewPago);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getCuenta();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getCuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Transferencia> transferenciaListOrphanCheck = usuario.getTransferenciaList();
            for (Transferencia transferenciaListOrphanCheckTransferencia : transferenciaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Transferencia " + transferenciaListOrphanCheckTransferencia + " in its transferenciaList field has a non-nullable usuario field.");
            }
            List<Pago> pagoListOrphanCheck = usuario.getPagoList();
            for (Pago pagoListOrphanCheckPago : pagoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Pago " + pagoListOrphanCheckPago + " in its pagoList field has a non-nullable usuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
