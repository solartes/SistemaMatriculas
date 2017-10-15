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
import entidades.MateriaOfertada;
import java.util.ArrayList;
import java.util.List;
import entidades.Horario;
import entidades.PeriodoAcademico;
import java.math.BigDecimal;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class PeriodoAcademicoJpaController implements Serializable {

    public PeriodoAcademicoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PeriodoAcademico periodoAcademico) throws PreexistingEntityException, Exception {
        if (periodoAcademico.getMateriaOfertadaList() == null) {
            periodoAcademico.setMateriaOfertadaList(new ArrayList<MateriaOfertada>());
        }
        if (periodoAcademico.getHorarioList() == null) {
            periodoAcademico.setHorarioList(new ArrayList<Horario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MateriaOfertada> attachedMateriaOfertadaList = new ArrayList<MateriaOfertada>();
            for (MateriaOfertada materiaOfertadaListMateriaOfertadaToAttach : periodoAcademico.getMateriaOfertadaList()) {
                materiaOfertadaListMateriaOfertadaToAttach = em.getReference(materiaOfertadaListMateriaOfertadaToAttach.getClass(), materiaOfertadaListMateriaOfertadaToAttach.getMateriaOfertadaPK());
                attachedMateriaOfertadaList.add(materiaOfertadaListMateriaOfertadaToAttach);
            }
            periodoAcademico.setMateriaOfertadaList(attachedMateriaOfertadaList);
            List<Horario> attachedHorarioList = new ArrayList<Horario>();
            for (Horario horarioListHorarioToAttach : periodoAcademico.getHorarioList()) {
                horarioListHorarioToAttach = em.getReference(horarioListHorarioToAttach.getClass(), horarioListHorarioToAttach.getHorarioPK());
                attachedHorarioList.add(horarioListHorarioToAttach);
            }
            periodoAcademico.setHorarioList(attachedHorarioList);
            em.persist(periodoAcademico);
            for (MateriaOfertada materiaOfertadaListMateriaOfertada : periodoAcademico.getMateriaOfertadaList()) {
                PeriodoAcademico oldPeriodoAcademicoOfMateriaOfertadaListMateriaOfertada = materiaOfertadaListMateriaOfertada.getPeriodoAcademico();
                materiaOfertadaListMateriaOfertada.setPeriodoAcademico(periodoAcademico);
                materiaOfertadaListMateriaOfertada = em.merge(materiaOfertadaListMateriaOfertada);
                if (oldPeriodoAcademicoOfMateriaOfertadaListMateriaOfertada != null) {
                    oldPeriodoAcademicoOfMateriaOfertadaListMateriaOfertada.getMateriaOfertadaList().remove(materiaOfertadaListMateriaOfertada);
                    oldPeriodoAcademicoOfMateriaOfertadaListMateriaOfertada = em.merge(oldPeriodoAcademicoOfMateriaOfertadaListMateriaOfertada);
                }
            }
            for (Horario horarioListHorario : periodoAcademico.getHorarioList()) {
                PeriodoAcademico oldPeriodoAcademicoOfHorarioListHorario = horarioListHorario.getPeriodoAcademico();
                horarioListHorario.setPeriodoAcademico(periodoAcademico);
                horarioListHorario = em.merge(horarioListHorario);
                if (oldPeriodoAcademicoOfHorarioListHorario != null) {
                    oldPeriodoAcademicoOfHorarioListHorario.getHorarioList().remove(horarioListHorario);
                    oldPeriodoAcademicoOfHorarioListHorario = em.merge(oldPeriodoAcademicoOfHorarioListHorario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPeriodoAcademico(periodoAcademico.getPerAcaId()) != null) {
                throw new PreexistingEntityException("PeriodoAcademico " + periodoAcademico + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PeriodoAcademico periodoAcademico) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PeriodoAcademico persistentPeriodoAcademico = em.find(PeriodoAcademico.class, periodoAcademico.getPerAcaId());
            List<MateriaOfertada> materiaOfertadaListOld = persistentPeriodoAcademico.getMateriaOfertadaList();
            List<MateriaOfertada> materiaOfertadaListNew = periodoAcademico.getMateriaOfertadaList();
            List<Horario> horarioListOld = persistentPeriodoAcademico.getHorarioList();
            List<Horario> horarioListNew = periodoAcademico.getHorarioList();
            List<String> illegalOrphanMessages = null;
            for (MateriaOfertada materiaOfertadaListOldMateriaOfertada : materiaOfertadaListOld) {
                if (!materiaOfertadaListNew.contains(materiaOfertadaListOldMateriaOfertada)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MateriaOfertada " + materiaOfertadaListOldMateriaOfertada + " since its periodoAcademico field is not nullable.");
                }
            }
            for (Horario horarioListOldHorario : horarioListOld) {
                if (!horarioListNew.contains(horarioListOldHorario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Horario " + horarioListOldHorario + " since its periodoAcademico field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<MateriaOfertada> attachedMateriaOfertadaListNew = new ArrayList<MateriaOfertada>();
            for (MateriaOfertada materiaOfertadaListNewMateriaOfertadaToAttach : materiaOfertadaListNew) {
                materiaOfertadaListNewMateriaOfertadaToAttach = em.getReference(materiaOfertadaListNewMateriaOfertadaToAttach.getClass(), materiaOfertadaListNewMateriaOfertadaToAttach.getMateriaOfertadaPK());
                attachedMateriaOfertadaListNew.add(materiaOfertadaListNewMateriaOfertadaToAttach);
            }
            materiaOfertadaListNew = attachedMateriaOfertadaListNew;
            periodoAcademico.setMateriaOfertadaList(materiaOfertadaListNew);
            List<Horario> attachedHorarioListNew = new ArrayList<Horario>();
            for (Horario horarioListNewHorarioToAttach : horarioListNew) {
                horarioListNewHorarioToAttach = em.getReference(horarioListNewHorarioToAttach.getClass(), horarioListNewHorarioToAttach.getHorarioPK());
                attachedHorarioListNew.add(horarioListNewHorarioToAttach);
            }
            horarioListNew = attachedHorarioListNew;
            periodoAcademico.setHorarioList(horarioListNew);
            periodoAcademico = em.merge(periodoAcademico);
            for (MateriaOfertada materiaOfertadaListNewMateriaOfertada : materiaOfertadaListNew) {
                if (!materiaOfertadaListOld.contains(materiaOfertadaListNewMateriaOfertada)) {
                    PeriodoAcademico oldPeriodoAcademicoOfMateriaOfertadaListNewMateriaOfertada = materiaOfertadaListNewMateriaOfertada.getPeriodoAcademico();
                    materiaOfertadaListNewMateriaOfertada.setPeriodoAcademico(periodoAcademico);
                    materiaOfertadaListNewMateriaOfertada = em.merge(materiaOfertadaListNewMateriaOfertada);
                    if (oldPeriodoAcademicoOfMateriaOfertadaListNewMateriaOfertada != null && !oldPeriodoAcademicoOfMateriaOfertadaListNewMateriaOfertada.equals(periodoAcademico)) {
                        oldPeriodoAcademicoOfMateriaOfertadaListNewMateriaOfertada.getMateriaOfertadaList().remove(materiaOfertadaListNewMateriaOfertada);
                        oldPeriodoAcademicoOfMateriaOfertadaListNewMateriaOfertada = em.merge(oldPeriodoAcademicoOfMateriaOfertadaListNewMateriaOfertada);
                    }
                }
            }
            for (Horario horarioListNewHorario : horarioListNew) {
                if (!horarioListOld.contains(horarioListNewHorario)) {
                    PeriodoAcademico oldPeriodoAcademicoOfHorarioListNewHorario = horarioListNewHorario.getPeriodoAcademico();
                    horarioListNewHorario.setPeriodoAcademico(periodoAcademico);
                    horarioListNewHorario = em.merge(horarioListNewHorario);
                    if (oldPeriodoAcademicoOfHorarioListNewHorario != null && !oldPeriodoAcademicoOfHorarioListNewHorario.equals(periodoAcademico)) {
                        oldPeriodoAcademicoOfHorarioListNewHorario.getHorarioList().remove(horarioListNewHorario);
                        oldPeriodoAcademicoOfHorarioListNewHorario = em.merge(oldPeriodoAcademicoOfHorarioListNewHorario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = periodoAcademico.getPerAcaId();
                if (findPeriodoAcademico(id) == null) {
                    throw new NonexistentEntityException("The periodoAcademico with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PeriodoAcademico periodoAcademico;
            try {
                periodoAcademico = em.getReference(PeriodoAcademico.class, id);
                periodoAcademico.getPerAcaId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The periodoAcademico with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<MateriaOfertada> materiaOfertadaListOrphanCheck = periodoAcademico.getMateriaOfertadaList();
            for (MateriaOfertada materiaOfertadaListOrphanCheckMateriaOfertada : materiaOfertadaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This PeriodoAcademico (" + periodoAcademico + ") cannot be destroyed since the MateriaOfertada " + materiaOfertadaListOrphanCheckMateriaOfertada + " in its materiaOfertadaList field has a non-nullable periodoAcademico field.");
            }
            List<Horario> horarioListOrphanCheck = periodoAcademico.getHorarioList();
            for (Horario horarioListOrphanCheckHorario : horarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This PeriodoAcademico (" + periodoAcademico + ") cannot be destroyed since the Horario " + horarioListOrphanCheckHorario + " in its horarioList field has a non-nullable periodoAcademico field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(periodoAcademico);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PeriodoAcademico> findPeriodoAcademicoEntities() {
        return findPeriodoAcademicoEntities(true, -1, -1);
    }

    public List<PeriodoAcademico> findPeriodoAcademicoEntities(int maxResults, int firstResult) {
        return findPeriodoAcademicoEntities(false, maxResults, firstResult);
    }

    private List<PeriodoAcademico> findPeriodoAcademicoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PeriodoAcademico.class));
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

    public PeriodoAcademico findPeriodoAcademico(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PeriodoAcademico.class, id);
        } finally {
            em.close();
        }
    }

    public int getPeriodoAcademicoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PeriodoAcademico> rt = cq.from(PeriodoAcademico.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
