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
import entidades.Grupo;
import entidades.MateriaGrupoProfesor;
import entidades.MateriaGrupoProfesorPK;
import entidades.MateriaOfertada;
import entidades.Profesor;
import entidades.Nota;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class MateriaGrupoProfesorJpaController implements Serializable {

    public MateriaGrupoProfesorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MateriaGrupoProfesor materiaGrupoProfesor) throws PreexistingEntityException, Exception {
        if (materiaGrupoProfesor.getMateriaGrupoProfesorPK() == null) {
            materiaGrupoProfesor.setMateriaGrupoProfesorPK(new MateriaGrupoProfesorPK());
        }
        if (materiaGrupoProfesor.getNotaList() == null) {
            materiaGrupoProfesor.setNotaList(new ArrayList<Nota>());
        }
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setGruSeccion(materiaGrupoProfesor.getGrupo().getGruSeccion());
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setMatCod(materiaGrupoProfesor.getMateriaOfertada().getMateriaOfertadaPK().getMatCod());
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setPerAcaId(materiaGrupoProfesor.getMateriaOfertada().getMateriaOfertadaPK().getPerAcaId());
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setPerIdentificacion(materiaGrupoProfesor.getProfesor().getPerIdentificacion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupo grupo = materiaGrupoProfesor.getGrupo();
            if (grupo != null) {
                grupo = em.getReference(grupo.getClass(), grupo.getGruSeccion());
                materiaGrupoProfesor.setGrupo(grupo);
            }
            MateriaOfertada materiaOfertada = materiaGrupoProfesor.getMateriaOfertada();
            if (materiaOfertada != null) {
                materiaOfertada = em.getReference(materiaOfertada.getClass(), materiaOfertada.getMateriaOfertadaPK());
                materiaGrupoProfesor.setMateriaOfertada(materiaOfertada);
            }
            Profesor profesor = materiaGrupoProfesor.getProfesor();
            if (profesor != null) {
                profesor = em.getReference(profesor.getClass(), profesor.getPerIdentificacion());
                materiaGrupoProfesor.setProfesor(profesor);
            }
            List<Nota> attachedNotaList = new ArrayList<Nota>();
            for (Nota notaListNotaToAttach : materiaGrupoProfesor.getNotaList()) {
                notaListNotaToAttach = em.getReference(notaListNotaToAttach.getClass(), notaListNotaToAttach.getNotaPK());
                attachedNotaList.add(notaListNotaToAttach);
            }
            materiaGrupoProfesor.setNotaList(attachedNotaList);
            em.persist(materiaGrupoProfesor);
            if (grupo != null) {
                grupo.getMateriaGrupoProfesorList().add(materiaGrupoProfesor);
                grupo = em.merge(grupo);
            }
            if (materiaOfertada != null) {
                materiaOfertada.getMateriaGrupoProfesorList().add(materiaGrupoProfesor);
                materiaOfertada = em.merge(materiaOfertada);
            }
            if (profesor != null) {
                profesor.getMateriaGrupoProfesorList().add(materiaGrupoProfesor);
                profesor = em.merge(profesor);
            }
            for (Nota notaListNota : materiaGrupoProfesor.getNotaList()) {
                MateriaGrupoProfesor oldMateriaGrupoProfesorOfNotaListNota = notaListNota.getMateriaGrupoProfesor();
                notaListNota.setMateriaGrupoProfesor(materiaGrupoProfesor);
                notaListNota = em.merge(notaListNota);
                if (oldMateriaGrupoProfesorOfNotaListNota != null) {
                    oldMateriaGrupoProfesorOfNotaListNota.getNotaList().remove(notaListNota);
                    oldMateriaGrupoProfesorOfNotaListNota = em.merge(oldMateriaGrupoProfesorOfNotaListNota);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMateriaGrupoProfesor(materiaGrupoProfesor.getMateriaGrupoProfesorPK()) != null) {
                throw new PreexistingEntityException("MateriaGrupoProfesor " + materiaGrupoProfesor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MateriaGrupoProfesor materiaGrupoProfesor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setGruSeccion(materiaGrupoProfesor.getGrupo().getGruSeccion());
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setMatCod(materiaGrupoProfesor.getMateriaOfertada().getMateriaOfertadaPK().getMatCod());
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setPerAcaId(materiaGrupoProfesor.getMateriaOfertada().getMateriaOfertadaPK().getPerAcaId());
        materiaGrupoProfesor.getMateriaGrupoProfesorPK().setPerIdentificacion(materiaGrupoProfesor.getProfesor().getPerIdentificacion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MateriaGrupoProfesor persistentMateriaGrupoProfesor = em.find(MateriaGrupoProfesor.class, materiaGrupoProfesor.getMateriaGrupoProfesorPK());
            Grupo grupoOld = persistentMateriaGrupoProfesor.getGrupo();
            Grupo grupoNew = materiaGrupoProfesor.getGrupo();
            MateriaOfertada materiaOfertadaOld = persistentMateriaGrupoProfesor.getMateriaOfertada();
            MateriaOfertada materiaOfertadaNew = materiaGrupoProfesor.getMateriaOfertada();
            Profesor profesorOld = persistentMateriaGrupoProfesor.getProfesor();
            Profesor profesorNew = materiaGrupoProfesor.getProfesor();
            List<Nota> notaListOld = persistentMateriaGrupoProfesor.getNotaList();
            List<Nota> notaListNew = materiaGrupoProfesor.getNotaList();
            List<String> illegalOrphanMessages = null;
            for (Nota notaListOldNota : notaListOld) {
                if (!notaListNew.contains(notaListOldNota)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Nota " + notaListOldNota + " since its materiaGrupoProfesor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (grupoNew != null) {
                grupoNew = em.getReference(grupoNew.getClass(), grupoNew.getGruSeccion());
                materiaGrupoProfesor.setGrupo(grupoNew);
            }
            if (materiaOfertadaNew != null) {
                materiaOfertadaNew = em.getReference(materiaOfertadaNew.getClass(), materiaOfertadaNew.getMateriaOfertadaPK());
                materiaGrupoProfesor.setMateriaOfertada(materiaOfertadaNew);
            }
            if (profesorNew != null) {
                profesorNew = em.getReference(profesorNew.getClass(), profesorNew.getPerIdentificacion());
                materiaGrupoProfesor.setProfesor(profesorNew);
            }
            List<Nota> attachedNotaListNew = new ArrayList<Nota>();
            for (Nota notaListNewNotaToAttach : notaListNew) {
                notaListNewNotaToAttach = em.getReference(notaListNewNotaToAttach.getClass(), notaListNewNotaToAttach.getNotaPK());
                attachedNotaListNew.add(notaListNewNotaToAttach);
            }
            notaListNew = attachedNotaListNew;
            materiaGrupoProfesor.setNotaList(notaListNew);
            materiaGrupoProfesor = em.merge(materiaGrupoProfesor);
            if (grupoOld != null && !grupoOld.equals(grupoNew)) {
                grupoOld.getMateriaGrupoProfesorList().remove(materiaGrupoProfesor);
                grupoOld = em.merge(grupoOld);
            }
            if (grupoNew != null && !grupoNew.equals(grupoOld)) {
                grupoNew.getMateriaGrupoProfesorList().add(materiaGrupoProfesor);
                grupoNew = em.merge(grupoNew);
            }
            if (materiaOfertadaOld != null && !materiaOfertadaOld.equals(materiaOfertadaNew)) {
                materiaOfertadaOld.getMateriaGrupoProfesorList().remove(materiaGrupoProfesor);
                materiaOfertadaOld = em.merge(materiaOfertadaOld);
            }
            if (materiaOfertadaNew != null && !materiaOfertadaNew.equals(materiaOfertadaOld)) {
                materiaOfertadaNew.getMateriaGrupoProfesorList().add(materiaGrupoProfesor);
                materiaOfertadaNew = em.merge(materiaOfertadaNew);
            }
            if (profesorOld != null && !profesorOld.equals(profesorNew)) {
                profesorOld.getMateriaGrupoProfesorList().remove(materiaGrupoProfesor);
                profesorOld = em.merge(profesorOld);
            }
            if (profesorNew != null && !profesorNew.equals(profesorOld)) {
                profesorNew.getMateriaGrupoProfesorList().add(materiaGrupoProfesor);
                profesorNew = em.merge(profesorNew);
            }
            for (Nota notaListNewNota : notaListNew) {
                if (!notaListOld.contains(notaListNewNota)) {
                    MateriaGrupoProfesor oldMateriaGrupoProfesorOfNotaListNewNota = notaListNewNota.getMateriaGrupoProfesor();
                    notaListNewNota.setMateriaGrupoProfesor(materiaGrupoProfesor);
                    notaListNewNota = em.merge(notaListNewNota);
                    if (oldMateriaGrupoProfesorOfNotaListNewNota != null && !oldMateriaGrupoProfesorOfNotaListNewNota.equals(materiaGrupoProfesor)) {
                        oldMateriaGrupoProfesorOfNotaListNewNota.getNotaList().remove(notaListNewNota);
                        oldMateriaGrupoProfesorOfNotaListNewNota = em.merge(oldMateriaGrupoProfesorOfNotaListNewNota);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                MateriaGrupoProfesorPK id = materiaGrupoProfesor.getMateriaGrupoProfesorPK();
                if (findMateriaGrupoProfesor(id) == null) {
                    throw new NonexistentEntityException("The materiaGrupoProfesor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(MateriaGrupoProfesorPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MateriaGrupoProfesor materiaGrupoProfesor;
            try {
                materiaGrupoProfesor = em.getReference(MateriaGrupoProfesor.class, id);
                materiaGrupoProfesor.getMateriaGrupoProfesorPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The materiaGrupoProfesor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Nota> notaListOrphanCheck = materiaGrupoProfesor.getNotaList();
            for (Nota notaListOrphanCheckNota : notaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This MateriaGrupoProfesor (" + materiaGrupoProfesor + ") cannot be destroyed since the Nota " + notaListOrphanCheckNota + " in its notaList field has a non-nullable materiaGrupoProfesor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Grupo grupo = materiaGrupoProfesor.getGrupo();
            if (grupo != null) {
                grupo.getMateriaGrupoProfesorList().remove(materiaGrupoProfesor);
                grupo = em.merge(grupo);
            }
            MateriaOfertada materiaOfertada = materiaGrupoProfesor.getMateriaOfertada();
            if (materiaOfertada != null) {
                materiaOfertada.getMateriaGrupoProfesorList().remove(materiaGrupoProfesor);
                materiaOfertada = em.merge(materiaOfertada);
            }
            Profesor profesor = materiaGrupoProfesor.getProfesor();
            if (profesor != null) {
                profesor.getMateriaGrupoProfesorList().remove(materiaGrupoProfesor);
                profesor = em.merge(profesor);
            }
            em.remove(materiaGrupoProfesor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MateriaGrupoProfesor> findMateriaGrupoProfesorEntities() {
        return findMateriaGrupoProfesorEntities(true, -1, -1);
    }

    public List<MateriaGrupoProfesor> findMateriaGrupoProfesorEntities(int maxResults, int firstResult) {
        return findMateriaGrupoProfesorEntities(false, maxResults, firstResult);
    }

    private List<MateriaGrupoProfesor> findMateriaGrupoProfesorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MateriaGrupoProfesor.class));
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

    public MateriaGrupoProfesor findMateriaGrupoProfesor(MateriaGrupoProfesorPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MateriaGrupoProfesor.class, id);
        } finally {
            em.close();
        }
    }

    public int getMateriaGrupoProfesorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MateriaGrupoProfesor> rt = cq.from(MateriaGrupoProfesor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
