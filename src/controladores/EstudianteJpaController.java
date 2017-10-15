/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import entidades.Estudiante;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Persona;
import entidades.Programa;
import java.util.ArrayList;
import java.util.List;
import entidades.Nota;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class EstudianteJpaController implements Serializable {

    public EstudianteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estudiante estudiante) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (estudiante.getProgramaList() == null) {
            estudiante.setProgramaList(new ArrayList<Programa>());
        }
        if (estudiante.getNotaList() == null) {
            estudiante.setNotaList(new ArrayList<Nota>());
        }
        List<String> illegalOrphanMessages = null;
        Persona personaOrphanCheck = estudiante.getPersona();
        if (personaOrphanCheck != null) {
            Estudiante oldEstudianteOfPersona = personaOrphanCheck.getEstudiante();
            if (oldEstudianteOfPersona != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Persona " + personaOrphanCheck + " already has an item of type Estudiante whose persona column cannot be null. Please make another selection for the persona field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persona persona = estudiante.getPersona();
            if (persona != null) {
                persona = em.getReference(persona.getClass(), persona.getPerIdentificacion());
                estudiante.setPersona(persona);
            }
            List<Programa> attachedProgramaList = new ArrayList<Programa>();
            for (Programa programaListProgramaToAttach : estudiante.getProgramaList()) {
                programaListProgramaToAttach = em.getReference(programaListProgramaToAttach.getClass(), programaListProgramaToAttach.getPrgId());
                attachedProgramaList.add(programaListProgramaToAttach);
            }
            estudiante.setProgramaList(attachedProgramaList);
            List<Nota> attachedNotaList = new ArrayList<Nota>();
            for (Nota notaListNotaToAttach : estudiante.getNotaList()) {
                notaListNotaToAttach = em.getReference(notaListNotaToAttach.getClass(), notaListNotaToAttach.getNotaPK());
                attachedNotaList.add(notaListNotaToAttach);
            }
            estudiante.setNotaList(attachedNotaList);
            em.persist(estudiante);
            if (persona != null) {
                persona.setEstudiante(estudiante);
                persona = em.merge(persona);
            }
            for (Programa programaListPrograma : estudiante.getProgramaList()) {
                programaListPrograma.getEstudianteList().add(estudiante);
                programaListPrograma = em.merge(programaListPrograma);
            }
            for (Nota notaListNota : estudiante.getNotaList()) {
                Estudiante oldEstudianteOfNotaListNota = notaListNota.getEstudiante();
                notaListNota.setEstudiante(estudiante);
                notaListNota = em.merge(notaListNota);
                if (oldEstudianteOfNotaListNota != null) {
                    oldEstudianteOfNotaListNota.getNotaList().remove(notaListNota);
                    oldEstudianteOfNotaListNota = em.merge(oldEstudianteOfNotaListNota);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEstudiante(estudiante.getPerIdentificacion()) != null) {
                throw new PreexistingEntityException("Estudiante " + estudiante + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estudiante estudiante) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estudiante persistentEstudiante = em.find(Estudiante.class, estudiante.getPerIdentificacion());
            Persona personaOld = persistentEstudiante.getPersona();
            Persona personaNew = estudiante.getPersona();
            List<Programa> programaListOld = persistentEstudiante.getProgramaList();
            List<Programa> programaListNew = estudiante.getProgramaList();
            List<Nota> notaListOld = persistentEstudiante.getNotaList();
            List<Nota> notaListNew = estudiante.getNotaList();
            List<String> illegalOrphanMessages = null;
            if (personaNew != null && !personaNew.equals(personaOld)) {
                Estudiante oldEstudianteOfPersona = personaNew.getEstudiante();
                if (oldEstudianteOfPersona != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Persona " + personaNew + " already has an item of type Estudiante whose persona column cannot be null. Please make another selection for the persona field.");
                }
            }
            for (Nota notaListOldNota : notaListOld) {
                if (!notaListNew.contains(notaListOldNota)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Nota " + notaListOldNota + " since its estudiante field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personaNew != null) {
                personaNew = em.getReference(personaNew.getClass(), personaNew.getPerIdentificacion());
                estudiante.setPersona(personaNew);
            }
            List<Programa> attachedProgramaListNew = new ArrayList<Programa>();
            for (Programa programaListNewProgramaToAttach : programaListNew) {
                programaListNewProgramaToAttach = em.getReference(programaListNewProgramaToAttach.getClass(), programaListNewProgramaToAttach.getPrgId());
                attachedProgramaListNew.add(programaListNewProgramaToAttach);
            }
            programaListNew = attachedProgramaListNew;
            estudiante.setProgramaList(programaListNew);
            List<Nota> attachedNotaListNew = new ArrayList<Nota>();
            for (Nota notaListNewNotaToAttach : notaListNew) {
                notaListNewNotaToAttach = em.getReference(notaListNewNotaToAttach.getClass(), notaListNewNotaToAttach.getNotaPK());
                attachedNotaListNew.add(notaListNewNotaToAttach);
            }
            notaListNew = attachedNotaListNew;
            estudiante.setNotaList(notaListNew);
            estudiante = em.merge(estudiante);
            if (personaOld != null && !personaOld.equals(personaNew)) {
                personaOld.setEstudiante(null);
                personaOld = em.merge(personaOld);
            }
            if (personaNew != null && !personaNew.equals(personaOld)) {
                personaNew.setEstudiante(estudiante);
                personaNew = em.merge(personaNew);
            }
            for (Programa programaListOldPrograma : programaListOld) {
                if (!programaListNew.contains(programaListOldPrograma)) {
                    programaListOldPrograma.getEstudianteList().remove(estudiante);
                    programaListOldPrograma = em.merge(programaListOldPrograma);
                }
            }
            for (Programa programaListNewPrograma : programaListNew) {
                if (!programaListOld.contains(programaListNewPrograma)) {
                    programaListNewPrograma.getEstudianteList().add(estudiante);
                    programaListNewPrograma = em.merge(programaListNewPrograma);
                }
            }
            for (Nota notaListNewNota : notaListNew) {
                if (!notaListOld.contains(notaListNewNota)) {
                    Estudiante oldEstudianteOfNotaListNewNota = notaListNewNota.getEstudiante();
                    notaListNewNota.setEstudiante(estudiante);
                    notaListNewNota = em.merge(notaListNewNota);
                    if (oldEstudianteOfNotaListNewNota != null && !oldEstudianteOfNotaListNewNota.equals(estudiante)) {
                        oldEstudianteOfNotaListNewNota.getNotaList().remove(notaListNewNota);
                        oldEstudianteOfNotaListNewNota = em.merge(oldEstudianteOfNotaListNewNota);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = estudiante.getPerIdentificacion();
                if (findEstudiante(id) == null) {
                    throw new NonexistentEntityException("The estudiante with id " + id + " no longer exists.");
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
            Estudiante estudiante;
            try {
                estudiante = em.getReference(Estudiante.class, id);
                estudiante.getPerIdentificacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estudiante with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Nota> notaListOrphanCheck = estudiante.getNotaList();
            for (Nota notaListOrphanCheckNota : notaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estudiante (" + estudiante + ") cannot be destroyed since the Nota " + notaListOrphanCheckNota + " in its notaList field has a non-nullable estudiante field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Persona persona = estudiante.getPersona();
            if (persona != null) {
                persona.setEstudiante(null);
                persona = em.merge(persona);
            }
            List<Programa> programaList = estudiante.getProgramaList();
            for (Programa programaListPrograma : programaList) {
                programaListPrograma.getEstudianteList().remove(estudiante);
                programaListPrograma = em.merge(programaListPrograma);
            }
            em.remove(estudiante);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estudiante> findEstudianteEntities() {
        return findEstudianteEntities(true, -1, -1);
    }

    public List<Estudiante> findEstudianteEntities(int maxResults, int firstResult) {
        return findEstudianteEntities(false, maxResults, firstResult);
    }

    private List<Estudiante> findEstudianteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estudiante.class));
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

    public Estudiante findEstudiante(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estudiante.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstudianteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estudiante> rt = cq.from(Estudiante.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
