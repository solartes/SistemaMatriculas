/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import entidades.Materia;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Programa;
import entidades.MateriaOfertada;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class MateriaJpaController implements Serializable {

    public MateriaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Materia materia) throws PreexistingEntityException, Exception {
        if (materia.getMateriaOfertadaList() == null) {
            materia.setMateriaOfertadaList(new ArrayList<MateriaOfertada>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Programa prgId = materia.getPrgId();
            if (prgId != null) {
                prgId = em.getReference(prgId.getClass(), prgId.getPrgId());
                materia.setPrgId(prgId);
            }
            List<MateriaOfertada> attachedMateriaOfertadaList = new ArrayList<MateriaOfertada>();
            for (MateriaOfertada materiaOfertadaListMateriaOfertadaToAttach : materia.getMateriaOfertadaList()) {
                materiaOfertadaListMateriaOfertadaToAttach = em.getReference(materiaOfertadaListMateriaOfertadaToAttach.getClass(), materiaOfertadaListMateriaOfertadaToAttach.getMateriaOfertadaPK());
                attachedMateriaOfertadaList.add(materiaOfertadaListMateriaOfertadaToAttach);
            }
            materia.setMateriaOfertadaList(attachedMateriaOfertadaList);
            em.persist(materia);
            if (prgId != null) {
                prgId.getMateriaList().add(materia);
                prgId = em.merge(prgId);
            }
            for (MateriaOfertada materiaOfertadaListMateriaOfertada : materia.getMateriaOfertadaList()) {
                Materia oldMateriaOfMateriaOfertadaListMateriaOfertada = materiaOfertadaListMateriaOfertada.getMateria();
                materiaOfertadaListMateriaOfertada.setMateria(materia);
                materiaOfertadaListMateriaOfertada = em.merge(materiaOfertadaListMateriaOfertada);
                if (oldMateriaOfMateriaOfertadaListMateriaOfertada != null) {
                    oldMateriaOfMateriaOfertadaListMateriaOfertada.getMateriaOfertadaList().remove(materiaOfertadaListMateriaOfertada);
                    oldMateriaOfMateriaOfertadaListMateriaOfertada = em.merge(oldMateriaOfMateriaOfertadaListMateriaOfertada);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMateria(materia.getMatCod()) != null) {
                throw new PreexistingEntityException("Materia " + materia + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Materia materia) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Materia persistentMateria = em.find(Materia.class, materia.getMatCod());
            Programa prgIdOld = persistentMateria.getPrgId();
            Programa prgIdNew = materia.getPrgId();
            List<MateriaOfertada> materiaOfertadaListOld = persistentMateria.getMateriaOfertadaList();
            List<MateriaOfertada> materiaOfertadaListNew = materia.getMateriaOfertadaList();
            List<String> illegalOrphanMessages = null;
            for (MateriaOfertada materiaOfertadaListOldMateriaOfertada : materiaOfertadaListOld) {
                if (!materiaOfertadaListNew.contains(materiaOfertadaListOldMateriaOfertada)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MateriaOfertada " + materiaOfertadaListOldMateriaOfertada + " since its materia field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (prgIdNew != null) {
                prgIdNew = em.getReference(prgIdNew.getClass(), prgIdNew.getPrgId());
                materia.setPrgId(prgIdNew);
            }
            List<MateriaOfertada> attachedMateriaOfertadaListNew = new ArrayList<MateriaOfertada>();
            for (MateriaOfertada materiaOfertadaListNewMateriaOfertadaToAttach : materiaOfertadaListNew) {
                materiaOfertadaListNewMateriaOfertadaToAttach = em.getReference(materiaOfertadaListNewMateriaOfertadaToAttach.getClass(), materiaOfertadaListNewMateriaOfertadaToAttach.getMateriaOfertadaPK());
                attachedMateriaOfertadaListNew.add(materiaOfertadaListNewMateriaOfertadaToAttach);
            }
            materiaOfertadaListNew = attachedMateriaOfertadaListNew;
            materia.setMateriaOfertadaList(materiaOfertadaListNew);
            materia = em.merge(materia);
            if (prgIdOld != null && !prgIdOld.equals(prgIdNew)) {
                prgIdOld.getMateriaList().remove(materia);
                prgIdOld = em.merge(prgIdOld);
            }
            if (prgIdNew != null && !prgIdNew.equals(prgIdOld)) {
                prgIdNew.getMateriaList().add(materia);
                prgIdNew = em.merge(prgIdNew);
            }
            for (MateriaOfertada materiaOfertadaListNewMateriaOfertada : materiaOfertadaListNew) {
                if (!materiaOfertadaListOld.contains(materiaOfertadaListNewMateriaOfertada)) {
                    Materia oldMateriaOfMateriaOfertadaListNewMateriaOfertada = materiaOfertadaListNewMateriaOfertada.getMateria();
                    materiaOfertadaListNewMateriaOfertada.setMateria(materia);
                    materiaOfertadaListNewMateriaOfertada = em.merge(materiaOfertadaListNewMateriaOfertada);
                    if (oldMateriaOfMateriaOfertadaListNewMateriaOfertada != null && !oldMateriaOfMateriaOfertadaListNewMateriaOfertada.equals(materia)) {
                        oldMateriaOfMateriaOfertadaListNewMateriaOfertada.getMateriaOfertadaList().remove(materiaOfertadaListNewMateriaOfertada);
                        oldMateriaOfMateriaOfertadaListNewMateriaOfertada = em.merge(oldMateriaOfMateriaOfertadaListNewMateriaOfertada);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = materia.getMatCod();
                if (findMateria(id) == null) {
                    throw new NonexistentEntityException("The materia with id " + id + " no longer exists.");
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
            Materia materia;
            try {
                materia = em.getReference(Materia.class, id);
                materia.getMatCod();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The materia with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<MateriaOfertada> materiaOfertadaListOrphanCheck = materia.getMateriaOfertadaList();
            for (MateriaOfertada materiaOfertadaListOrphanCheckMateriaOfertada : materiaOfertadaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Materia (" + materia + ") cannot be destroyed since the MateriaOfertada " + materiaOfertadaListOrphanCheckMateriaOfertada + " in its materiaOfertadaList field has a non-nullable materia field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Programa prgId = materia.getPrgId();
            if (prgId != null) {
                prgId.getMateriaList().remove(materia);
                prgId = em.merge(prgId);
            }
            em.remove(materia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Materia> findMateriaEntities() {
        return findMateriaEntities(true, -1, -1);
    }

    public List<Materia> findMateriaEntities(int maxResults, int firstResult) {
        return findMateriaEntities(false, maxResults, firstResult);
    }

    private List<Materia> findMateriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Materia.class));
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

    public Materia findMateria(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Materia.class, id);
        } finally {
            em.close();
        }
    }

    public int getMateriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Materia> rt = cq.from(Materia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
