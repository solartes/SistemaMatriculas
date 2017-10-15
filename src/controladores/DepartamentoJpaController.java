/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import entidades.Departamento;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Facultad;
import entidades.Profesor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class DepartamentoJpaController implements Serializable {

    public DepartamentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Departamento departamento) throws PreexistingEntityException, Exception {
        if (departamento.getProfesorList() == null) {
            departamento.setProfesorList(new ArrayList<Profesor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facultad facId = departamento.getFacId();
            if (facId != null) {
                facId = em.getReference(facId.getClass(), facId.getFacId());
                departamento.setFacId(facId);
            }
            List<Profesor> attachedProfesorList = new ArrayList<Profesor>();
            for (Profesor profesorListProfesorToAttach : departamento.getProfesorList()) {
                profesorListProfesorToAttach = em.getReference(profesorListProfesorToAttach.getClass(), profesorListProfesorToAttach.getPerIdentificacion());
                attachedProfesorList.add(profesorListProfesorToAttach);
            }
            departamento.setProfesorList(attachedProfesorList);
            em.persist(departamento);
            if (facId != null) {
                facId.getDepartamentoList().add(departamento);
                facId = em.merge(facId);
            }
            for (Profesor profesorListProfesor : departamento.getProfesorList()) {
                Departamento oldDepIdOfProfesorListProfesor = profesorListProfesor.getDepId();
                profesorListProfesor.setDepId(departamento);
                profesorListProfesor = em.merge(profesorListProfesor);
                if (oldDepIdOfProfesorListProfesor != null) {
                    oldDepIdOfProfesorListProfesor.getProfesorList().remove(profesorListProfesor);
                    oldDepIdOfProfesorListProfesor = em.merge(oldDepIdOfProfesorListProfesor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDepartamento(departamento.getDepId()) != null) {
                throw new PreexistingEntityException("Departamento " + departamento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Departamento departamento) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Departamento persistentDepartamento = em.find(Departamento.class, departamento.getDepId());
            Facultad facIdOld = persistentDepartamento.getFacId();
            Facultad facIdNew = departamento.getFacId();
            List<Profesor> profesorListOld = persistentDepartamento.getProfesorList();
            List<Profesor> profesorListNew = departamento.getProfesorList();
            List<String> illegalOrphanMessages = null;
            for (Profesor profesorListOldProfesor : profesorListOld) {
                if (!profesorListNew.contains(profesorListOldProfesor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Profesor " + profesorListOldProfesor + " since its depId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (facIdNew != null) {
                facIdNew = em.getReference(facIdNew.getClass(), facIdNew.getFacId());
                departamento.setFacId(facIdNew);
            }
            List<Profesor> attachedProfesorListNew = new ArrayList<Profesor>();
            for (Profesor profesorListNewProfesorToAttach : profesorListNew) {
                profesorListNewProfesorToAttach = em.getReference(profesorListNewProfesorToAttach.getClass(), profesorListNewProfesorToAttach.getPerIdentificacion());
                attachedProfesorListNew.add(profesorListNewProfesorToAttach);
            }
            profesorListNew = attachedProfesorListNew;
            departamento.setProfesorList(profesorListNew);
            departamento = em.merge(departamento);
            if (facIdOld != null && !facIdOld.equals(facIdNew)) {
                facIdOld.getDepartamentoList().remove(departamento);
                facIdOld = em.merge(facIdOld);
            }
            if (facIdNew != null && !facIdNew.equals(facIdOld)) {
                facIdNew.getDepartamentoList().add(departamento);
                facIdNew = em.merge(facIdNew);
            }
            for (Profesor profesorListNewProfesor : profesorListNew) {
                if (!profesorListOld.contains(profesorListNewProfesor)) {
                    Departamento oldDepIdOfProfesorListNewProfesor = profesorListNewProfesor.getDepId();
                    profesorListNewProfesor.setDepId(departamento);
                    profesorListNewProfesor = em.merge(profesorListNewProfesor);
                    if (oldDepIdOfProfesorListNewProfesor != null && !oldDepIdOfProfesorListNewProfesor.equals(departamento)) {
                        oldDepIdOfProfesorListNewProfesor.getProfesorList().remove(profesorListNewProfesor);
                        oldDepIdOfProfesorListNewProfesor = em.merge(oldDepIdOfProfesorListNewProfesor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = departamento.getDepId();
                if (findDepartamento(id) == null) {
                    throw new NonexistentEntityException("The departamento with id " + id + " no longer exists.");
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
            Departamento departamento;
            try {
                departamento = em.getReference(Departamento.class, id);
                departamento.getDepId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The departamento with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Profesor> profesorListOrphanCheck = departamento.getProfesorList();
            for (Profesor profesorListOrphanCheckProfesor : profesorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Departamento (" + departamento + ") cannot be destroyed since the Profesor " + profesorListOrphanCheckProfesor + " in its profesorList field has a non-nullable depId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Facultad facId = departamento.getFacId();
            if (facId != null) {
                facId.getDepartamentoList().remove(departamento);
                facId = em.merge(facId);
            }
            em.remove(departamento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Departamento> findDepartamentoEntities() {
        return findDepartamentoEntities(true, -1, -1);
    }

    public List<Departamento> findDepartamentoEntities(int maxResults, int firstResult) {
        return findDepartamentoEntities(false, maxResults, firstResult);
    }

    private List<Departamento> findDepartamentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Departamento.class));
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

    public Departamento findDepartamento(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Departamento.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartamentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Departamento> rt = cq.from(Departamento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
