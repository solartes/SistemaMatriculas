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
import entidades.Grupo;
import entidades.Horario;
import entidades.HorarioPK;
import entidades.PeriodoAcademico;
import entidades.Salon;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class HorarioJpaController implements Serializable {

    public HorarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Horario horario) throws PreexistingEntityException, Exception {
        if (horario.getHorarioPK() == null) {
            horario.setHorarioPK(new HorarioPK());
        }
        horario.getHorarioPK().setPerAcaId(horario.getPeriodoAcademico().getPerAcaId());
        horario.getHorarioPK().setSalId(horario.getSalon().getSalId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupo gruSeccion = horario.getGruSeccion();
            if (gruSeccion != null) {
                gruSeccion = em.getReference(gruSeccion.getClass(), gruSeccion.getGruSeccion());
                horario.setGruSeccion(gruSeccion);
            }
            PeriodoAcademico periodoAcademico = horario.getPeriodoAcademico();
            if (periodoAcademico != null) {
                periodoAcademico = em.getReference(periodoAcademico.getClass(), periodoAcademico.getPerAcaId());
                horario.setPeriodoAcademico(periodoAcademico);
            }
            Salon salon = horario.getSalon();
            if (salon != null) {
                salon = em.getReference(salon.getClass(), salon.getSalId());
                horario.setSalon(salon);
            }
            em.persist(horario);
            if (gruSeccion != null) {
                gruSeccion.getHorarioList().add(horario);
                gruSeccion = em.merge(gruSeccion);
            }
            if (periodoAcademico != null) {
                periodoAcademico.getHorarioList().add(horario);
                periodoAcademico = em.merge(periodoAcademico);
            }
            if (salon != null) {
                salon.getHorarioList().add(horario);
                salon = em.merge(salon);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findHorario(horario.getHorarioPK()) != null) {
                throw new PreexistingEntityException("Horario " + horario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Horario horario) throws NonexistentEntityException, Exception {
        horario.getHorarioPK().setPerAcaId(horario.getPeriodoAcademico().getPerAcaId());
        horario.getHorarioPK().setSalId(horario.getSalon().getSalId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Horario persistentHorario = em.find(Horario.class, horario.getHorarioPK());
            Grupo gruSeccionOld = persistentHorario.getGruSeccion();
            Grupo gruSeccionNew = horario.getGruSeccion();
            PeriodoAcademico periodoAcademicoOld = persistentHorario.getPeriodoAcademico();
            PeriodoAcademico periodoAcademicoNew = horario.getPeriodoAcademico();
            Salon salonOld = persistentHorario.getSalon();
            Salon salonNew = horario.getSalon();
            if (gruSeccionNew != null) {
                gruSeccionNew = em.getReference(gruSeccionNew.getClass(), gruSeccionNew.getGruSeccion());
                horario.setGruSeccion(gruSeccionNew);
            }
            if (periodoAcademicoNew != null) {
                periodoAcademicoNew = em.getReference(periodoAcademicoNew.getClass(), periodoAcademicoNew.getPerAcaId());
                horario.setPeriodoAcademico(periodoAcademicoNew);
            }
            if (salonNew != null) {
                salonNew = em.getReference(salonNew.getClass(), salonNew.getSalId());
                horario.setSalon(salonNew);
            }
            horario = em.merge(horario);
            if (gruSeccionOld != null && !gruSeccionOld.equals(gruSeccionNew)) {
                gruSeccionOld.getHorarioList().remove(horario);
                gruSeccionOld = em.merge(gruSeccionOld);
            }
            if (gruSeccionNew != null && !gruSeccionNew.equals(gruSeccionOld)) {
                gruSeccionNew.getHorarioList().add(horario);
                gruSeccionNew = em.merge(gruSeccionNew);
            }
            if (periodoAcademicoOld != null && !periodoAcademicoOld.equals(periodoAcademicoNew)) {
                periodoAcademicoOld.getHorarioList().remove(horario);
                periodoAcademicoOld = em.merge(periodoAcademicoOld);
            }
            if (periodoAcademicoNew != null && !periodoAcademicoNew.equals(periodoAcademicoOld)) {
                periodoAcademicoNew.getHorarioList().add(horario);
                periodoAcademicoNew = em.merge(periodoAcademicoNew);
            }
            if (salonOld != null && !salonOld.equals(salonNew)) {
                salonOld.getHorarioList().remove(horario);
                salonOld = em.merge(salonOld);
            }
            if (salonNew != null && !salonNew.equals(salonOld)) {
                salonNew.getHorarioList().add(horario);
                salonNew = em.merge(salonNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                HorarioPK id = horario.getHorarioPK();
                if (findHorario(id) == null) {
                    throw new NonexistentEntityException("The horario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(HorarioPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Horario horario;
            try {
                horario = em.getReference(Horario.class, id);
                horario.getHorarioPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The horario with id " + id + " no longer exists.", enfe);
            }
            Grupo gruSeccion = horario.getGruSeccion();
            if (gruSeccion != null) {
                gruSeccion.getHorarioList().remove(horario);
                gruSeccion = em.merge(gruSeccion);
            }
            PeriodoAcademico periodoAcademico = horario.getPeriodoAcademico();
            if (periodoAcademico != null) {
                periodoAcademico.getHorarioList().remove(horario);
                periodoAcademico = em.merge(periodoAcademico);
            }
            Salon salon = horario.getSalon();
            if (salon != null) {
                salon.getHorarioList().remove(horario);
                salon = em.merge(salon);
            }
            em.remove(horario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Horario> findHorarioEntities() {
        return findHorarioEntities(true, -1, -1);
    }

    public List<Horario> findHorarioEntities(int maxResults, int firstResult) {
        return findHorarioEntities(false, maxResults, firstResult);
    }

    private List<Horario> findHorarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Horario.class));
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

    public Horario findHorario(HorarioPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Horario.class, id);
        } finally {
            em.close();
        }
    }

    public int getHorarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Horario> rt = cq.from(Horario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
