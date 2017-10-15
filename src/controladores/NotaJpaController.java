/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Estudiante;
import entidades.MateriaGrupoProfesor;
import entidades.Nota;
import entidades.NotaPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class NotaJpaController implements Serializable {

    public NotaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Nota nota) throws PreexistingEntityException, Exception {
        if (nota.getNotaPK() == null) {
            nota.setNotaPK(new NotaPK());
        }
        nota.getNotaPK().setPerIdentificacion(nota.getEstudiante().getPerIdentificacion());
        nota.getNotaPK().setMatCod(nota.getMateriaGrupoProfesor().getMateriaGrupoProfesorPK().getMatCod());
        nota.getNotaPK().setMatPerIdentificacion(nota.getMateriaGrupoProfesor().getMateriaGrupoProfesorPK().getPerIdentificacion());
        nota.getNotaPK().setPerAcaId(nota.getMateriaGrupoProfesor().getMateriaGrupoProfesorPK().getPerAcaId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estudiante estudiante = nota.getEstudiante();
            if (estudiante != null) {
                estudiante = em.getReference(estudiante.getClass(), estudiante.getPerIdentificacion());
                nota.setEstudiante(estudiante);
            }
            MateriaGrupoProfesor materiaGrupoProfesor = nota.getMateriaGrupoProfesor();
            if (materiaGrupoProfesor != null) {
                materiaGrupoProfesor = em.getReference(materiaGrupoProfesor.getClass(), materiaGrupoProfesor.getMateriaGrupoProfesorPK());
                nota.setMateriaGrupoProfesor(materiaGrupoProfesor);
            }
            em.persist(nota);
            if (estudiante != null) {
                estudiante.getNotaList().add(nota);
                estudiante = em.merge(estudiante);
            }
            if (materiaGrupoProfesor != null) {
                materiaGrupoProfesor.getNotaList().add(nota);
                materiaGrupoProfesor = em.merge(materiaGrupoProfesor);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findNota(nota.getNotaPK()) != null) {
                throw new PreexistingEntityException("Nota " + nota + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Nota nota) throws NonexistentEntityException, Exception {
        nota.getNotaPK().setPerIdentificacion(nota.getEstudiante().getPerIdentificacion());
        nota.getNotaPK().setMatCod(nota.getMateriaGrupoProfesor().getMateriaGrupoProfesorPK().getMatCod());
        nota.getNotaPK().setMatPerIdentificacion(nota.getMateriaGrupoProfesor().getMateriaGrupoProfesorPK().getPerIdentificacion());
        nota.getNotaPK().setPerAcaId(nota.getMateriaGrupoProfesor().getMateriaGrupoProfesorPK().getPerAcaId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Nota persistentNota = em.find(Nota.class, nota.getNotaPK());
            Estudiante estudianteOld = persistentNota.getEstudiante();
            Estudiante estudianteNew = nota.getEstudiante();
            MateriaGrupoProfesor materiaGrupoProfesorOld = persistentNota.getMateriaGrupoProfesor();
            MateriaGrupoProfesor materiaGrupoProfesorNew = nota.getMateriaGrupoProfesor();
            if (estudianteNew != null) {
                estudianteNew = em.getReference(estudianteNew.getClass(), estudianteNew.getPerIdentificacion());
                nota.setEstudiante(estudianteNew);
            }
            if (materiaGrupoProfesorNew != null) {
                materiaGrupoProfesorNew = em.getReference(materiaGrupoProfesorNew.getClass(), materiaGrupoProfesorNew.getMateriaGrupoProfesorPK());
                nota.setMateriaGrupoProfesor(materiaGrupoProfesorNew);
            }
            nota = em.merge(nota);
            if (estudianteOld != null && !estudianteOld.equals(estudianteNew)) {
                estudianteOld.getNotaList().remove(nota);
                estudianteOld = em.merge(estudianteOld);
            }
            if (estudianteNew != null && !estudianteNew.equals(estudianteOld)) {
                estudianteNew.getNotaList().add(nota);
                estudianteNew = em.merge(estudianteNew);
            }
            if (materiaGrupoProfesorOld != null && !materiaGrupoProfesorOld.equals(materiaGrupoProfesorNew)) {
                materiaGrupoProfesorOld.getNotaList().remove(nota);
                materiaGrupoProfesorOld = em.merge(materiaGrupoProfesorOld);
            }
            if (materiaGrupoProfesorNew != null && !materiaGrupoProfesorNew.equals(materiaGrupoProfesorOld)) {
                materiaGrupoProfesorNew.getNotaList().add(nota);
                materiaGrupoProfesorNew = em.merge(materiaGrupoProfesorNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                NotaPK id = nota.getNotaPK();
                if (findNota(id) == null) {
                    throw new NonexistentEntityException("The nota with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(NotaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Nota nota;
            try {
                nota = em.getReference(Nota.class, id);
                nota.getNotaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The nota with id " + id + " no longer exists.", enfe);
            }
            Estudiante estudiante = nota.getEstudiante();
            if (estudiante != null) {
                estudiante.getNotaList().remove(nota);
                estudiante = em.merge(estudiante);
            }
            MateriaGrupoProfesor materiaGrupoProfesor = nota.getMateriaGrupoProfesor();
            if (materiaGrupoProfesor != null) {
                materiaGrupoProfesor.getNotaList().remove(nota);
                materiaGrupoProfesor = em.merge(materiaGrupoProfesor);
            }
            em.remove(nota);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Nota> findNotaEntities() {
        return findNotaEntities(true, -1, -1);
    }

    public List<Nota> findNotaEntities(int maxResults, int firstResult) {
        return findNotaEntities(false, maxResults, firstResult);
    }

    private List<Nota> findNotaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Nota.class));
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

    public Nota findNota(NotaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Nota.class, id);
        } finally {
            em.close();
        }
    }

    public int getNotaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Nota> rt = cq.from(Nota.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
