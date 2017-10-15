/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Departamento;
import entidades.Persona;
import entidades.MateriaGrupoProfesor;
import entidades.Profesor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class ProfesorJpaController implements Serializable {

    public ProfesorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Profesor profesor) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (profesor.getMateriaGrupoProfesorList() == null) {
            profesor.setMateriaGrupoProfesorList(new ArrayList<MateriaGrupoProfesor>());
        }
        List<String> illegalOrphanMessages = null;
        Persona personaOrphanCheck = profesor.getPersona();
        if (personaOrphanCheck != null) {
            Profesor oldProfesorOfPersona = personaOrphanCheck.getProfesor();
            if (oldProfesorOfPersona != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Persona " + personaOrphanCheck + " already has an item of type Profesor whose persona column cannot be null. Please make another selection for the persona field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Departamento depId = profesor.getDepId();
            if (depId != null) {
                depId = em.getReference(depId.getClass(), depId.getDepId());
                profesor.setDepId(depId);
            }
            Persona persona = profesor.getPersona();
            if (persona != null) {
                persona = em.getReference(persona.getClass(), persona.getPerIdentificacion());
                profesor.setPersona(persona);
            }
            List<MateriaGrupoProfesor> attachedMateriaGrupoProfesorList = new ArrayList<MateriaGrupoProfesor>();
            for (MateriaGrupoProfesor materiaGrupoProfesorListMateriaGrupoProfesorToAttach : profesor.getMateriaGrupoProfesorList()) {
                materiaGrupoProfesorListMateriaGrupoProfesorToAttach = em.getReference(materiaGrupoProfesorListMateriaGrupoProfesorToAttach.getClass(), materiaGrupoProfesorListMateriaGrupoProfesorToAttach.getMateriaGrupoProfesorPK());
                attachedMateriaGrupoProfesorList.add(materiaGrupoProfesorListMateriaGrupoProfesorToAttach);
            }
            profesor.setMateriaGrupoProfesorList(attachedMateriaGrupoProfesorList);
            em.persist(profesor);
            if (depId != null) {
                depId.getProfesorList().add(profesor);
                depId = em.merge(depId);
            }
            if (persona != null) {
                persona.setProfesor(profesor);
                persona = em.merge(persona);
            }
            for (MateriaGrupoProfesor materiaGrupoProfesorListMateriaGrupoProfesor : profesor.getMateriaGrupoProfesorList()) {
                Profesor oldProfesorOfMateriaGrupoProfesorListMateriaGrupoProfesor = materiaGrupoProfesorListMateriaGrupoProfesor.getProfesor();
                materiaGrupoProfesorListMateriaGrupoProfesor.setProfesor(profesor);
                materiaGrupoProfesorListMateriaGrupoProfesor = em.merge(materiaGrupoProfesorListMateriaGrupoProfesor);
                if (oldProfesorOfMateriaGrupoProfesorListMateriaGrupoProfesor != null) {
                    oldProfesorOfMateriaGrupoProfesorListMateriaGrupoProfesor.getMateriaGrupoProfesorList().remove(materiaGrupoProfesorListMateriaGrupoProfesor);
                    oldProfesorOfMateriaGrupoProfesorListMateriaGrupoProfesor = em.merge(oldProfesorOfMateriaGrupoProfesorListMateriaGrupoProfesor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProfesor(profesor.getPerIdentificacion()) != null) {
                throw new PreexistingEntityException("Profesor " + profesor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Profesor profesor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor persistentProfesor = em.find(Profesor.class, profesor.getPerIdentificacion());
            Departamento depIdOld = persistentProfesor.getDepId();
            Departamento depIdNew = profesor.getDepId();
            Persona personaOld = persistentProfesor.getPersona();
            Persona personaNew = profesor.getPersona();
            List<MateriaGrupoProfesor> materiaGrupoProfesorListOld = persistentProfesor.getMateriaGrupoProfesorList();
            List<MateriaGrupoProfesor> materiaGrupoProfesorListNew = profesor.getMateriaGrupoProfesorList();
            List<String> illegalOrphanMessages = null;
            if (personaNew != null && !personaNew.equals(personaOld)) {
                Profesor oldProfesorOfPersona = personaNew.getProfesor();
                if (oldProfesorOfPersona != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Persona " + personaNew + " already has an item of type Profesor whose persona column cannot be null. Please make another selection for the persona field.");
                }
            }
            for (MateriaGrupoProfesor materiaGrupoProfesorListOldMateriaGrupoProfesor : materiaGrupoProfesorListOld) {
                if (!materiaGrupoProfesorListNew.contains(materiaGrupoProfesorListOldMateriaGrupoProfesor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MateriaGrupoProfesor " + materiaGrupoProfesorListOldMateriaGrupoProfesor + " since its profesor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (depIdNew != null) {
                depIdNew = em.getReference(depIdNew.getClass(), depIdNew.getDepId());
                profesor.setDepId(depIdNew);
            }
            if (personaNew != null) {
                personaNew = em.getReference(personaNew.getClass(), personaNew.getPerIdentificacion());
                profesor.setPersona(personaNew);
            }
            List<MateriaGrupoProfesor> attachedMateriaGrupoProfesorListNew = new ArrayList<MateriaGrupoProfesor>();
            for (MateriaGrupoProfesor materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach : materiaGrupoProfesorListNew) {
                materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach = em.getReference(materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach.getClass(), materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach.getMateriaGrupoProfesorPK());
                attachedMateriaGrupoProfesorListNew.add(materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach);
            }
            materiaGrupoProfesorListNew = attachedMateriaGrupoProfesorListNew;
            profesor.setMateriaGrupoProfesorList(materiaGrupoProfesorListNew);
            profesor = em.merge(profesor);
            if (depIdOld != null && !depIdOld.equals(depIdNew)) {
                depIdOld.getProfesorList().remove(profesor);
                depIdOld = em.merge(depIdOld);
            }
            if (depIdNew != null && !depIdNew.equals(depIdOld)) {
                depIdNew.getProfesorList().add(profesor);
                depIdNew = em.merge(depIdNew);
            }
            if (personaOld != null && !personaOld.equals(personaNew)) {
                personaOld.setProfesor(null);
                personaOld = em.merge(personaOld);
            }
            if (personaNew != null && !personaNew.equals(personaOld)) {
                personaNew.setProfesor(profesor);
                personaNew = em.merge(personaNew);
            }
            for (MateriaGrupoProfesor materiaGrupoProfesorListNewMateriaGrupoProfesor : materiaGrupoProfesorListNew) {
                if (!materiaGrupoProfesorListOld.contains(materiaGrupoProfesorListNewMateriaGrupoProfesor)) {
                    Profesor oldProfesorOfMateriaGrupoProfesorListNewMateriaGrupoProfesor = materiaGrupoProfesorListNewMateriaGrupoProfesor.getProfesor();
                    materiaGrupoProfesorListNewMateriaGrupoProfesor.setProfesor(profesor);
                    materiaGrupoProfesorListNewMateriaGrupoProfesor = em.merge(materiaGrupoProfesorListNewMateriaGrupoProfesor);
                    if (oldProfesorOfMateriaGrupoProfesorListNewMateriaGrupoProfesor != null && !oldProfesorOfMateriaGrupoProfesorListNewMateriaGrupoProfesor.equals(profesor)) {
                        oldProfesorOfMateriaGrupoProfesorListNewMateriaGrupoProfesor.getMateriaGrupoProfesorList().remove(materiaGrupoProfesorListNewMateriaGrupoProfesor);
                        oldProfesorOfMateriaGrupoProfesorListNewMateriaGrupoProfesor = em.merge(oldProfesorOfMateriaGrupoProfesorListNewMateriaGrupoProfesor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = profesor.getPerIdentificacion();
                if (findProfesor(id) == null) {
                    throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor profesor;
            try {
                profesor = em.getReference(Profesor.class, id);
                profesor.getPerIdentificacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<MateriaGrupoProfesor> materiaGrupoProfesorListOrphanCheck = profesor.getMateriaGrupoProfesorList();
            for (MateriaGrupoProfesor materiaGrupoProfesorListOrphanCheckMateriaGrupoProfesor : materiaGrupoProfesorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Profesor (" + profesor + ") cannot be destroyed since the MateriaGrupoProfesor " + materiaGrupoProfesorListOrphanCheckMateriaGrupoProfesor + " in its materiaGrupoProfesorList field has a non-nullable profesor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Departamento depId = profesor.getDepId();
            if (depId != null) {
                depId.getProfesorList().remove(profesor);
                depId = em.merge(depId);
            }
            Persona persona = profesor.getPersona();
            if (persona != null) {
                persona.setProfesor(null);
                persona = em.merge(persona);
            }
            em.remove(profesor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Profesor> findProfesorEntities() {
        return findProfesorEntities(true, -1, -1);
    }

    public List<Profesor> findProfesorEntities(int maxResults, int firstResult) {
        return findProfesorEntities(false, maxResults, firstResult);
    }

    private List<Profesor> findProfesorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Profesor.class));
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

    public Profesor findProfesor(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Profesor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfesorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Profesor> rt = cq.from(Profesor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
