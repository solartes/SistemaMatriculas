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
import entidades.Materia;
import entidades.PeriodoAcademico;
import entidades.MateriaGrupoProfesor;
import entidades.MateriaOfertada;
import entidades.MateriaOfertadaPK;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class MateriaOfertadaJpaController implements Serializable {

    public MateriaOfertadaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MateriaOfertada materiaOfertada) throws PreexistingEntityException, Exception {
        if (materiaOfertada.getMateriaOfertadaPK() == null) {
            materiaOfertada.setMateriaOfertadaPK(new MateriaOfertadaPK());
        }
        if (materiaOfertada.getMateriaGrupoProfesorList() == null) {
            materiaOfertada.setMateriaGrupoProfesorList(new ArrayList<MateriaGrupoProfesor>());
        }
        materiaOfertada.getMateriaOfertadaPK().setPerAcaId(materiaOfertada.getPeriodoAcademico().getPerAcaId());
        materiaOfertada.getMateriaOfertadaPK().setMatCod(materiaOfertada.getMateria().getMatCod());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Materia materia = materiaOfertada.getMateria();
            if (materia != null) {
                materia = em.getReference(materia.getClass(), materia.getMatCod());
                materiaOfertada.setMateria(materia);
            }
            PeriodoAcademico periodoAcademico = materiaOfertada.getPeriodoAcademico();
            if (periodoAcademico != null) {
                periodoAcademico = em.getReference(periodoAcademico.getClass(), periodoAcademico.getPerAcaId());
                materiaOfertada.setPeriodoAcademico(periodoAcademico);
            }
            List<MateriaGrupoProfesor> attachedMateriaGrupoProfesorList = new ArrayList<MateriaGrupoProfesor>();
            for (MateriaGrupoProfesor materiaGrupoProfesorListMateriaGrupoProfesorToAttach : materiaOfertada.getMateriaGrupoProfesorList()) {
                materiaGrupoProfesorListMateriaGrupoProfesorToAttach = em.getReference(materiaGrupoProfesorListMateriaGrupoProfesorToAttach.getClass(), materiaGrupoProfesorListMateriaGrupoProfesorToAttach.getMateriaGrupoProfesorPK());
                attachedMateriaGrupoProfesorList.add(materiaGrupoProfesorListMateriaGrupoProfesorToAttach);
            }
            materiaOfertada.setMateriaGrupoProfesorList(attachedMateriaGrupoProfesorList);
            em.persist(materiaOfertada);
            if (materia != null) {
                materia.getMateriaOfertadaList().add(materiaOfertada);
                materia = em.merge(materia);
            }
            if (periodoAcademico != null) {
                periodoAcademico.getMateriaOfertadaList().add(materiaOfertada);
                periodoAcademico = em.merge(periodoAcademico);
            }
            for (MateriaGrupoProfesor materiaGrupoProfesorListMateriaGrupoProfesor : materiaOfertada.getMateriaGrupoProfesorList()) {
                MateriaOfertada oldMateriaOfertadaOfMateriaGrupoProfesorListMateriaGrupoProfesor = materiaGrupoProfesorListMateriaGrupoProfesor.getMateriaOfertada();
                materiaGrupoProfesorListMateriaGrupoProfesor.setMateriaOfertada(materiaOfertada);
                materiaGrupoProfesorListMateriaGrupoProfesor = em.merge(materiaGrupoProfesorListMateriaGrupoProfesor);
                if (oldMateriaOfertadaOfMateriaGrupoProfesorListMateriaGrupoProfesor != null) {
                    oldMateriaOfertadaOfMateriaGrupoProfesorListMateriaGrupoProfesor.getMateriaGrupoProfesorList().remove(materiaGrupoProfesorListMateriaGrupoProfesor);
                    oldMateriaOfertadaOfMateriaGrupoProfesorListMateriaGrupoProfesor = em.merge(oldMateriaOfertadaOfMateriaGrupoProfesorListMateriaGrupoProfesor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMateriaOfertada(materiaOfertada.getMateriaOfertadaPK()) != null) {
                throw new PreexistingEntityException("MateriaOfertada " + materiaOfertada + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MateriaOfertada materiaOfertada) throws IllegalOrphanException, NonexistentEntityException, Exception {
        materiaOfertada.getMateriaOfertadaPK().setPerAcaId(materiaOfertada.getPeriodoAcademico().getPerAcaId());
        materiaOfertada.getMateriaOfertadaPK().setMatCod(materiaOfertada.getMateria().getMatCod());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MateriaOfertada persistentMateriaOfertada = em.find(MateriaOfertada.class, materiaOfertada.getMateriaOfertadaPK());
            Materia materiaOld = persistentMateriaOfertada.getMateria();
            Materia materiaNew = materiaOfertada.getMateria();
            PeriodoAcademico periodoAcademicoOld = persistentMateriaOfertada.getPeriodoAcademico();
            PeriodoAcademico periodoAcademicoNew = materiaOfertada.getPeriodoAcademico();
            List<MateriaGrupoProfesor> materiaGrupoProfesorListOld = persistentMateriaOfertada.getMateriaGrupoProfesorList();
            List<MateriaGrupoProfesor> materiaGrupoProfesorListNew = materiaOfertada.getMateriaGrupoProfesorList();
            List<String> illegalOrphanMessages = null;
            for (MateriaGrupoProfesor materiaGrupoProfesorListOldMateriaGrupoProfesor : materiaGrupoProfesorListOld) {
                if (!materiaGrupoProfesorListNew.contains(materiaGrupoProfesorListOldMateriaGrupoProfesor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MateriaGrupoProfesor " + materiaGrupoProfesorListOldMateriaGrupoProfesor + " since its materiaOfertada field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (materiaNew != null) {
                materiaNew = em.getReference(materiaNew.getClass(), materiaNew.getMatCod());
                materiaOfertada.setMateria(materiaNew);
            }
            if (periodoAcademicoNew != null) {
                periodoAcademicoNew = em.getReference(periodoAcademicoNew.getClass(), periodoAcademicoNew.getPerAcaId());
                materiaOfertada.setPeriodoAcademico(periodoAcademicoNew);
            }
            List<MateriaGrupoProfesor> attachedMateriaGrupoProfesorListNew = new ArrayList<MateriaGrupoProfesor>();
            for (MateriaGrupoProfesor materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach : materiaGrupoProfesorListNew) {
                materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach = em.getReference(materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach.getClass(), materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach.getMateriaGrupoProfesorPK());
                attachedMateriaGrupoProfesorListNew.add(materiaGrupoProfesorListNewMateriaGrupoProfesorToAttach);
            }
            materiaGrupoProfesorListNew = attachedMateriaGrupoProfesorListNew;
            materiaOfertada.setMateriaGrupoProfesorList(materiaGrupoProfesorListNew);
            materiaOfertada = em.merge(materiaOfertada);
            if (materiaOld != null && !materiaOld.equals(materiaNew)) {
                materiaOld.getMateriaOfertadaList().remove(materiaOfertada);
                materiaOld = em.merge(materiaOld);
            }
            if (materiaNew != null && !materiaNew.equals(materiaOld)) {
                materiaNew.getMateriaOfertadaList().add(materiaOfertada);
                materiaNew = em.merge(materiaNew);
            }
            if (periodoAcademicoOld != null && !periodoAcademicoOld.equals(periodoAcademicoNew)) {
                periodoAcademicoOld.getMateriaOfertadaList().remove(materiaOfertada);
                periodoAcademicoOld = em.merge(periodoAcademicoOld);
            }
            if (periodoAcademicoNew != null && !periodoAcademicoNew.equals(periodoAcademicoOld)) {
                periodoAcademicoNew.getMateriaOfertadaList().add(materiaOfertada);
                periodoAcademicoNew = em.merge(periodoAcademicoNew);
            }
            for (MateriaGrupoProfesor materiaGrupoProfesorListNewMateriaGrupoProfesor : materiaGrupoProfesorListNew) {
                if (!materiaGrupoProfesorListOld.contains(materiaGrupoProfesorListNewMateriaGrupoProfesor)) {
                    MateriaOfertada oldMateriaOfertadaOfMateriaGrupoProfesorListNewMateriaGrupoProfesor = materiaGrupoProfesorListNewMateriaGrupoProfesor.getMateriaOfertada();
                    materiaGrupoProfesorListNewMateriaGrupoProfesor.setMateriaOfertada(materiaOfertada);
                    materiaGrupoProfesorListNewMateriaGrupoProfesor = em.merge(materiaGrupoProfesorListNewMateriaGrupoProfesor);
                    if (oldMateriaOfertadaOfMateriaGrupoProfesorListNewMateriaGrupoProfesor != null && !oldMateriaOfertadaOfMateriaGrupoProfesorListNewMateriaGrupoProfesor.equals(materiaOfertada)) {
                        oldMateriaOfertadaOfMateriaGrupoProfesorListNewMateriaGrupoProfesor.getMateriaGrupoProfesorList().remove(materiaGrupoProfesorListNewMateriaGrupoProfesor);
                        oldMateriaOfertadaOfMateriaGrupoProfesorListNewMateriaGrupoProfesor = em.merge(oldMateriaOfertadaOfMateriaGrupoProfesorListNewMateriaGrupoProfesor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                MateriaOfertadaPK id = materiaOfertada.getMateriaOfertadaPK();
                if (findMateriaOfertada(id) == null) {
                    throw new NonexistentEntityException("The materiaOfertada with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(MateriaOfertadaPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MateriaOfertada materiaOfertada;
            try {
                materiaOfertada = em.getReference(MateriaOfertada.class, id);
                materiaOfertada.getMateriaOfertadaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The materiaOfertada with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<MateriaGrupoProfesor> materiaGrupoProfesorListOrphanCheck = materiaOfertada.getMateriaGrupoProfesorList();
            for (MateriaGrupoProfesor materiaGrupoProfesorListOrphanCheckMateriaGrupoProfesor : materiaGrupoProfesorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This MateriaOfertada (" + materiaOfertada + ") cannot be destroyed since the MateriaGrupoProfesor " + materiaGrupoProfesorListOrphanCheckMateriaGrupoProfesor + " in its materiaGrupoProfesorList field has a non-nullable materiaOfertada field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Materia materia = materiaOfertada.getMateria();
            if (materia != null) {
                materia.getMateriaOfertadaList().remove(materiaOfertada);
                materia = em.merge(materia);
            }
            PeriodoAcademico periodoAcademico = materiaOfertada.getPeriodoAcademico();
            if (periodoAcademico != null) {
                periodoAcademico.getMateriaOfertadaList().remove(materiaOfertada);
                periodoAcademico = em.merge(periodoAcademico);
            }
            em.remove(materiaOfertada);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MateriaOfertada> findMateriaOfertadaEntities() {
        return findMateriaOfertadaEntities(true, -1, -1);
    }

    public List<MateriaOfertada> findMateriaOfertadaEntities(int maxResults, int firstResult) {
        return findMateriaOfertadaEntities(false, maxResults, firstResult);
    }

    private List<MateriaOfertada> findMateriaOfertadaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MateriaOfertada.class));
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

    public MateriaOfertada findMateriaOfertada(MateriaOfertadaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MateriaOfertada.class, id);
        } finally {
            em.close();
        }
    }

    public int getMateriaOfertadaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MateriaOfertada> rt = cq.from(MateriaOfertada.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
