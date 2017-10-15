/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import entidades.Grupo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.MateriaGrupoProfesor;
import java.util.ArrayList;
import java.util.List;
import entidades.Horario;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class GrupoJpaController implements Serializable {

    public GrupoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Grupo grupo) throws PreexistingEntityException, Exception {
        if (grupo.getMateriaGrupoProfesorList() == null) {
            grupo.setMateriaGrupoProfesorList(new ArrayList<MateriaGrupoProfesor>());
        }
        if (grupo.getHorarioList() == null) {
            grupo.setHorarioList(new ArrayList<Horario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MateriaGrupoProfesor> attachedMateriaGrupoProfesorList = new ArrayList<MateriaGrupoProfesor>();
            for (MateriaGrupoProfesor materiaGrupoProfesorListMateriaGrupoProfesorToAttach : grupo.getMateriaGrupoProfesorList()) {
                materiaGrupoProfesorListMateriaGrupoProfesorToAttach = em.getReference(materiaGrupoProfesorListMateriaGrupoProfesorToAttach.getClass(), materiaGrupoProfesorListMateriaGrupoProfesorToAttach.getMateriaGrupoProfesorPK());
                attachedMateriaGrupoProfesorList.add(materiaGrupoProfesorListMateriaGrupoProfesorToAttach);
            }
            grupo.setMateriaGrupoProfesorList(attachedMateriaGrupoProfesorList);
            List<Horario> attachedHorarioList = new ArrayList<Horario>();
            for (Horario horarioListHorarioToAttach : grupo.getHorarioList()) {
                horarioListHorarioToAttach = em.getReference(horarioListHorarioToAttach.getClass(), horarioListHorarioToAttach.getHorarioPK());
                attachedHorarioList.add(horarioListHorarioToAttach);
            }
            grupo.setHorarioList(attachedHorarioList);
            em.persist(grupo);
            for (MateriaGrupoProfesor materiaGrupoProfesorListMateriaGrupoProfesor : grupo.getMateriaGrupoProfesorList()) {
                Grupo oldGrupoOfMateriaGrupoProfesorListMateriaGrupoProfesor = materiaGrupoProfesorListMateriaGrupoProfesor.getGrupo();
                materiaGrupoProfesorListMateriaGrupoProfesor.setGrupo(grupo);
                materiaGrupoProfesorListMateriaGrupoProfesor = em.merge(materiaGrupoProfesorListMateriaGrupoProfesor);
                if (oldGrupoOfMateriaGrupoProfesorListMateriaGrupoProfesor != null) {
                    oldGrupoOfMateriaGrupoProfesorListMateriaGrupoProfesor.getMateriaGrupoProfesorList().remove(materiaGrupoProfesorListMateriaGrupoProfesor);
                    oldGrupoOfMateriaGrupoProfesorListMateriaGrupoProfesor = em.merge(oldGrupoOfMateriaGrupoProfesorListMateriaGrupoProfesor);
                }
            }
            for (Horario horarioListHorario : grupo.getHorarioList()) {
                Grupo oldGruSeccionOfHorarioListHorario = horarioListHorario.getGruSeccion();
                horarioListHorario.setGruSeccion(grupo);
                horarioListHorario = em.merge(horarioListHorario);
                if (oldGruSeccionOfHorarioListHorario != null) {
                    oldGruSeccionOfHorarioListHorario.getHorarioList().remove(horarioListHorario);
                    oldGruSeccionOfHorarioListHorario = em.merge(oldGruSeccionOfHorarioListHorario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findGrupo(grupo.getGruSeccion()) != null) {
                throw new PreexistingEntityException("Grupo " + grupo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Grupo grupo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupo persistentGrupo = em.find(Grupo.class, grupo.getGruSeccion());
            List<MateriaGrupoProfesor> materiaGrupoProfesorListOld = persistentGrupo.getMateriaGrupoProfesorList();
            List<MateriaGrupoProfesor> materiaGrupoProfesorListNew = grupo.getMateriaGrupoProfesorList();
            List<Horario> horarioListOld = persistentGrupo.getHorarioList();
            List<Horario> horarioListNew = grupo.getHorarioList();
            List<String> illegalOrphanMessages = null;
            for (MateriaGrupoProfesor materiaGrupoProfesorListOldMateriaGrupoProfesor : materiaGrupoProfesorListOld) {
                if (!materiaGrupoProfesorListNew.contains(materiaGrupoProfesorListOldMateriaGrupoProfesor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MateriaGrupoProfesor " + materiaGrupoProfesorListOldMateriaGrupoProfesor + " since its grupo field is not nullable.");
                }
            }
            for (Horario horarioListOldHorario : horarioListOld) {
                if (!horarioListNew.contains(horarioListOldHorario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Horario " + horarioListOldHorario + " since its gruSeccion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<MateriaGrupoProfesor> attachedMateriaGrupoProfesorListNew = new ArrayList<MateriaGrupoProfesor>();
            for (MateriaGrupoProfesor materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach : materiaGrupoProfesorListNew) {
                materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach = em.getReference(materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach.getClass(), materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach.getMateriaGrupoProfesorPK());
                attachedMateriaGrupoProfesorListNew.add(materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach);
            }
            materiaGrupoProfesorListNew = attachedMateriaGrupoProfesorListNew;
            grupo.setMateriaGrupoProfesorList(materiaGrupoProfesorListNew);
            List<Horario> attachedHorarioListNew = new ArrayList<Horario>();
            for (Horario horarioListNewHorarioToAttach : horarioListNew) {
                horarioListNewHorarioToAttach = em.getReference(horarioListNewHorarioToAttach.getClass(), horarioListNewHorarioToAttach.getHorarioPK());
                attachedHorarioListNew.add(horarioListNewHorarioToAttach);
            }
            horarioListNew = attachedHorarioListNew;
            grupo.setHorarioList(horarioListNew);
            grupo = em.merge(grupo);
            for (MateriaGrupoProfesor materiaGrupoProfesorListNewMateriaGrupoProfesor : materiaGrupoProfesorListNew) {
                if (!materiaGrupoProfesorListOld.contains(materiaGrupoProfesorListNewMateriaGrupoProfesor)) {
                    Grupo oldGrupoOfMateriaGrupoProfesorListNewMateriaGrupoProfesor = materiaGrupoProfesorListNewMateriaGrupoProfesor.getGrupo();
                    materiaGrupoProfesorListNewMateriaGrupoProfesor.setGrupo(grupo);
                    materiaGrupoProfesorListNewMateriaGrupoProfesor = em.merge(materiaGrupoProfesorListNewMateriaGrupoProfesor);
                    if (oldGrupoOfMateriaGrupoProfesorListNewMateriaGrupoProfesor != null && !oldGrupoOfMateriaGrupoProfesorListNewMateriaGrupoProfesor.equals(grupo)) {
                        oldGrupoOfMateriaGrupoProfesorListNewMateriaGrupoProfesor.getMateriaGrupoProfesorList().remove(materiaGrupoProfesorListNewMateriaGrupoProfesor);
                        oldGrupoOfMateriaGrupoProfesorListNewMateriaGrupoProfesor = em.merge(oldGrupoOfMateriaGrupoProfesorListNewMateriaGrupoProfesor);
                    }
                }
            }
            for (Horario horarioListNewHorario : horarioListNew) {
                if (!horarioListOld.contains(horarioListNewHorario)) {
                    Grupo oldGruSeccionOfHorarioListNewHorario = horarioListNewHorario.getGruSeccion();
                    horarioListNewHorario.setGruSeccion(grupo);
                    horarioListNewHorario = em.merge(horarioListNewHorario);
                    if (oldGruSeccionOfHorarioListNewHorario != null && !oldGruSeccionOfHorarioListNewHorario.equals(grupo)) {
                        oldGruSeccionOfHorarioListNewHorario.getHorarioList().remove(horarioListNewHorario);
                        oldGruSeccionOfHorarioListNewHorario = em.merge(oldGruSeccionOfHorarioListNewHorario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = grupo.getGruSeccion();
                if (findGrupo(id) == null) {
                    throw new NonexistentEntityException("The grupo with id " + id + " no longer exists.");
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
            Grupo grupo;
            try {
                grupo = em.getReference(Grupo.class, id);
                grupo.getGruSeccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The grupo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<MateriaGrupoProfesor> materiaGrupoProfesorListOrphanCheck = grupo.getMateriaGrupoProfesorList();
            for (MateriaGrupoProfesor materiaGrupoProfesorListOrphanCheckMateriaGrupoProfesor : materiaGrupoProfesorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grupo (" + grupo + ") cannot be destroyed since the MateriaGrupoProfesor " + materiaGrupoProfesorListOrphanCheckMateriaGrupoProfesor + " in its materiaGrupoProfesorList field has a non-nullable grupo field.");
            }
            List<Horario> horarioListOrphanCheck = grupo.getHorarioList();
            for (Horario horarioListOrphanCheckHorario : horarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grupo (" + grupo + ") cannot be destroyed since the Horario " + horarioListOrphanCheckHorario + " in its horarioList field has a non-nullable gruSeccion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(grupo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Grupo> findGrupoEntities() {
        return findGrupoEntities(true, -1, -1);
    }

    public List<Grupo> findGrupoEntities(int maxResults, int firstResult) {
        return findGrupoEntities(false, maxResults, firstResult);
    }

    private List<Grupo> findGrupoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Grupo.class));
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

    public Grupo findGrupo(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Grupo.class, id);
        } finally {
            em.close();
        }
    }

    public int getGrupoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Grupo> rt = cq.from(Grupo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
