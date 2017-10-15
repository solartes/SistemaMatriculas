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
import entidades.Profesor;
import entidades.Estudiante;
import entidades.Persona;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class PersonaJpaController implements Serializable {

    public PersonaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Persona persona) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor profesor = persona.getProfesor();
            if (profesor != null) {
                profesor = em.getReference(profesor.getClass(), profesor.getPerIdentificacion());
                persona.setProfesor(profesor);
            }
            Estudiante estudiante = persona.getEstudiante();
            if (estudiante != null) {
                estudiante = em.getReference(estudiante.getClass(), estudiante.getPerIdentificacion());
                persona.setEstudiante(estudiante);
            }
            em.persist(persona);
            if (profesor != null) {
                Persona oldPersonaOfProfesor = profesor.getPersona();
                if (oldPersonaOfProfesor != null) {
                    oldPersonaOfProfesor.setProfesor(null);
                    oldPersonaOfProfesor = em.merge(oldPersonaOfProfesor);
                }
                profesor.setPersona(persona);
                profesor = em.merge(profesor);
            }
            if (estudiante != null) {
                Persona oldPersonaOfEstudiante = estudiante.getPersona();
                if (oldPersonaOfEstudiante != null) {
                    oldPersonaOfEstudiante.setEstudiante(null);
                    oldPersonaOfEstudiante = em.merge(oldPersonaOfEstudiante);
                }
                estudiante.setPersona(persona);
                estudiante = em.merge(estudiante);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPersona(persona.getPerIdentificacion()) != null) {
                throw new PreexistingEntityException("Persona " + persona + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Persona persona) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona persistentPersona = em.find(Persona.class, persona.getPerIdentificacion());
            Profesor profesorOld = persistentPersona.getProfesor();
            Profesor profesorNew = persona.getProfesor();
            Estudiante estudianteOld = persistentPersona.getEstudiante();
            Estudiante estudianteNew = persona.getEstudiante();
            List<String> illegalOrphanMessages = null;
            if (profesorOld != null && !profesorOld.equals(profesorNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Profesor " + profesorOld + " since its persona field is not nullable.");
            }
            if (estudianteOld != null && !estudianteOld.equals(estudianteNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Estudiante " + estudianteOld + " since its persona field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (profesorNew != null) {
                profesorNew = em.getReference(profesorNew.getClass(), profesorNew.getPerIdentificacion());
                persona.setProfesor(profesorNew);
            }
            if (estudianteNew != null) {
                estudianteNew = em.getReference(estudianteNew.getClass(), estudianteNew.getPerIdentificacion());
                persona.setEstudiante(estudianteNew);
            }
            persona = em.merge(persona);
            if (profesorNew != null && !profesorNew.equals(profesorOld)) {
                Persona oldPersonaOfProfesor = profesorNew.getPersona();
                if (oldPersonaOfProfesor != null) {
                    oldPersonaOfProfesor.setProfesor(null);
                    oldPersonaOfProfesor = em.merge(oldPersonaOfProfesor);
                }
                profesorNew.setPersona(persona);
                profesorNew = em.merge(profesorNew);
            }
            if (estudianteNew != null && !estudianteNew.equals(estudianteOld)) {
                Persona oldPersonaOfEstudiante = estudianteNew.getPersona();
                if (oldPersonaOfEstudiante != null) {
                    oldPersonaOfEstudiante.setEstudiante(null);
                    oldPersonaOfEstudiante = em.merge(oldPersonaOfEstudiante);
                }
                estudianteNew.setPersona(persona);
                estudianteNew = em.merge(estudianteNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = persona.getPerIdentificacion();
                if (findPersona(id) == null) {
                    throw new NonexistentEntityException("The persona with id " + id + " no longer exists.");
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
            Persona persona;
            try {
                persona = em.getReference(Persona.class, id);
                persona.getPerIdentificacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persona with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Profesor profesorOrphanCheck = persona.getProfesor();
            if (profesorOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Profesor " + profesorOrphanCheck + " in its profesor field has a non-nullable persona field.");
            }
            Estudiante estudianteOrphanCheck = persona.getEstudiante();
            if (estudianteOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persona (" + persona + ") cannot be destroyed since the Estudiante " + estudianteOrphanCheck + " in its estudiante field has a non-nullable persona field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(persona);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Persona> findPersonaEntities() {
        return findPersonaEntities(true, -1, -1);
    }

    public List<Persona> findPersonaEntities(int maxResults, int firstResult) {
        return findPersonaEntities(false, maxResults, firstResult);
    }

    private List<Persona> findPersonaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Persona.class));
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

    public Persona findPersona(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Persona.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Persona> rt = cq.from(Persona.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
