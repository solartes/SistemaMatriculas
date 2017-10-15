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
import entidades.Facultad;
import entidades.Estudiante;
import java.util.ArrayList;
import java.util.List;
import entidades.Materia;
import entidades.Programa;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class ProgramaJpaController implements Serializable {

    public ProgramaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Programa programa) throws PreexistingEntityException, Exception {
        if (programa.getEstudianteList() == null) {
            programa.setEstudianteList(new ArrayList<Estudiante>());
        }
        if (programa.getMateriaList() == null) {
            programa.setMateriaList(new ArrayList<Materia>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facultad facId = programa.getFacId();
            if (facId != null) {
                facId = em.getReference(facId.getClass(), facId.getFacId());
                programa.setFacId(facId);
            }
            List<Estudiante> attachedEstudianteList = new ArrayList<Estudiante>();
            for (Estudiante estudianteListEstudianteToAttach : programa.getEstudianteList()) {
                estudianteListEstudianteToAttach = em.getReference(estudianteListEstudianteToAttach.getClass(), estudianteListEstudianteToAttach.getPerIdentificacion());
                attachedEstudianteList.add(estudianteListEstudianteToAttach);
            }
            programa.setEstudianteList(attachedEstudianteList);
            List<Materia> attachedMateriaList = new ArrayList<Materia>();
            for (Materia materiaListMateriaToAttach : programa.getMateriaList()) {
                materiaListMateriaToAttach = em.getReference(materiaListMateriaToAttach.getClass(), materiaListMateriaToAttach.getMatCod());
                attachedMateriaList.add(materiaListMateriaToAttach);
            }
            programa.setMateriaList(attachedMateriaList);
            em.persist(programa);
            if (facId != null) {
                facId.getProgramaList().add(programa);
                facId = em.merge(facId);
            }
            for (Estudiante estudianteListEstudiante : programa.getEstudianteList()) {
                estudianteListEstudiante.getProgramaList().add(programa);
                estudianteListEstudiante = em.merge(estudianteListEstudiante);
            }
            for (Materia materiaListMateria : programa.getMateriaList()) {
                Programa oldPrgIdOfMateriaListMateria = materiaListMateria.getPrgId();
                materiaListMateria.setPrgId(programa);
                materiaListMateria = em.merge(materiaListMateria);
                if (oldPrgIdOfMateriaListMateria != null) {
                    oldPrgIdOfMateriaListMateria.getMateriaList().remove(materiaListMateria);
                    oldPrgIdOfMateriaListMateria = em.merge(oldPrgIdOfMateriaListMateria);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPrograma(programa.getPrgId()) != null) {
                throw new PreexistingEntityException("Programa " + programa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Programa programa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Programa persistentPrograma = em.find(Programa.class, programa.getPrgId());
            Facultad facIdOld = persistentPrograma.getFacId();
            Facultad facIdNew = programa.getFacId();
            List<Estudiante> estudianteListOld = persistentPrograma.getEstudianteList();
            List<Estudiante> estudianteListNew = programa.getEstudianteList();
            List<Materia> materiaListOld = persistentPrograma.getMateriaList();
            List<Materia> materiaListNew = programa.getMateriaList();
            List<String> illegalOrphanMessages = null;
            for (Materia materiaListOldMateria : materiaListOld) {
                if (!materiaListNew.contains(materiaListOldMateria)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Materia " + materiaListOldMateria + " since its prgId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (facIdNew != null) {
                facIdNew = em.getReference(facIdNew.getClass(), facIdNew.getFacId());
                programa.setFacId(facIdNew);
            }
            List<Estudiante> attachedEstudianteListNew = new ArrayList<Estudiante>();
            for (Estudiante estudianteListNewEstudianteToAttach : estudianteListNew) {
                estudianteListNewEstudianteToAttach = em.getReference(estudianteListNewEstudianteToAttach.getClass(), estudianteListNewEstudianteToAttach.getPerIdentificacion());
                attachedEstudianteListNew.add(estudianteListNewEstudianteToAttach);
            }
            estudianteListNew = attachedEstudianteListNew;
            programa.setEstudianteList(estudianteListNew);
            List<Materia> attachedMateriaListNew = new ArrayList<Materia>();
            for (Materia materiaListNewMateriaToAttach : materiaListNew) {
                materiaListNewMateriaToAttach = em.getReference(materiaListNewMateriaToAttach.getClass(), materiaListNewMateriaToAttach.getMatCod());
                attachedMateriaListNew.add(materiaListNewMateriaToAttach);
            }
            materiaListNew = attachedMateriaListNew;
            programa.setMateriaList(materiaListNew);
            programa = em.merge(programa);
            if (facIdOld != null && !facIdOld.equals(facIdNew)) {
                facIdOld.getProgramaList().remove(programa);
                facIdOld = em.merge(facIdOld);
            }
            if (facIdNew != null && !facIdNew.equals(facIdOld)) {
                facIdNew.getProgramaList().add(programa);
                facIdNew = em.merge(facIdNew);
            }
            for (Estudiante estudianteListOldEstudiante : estudianteListOld) {
                if (!estudianteListNew.contains(estudianteListOldEstudiante)) {
                    estudianteListOldEstudiante.getProgramaList().remove(programa);
                    estudianteListOldEstudiante = em.merge(estudianteListOldEstudiante);
                }
            }
            for (Estudiante estudianteListNewEstudiante : estudianteListNew) {
                if (!estudianteListOld.contains(estudianteListNewEstudiante)) {
                    estudianteListNewEstudiante.getProgramaList().add(programa);
                    estudianteListNewEstudiante = em.merge(estudianteListNewEstudiante);
                }
            }
            for (Materia materiaListNewMateria : materiaListNew) {
                if (!materiaListOld.contains(materiaListNewMateria)) {
                    Programa oldPrgIdOfMateriaListNewMateria = materiaListNewMateria.getPrgId();
                    materiaListNewMateria.setPrgId(programa);
                    materiaListNewMateria = em.merge(materiaListNewMateria);
                    if (oldPrgIdOfMateriaListNewMateria != null && !oldPrgIdOfMateriaListNewMateria.equals(programa)) {
                        oldPrgIdOfMateriaListNewMateria.getMateriaList().remove(materiaListNewMateria);
                        oldPrgIdOfMateriaListNewMateria = em.merge(oldPrgIdOfMateriaListNewMateria);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = programa.getPrgId();
                if (findPrograma(id) == null) {
                    throw new NonexistentEntityException("The programa with id " + id + " no longer exists.");
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
            Programa programa;
            try {
                programa = em.getReference(Programa.class, id);
                programa.getPrgId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The programa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Materia> materiaListOrphanCheck = programa.getMateriaList();
            for (Materia materiaListOrphanCheckMateria : materiaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Programa (" + programa + ") cannot be destroyed since the Materia " + materiaListOrphanCheckMateria + " in its materiaList field has a non-nullable prgId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Facultad facId = programa.getFacId();
            if (facId != null) {
                facId.getProgramaList().remove(programa);
                facId = em.merge(facId);
            }
            List<Estudiante> estudianteList = programa.getEstudianteList();
            for (Estudiante estudianteListEstudiante : estudianteList) {
                estudianteListEstudiante.getProgramaList().remove(programa);
                estudianteListEstudiante = em.merge(estudianteListEstudiante);
            }
            em.remove(programa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Programa> findProgramaEntities() {
        return findProgramaEntities(true, -1, -1);
    }

    public List<Programa> findProgramaEntities(int maxResults, int firstResult) {
        return findProgramaEntities(false, maxResults, firstResult);
    }

    private List<Programa> findProgramaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Programa.class));
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

    public Programa findPrograma(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Programa.class, id);
        } finally {
            em.close();
        }
    }

    public int getProgramaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Programa> rt = cq.from(Programa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
