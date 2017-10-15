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
import entidades.Horario;
import entidades.Salon;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class SalonJpaController implements Serializable {

    public SalonJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Salon salon) throws PreexistingEntityException, Exception {
        if (salon.getHorarioList() == null) {
            salon.setHorarioList(new ArrayList<Horario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facultad facId = salon.getFacId();
            if (facId != null) {
                facId = em.getReference(facId.getClass(), facId.getFacId());
                salon.setFacId(facId);
            }
            List<Horario> attachedHorarioList = new ArrayList<Horario>();
            for (Horario horarioListHorarioToAttach : salon.getHorarioList()) {
                horarioListHorarioToAttach = em.getReference(horarioListHorarioToAttach.getClass(), horarioListHorarioToAttach.getHorarioPK());
                attachedHorarioList.add(horarioListHorarioToAttach);
            }
            salon.setHorarioList(attachedHorarioList);
            em.persist(salon);
            if (facId != null) {
                facId.getSalonList().add(salon);
                facId = em.merge(facId);
            }
            for (Horario horarioListHorario : salon.getHorarioList()) {
                Salon oldSalonOfHorarioListHorario = horarioListHorario.getSalon();
                horarioListHorario.setSalon(salon);
                horarioListHorario = em.merge(horarioListHorario);
                if (oldSalonOfHorarioListHorario != null) {
                    oldSalonOfHorarioListHorario.getHorarioList().remove(horarioListHorario);
                    oldSalonOfHorarioListHorario = em.merge(oldSalonOfHorarioListHorario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSalon(salon.getSalId()) != null) {
                throw new PreexistingEntityException("Salon " + salon + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Salon salon) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Salon persistentSalon = em.find(Salon.class, salon.getSalId());
            Facultad facIdOld = persistentSalon.getFacId();
            Facultad facIdNew = salon.getFacId();
            List<Horario> horarioListOld = persistentSalon.getHorarioList();
            List<Horario> horarioListNew = salon.getHorarioList();
            List<String> illegalOrphanMessages = null;
            for (Horario horarioListOldHorario : horarioListOld) {
                if (!horarioListNew.contains(horarioListOldHorario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Horario " + horarioListOldHorario + " since its salon field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (facIdNew != null) {
                facIdNew = em.getReference(facIdNew.getClass(), facIdNew.getFacId());
                salon.setFacId(facIdNew);
            }
            List<Horario> attachedHorarioListNew = new ArrayList<Horario>();
            for (Horario horarioListNewHorarioToAttach : horarioListNew) {
                horarioListNewHorarioToAttach = em.getReference(horarioListNewHorarioToAttach.getClass(), horarioListNewHorarioToAttach.getHorarioPK());
                attachedHorarioListNew.add(horarioListNewHorarioToAttach);
            }
            horarioListNew = attachedHorarioListNew;
            salon.setHorarioList(horarioListNew);
            salon = em.merge(salon);
            if (facIdOld != null && !facIdOld.equals(facIdNew)) {
                facIdOld.getSalonList().remove(salon);
                facIdOld = em.merge(facIdOld);
            }
            if (facIdNew != null && !facIdNew.equals(facIdOld)) {
                facIdNew.getSalonList().add(salon);
                facIdNew = em.merge(facIdNew);
            }
            for (Horario horarioListNewHorario : horarioListNew) {
                if (!horarioListOld.contains(horarioListNewHorario)) {
                    Salon oldSalonOfHorarioListNewHorario = horarioListNewHorario.getSalon();
                    horarioListNewHorario.setSalon(salon);
                    horarioListNewHorario = em.merge(horarioListNewHorario);
                    if (oldSalonOfHorarioListNewHorario != null && !oldSalonOfHorarioListNewHorario.equals(salon)) {
                        oldSalonOfHorarioListNewHorario.getHorarioList().remove(horarioListNewHorario);
                        oldSalonOfHorarioListNewHorario = em.merge(oldSalonOfHorarioListNewHorario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = salon.getSalId();
                if (findSalon(id) == null) {
                    throw new NonexistentEntityException("The salon with id " + id + " no longer exists.");
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
            Salon salon;
            try {
                salon = em.getReference(Salon.class, id);
                salon.getSalId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The salon with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Horario> horarioListOrphanCheck = salon.getHorarioList();
            for (Horario horarioListOrphanCheckHorario : horarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Salon (" + salon + ") cannot be destroyed since the Horario " + horarioListOrphanCheckHorario + " in its horarioList field has a non-nullable salon field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Facultad facId = salon.getFacId();
            if (facId != null) {
                facId.getSalonList().remove(salon);
                facId = em.merge(facId);
            }
            em.remove(salon);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Salon> findSalonEntities() {
        return findSalonEntities(true, -1, -1);
    }

    public List<Salon> findSalonEntities(int maxResults, int firstResult) {
        return findSalonEntities(false, maxResults, firstResult);
    }

    private List<Salon> findSalonEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Salon.class));
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

    public Salon findSalon(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Salon.class, id);
        } finally {
            em.close();
        }
    }

    public int getSalonCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Salon> rt = cq.from(Salon.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
