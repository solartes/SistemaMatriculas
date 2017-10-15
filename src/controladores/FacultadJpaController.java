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
import entidades.Programa;
import java.util.ArrayList;
import java.util.List;
import entidades.Salon;
import entidades.Departamento;
import entidades.Facultad;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Sebastian Jimenez
 */
public class FacultadJpaController implements Serializable {

    public FacultadJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facultad facultad) throws PreexistingEntityException, Exception {
        if (facultad.getProgramaList() == null) {
            facultad.setProgramaList(new ArrayList<Programa>());
        }
        if (facultad.getSalonList() == null) {
            facultad.setSalonList(new ArrayList<Salon>());
        }
        if (facultad.getDepartamentoList() == null) {
            facultad.setDepartamentoList(new ArrayList<Departamento>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Programa> attachedProgramaList = new ArrayList<Programa>();
            for (Programa programaListProgramaToAttach : facultad.getProgramaList()) {
                programaListProgramaToAttach = em.getReference(programaListProgramaToAttach.getClass(), programaListProgramaToAttach.getPrgId());
                attachedProgramaList.add(programaListProgramaToAttach);
            }
            facultad.setProgramaList(attachedProgramaList);
            List<Salon> attachedSalonList = new ArrayList<Salon>();
            for (Salon salonListSalonToAttach : facultad.getSalonList()) {
                salonListSalonToAttach = em.getReference(salonListSalonToAttach.getClass(), salonListSalonToAttach.getSalId());
                attachedSalonList.add(salonListSalonToAttach);
            }
            facultad.setSalonList(attachedSalonList);
            List<Departamento> attachedDepartamentoList = new ArrayList<Departamento>();
            for (Departamento departamentoListDepartamentoToAttach : facultad.getDepartamentoList()) {
                departamentoListDepartamentoToAttach = em.getReference(departamentoListDepartamentoToAttach.getClass(), departamentoListDepartamentoToAttach.getDepId());
                attachedDepartamentoList.add(departamentoListDepartamentoToAttach);
            }
            facultad.setDepartamentoList(attachedDepartamentoList);
            em.persist(facultad);
            for (Programa programaListPrograma : facultad.getProgramaList()) {
                Facultad oldFacIdOfProgramaListPrograma = programaListPrograma.getFacId();
                programaListPrograma.setFacId(facultad);
                programaListPrograma = em.merge(programaListPrograma);
                if (oldFacIdOfProgramaListPrograma != null) {
                    oldFacIdOfProgramaListPrograma.getProgramaList().remove(programaListPrograma);
                    oldFacIdOfProgramaListPrograma = em.merge(oldFacIdOfProgramaListPrograma);
                }
            }
            for (Salon salonListSalon : facultad.getSalonList()) {
                Facultad oldFacIdOfSalonListSalon = salonListSalon.getFacId();
                salonListSalon.setFacId(facultad);
                salonListSalon = em.merge(salonListSalon);
                if (oldFacIdOfSalonListSalon != null) {
                    oldFacIdOfSalonListSalon.getSalonList().remove(salonListSalon);
                    oldFacIdOfSalonListSalon = em.merge(oldFacIdOfSalonListSalon);
                }
            }
            for (Departamento departamentoListDepartamento : facultad.getDepartamentoList()) {
                Facultad oldFacIdOfDepartamentoListDepartamento = departamentoListDepartamento.getFacId();
                departamentoListDepartamento.setFacId(facultad);
                departamentoListDepartamento = em.merge(departamentoListDepartamento);
                if (oldFacIdOfDepartamentoListDepartamento != null) {
                    oldFacIdOfDepartamentoListDepartamento.getDepartamentoList().remove(departamentoListDepartamento);
                    oldFacIdOfDepartamentoListDepartamento = em.merge(oldFacIdOfDepartamentoListDepartamento);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFacultad(facultad.getFacId()) != null) {
                throw new PreexistingEntityException("Facultad " + facultad + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Facultad facultad) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facultad persistentFacultad = em.find(Facultad.class, facultad.getFacId());
            List<Programa> programaListOld = persistentFacultad.getProgramaList();
            List<Programa> programaListNew = facultad.getProgramaList();
            List<Salon> salonListOld = persistentFacultad.getSalonList();
            List<Salon> salonListNew = facultad.getSalonList();
            List<Departamento> departamentoListOld = persistentFacultad.getDepartamentoList();
            List<Departamento> departamentoListNew = facultad.getDepartamentoList();
            List<String> illegalOrphanMessages = null;
            for (Programa programaListOldPrograma : programaListOld) {
                if (!programaListNew.contains(programaListOldPrograma)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Programa " + programaListOldPrograma + " since its facId field is not nullable.");
                }
            }
            for (Salon salonListOldSalon : salonListOld) {
                if (!salonListNew.contains(salonListOldSalon)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Salon " + salonListOldSalon + " since its facId field is not nullable.");
                }
            }
            for (Departamento departamentoListOldDepartamento : departamentoListOld) {
                if (!departamentoListNew.contains(departamentoListOldDepartamento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Departamento " + departamentoListOldDepartamento + " since its facId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Programa> attachedProgramaListNew = new ArrayList<Programa>();
            for (Programa programaListNewProgramaToAttach : programaListNew) {
                programaListNewProgramaToAttach = em.getReference(programaListNewProgramaToAttach.getClass(), programaListNewProgramaToAttach.getPrgId());
                attachedProgramaListNew.add(programaListNewProgramaToAttach);
            }
            programaListNew = attachedProgramaListNew;
            facultad.setProgramaList(programaListNew);
            List<Salon> attachedSalonListNew = new ArrayList<Salon>();
            for (Salon salonListNewSalonToAttach : salonListNew) {
                salonListNewSalonToAttach = em.getReference(salonListNewSalonToAttach.getClass(), salonListNewSalonToAttach.getSalId());
                attachedSalonListNew.add(salonListNewSalonToAttach);
            }
            salonListNew = attachedSalonListNew;
            facultad.setSalonList(salonListNew);
            List<Departamento> attachedDepartamentoListNew = new ArrayList<Departamento>();
            for (Departamento departamentoListNewDepartamentoToAttach : departamentoListNew) {
                departamentoListNewDepartamentoToAttach = em.getReference(departamentoListNewDepartamentoToAttach.getClass(), departamentoListNewDepartamentoToAttach.getDepId());
                attachedDepartamentoListNew.add(departamentoListNewDepartamentoToAttach);
            }
            departamentoListNew = attachedDepartamentoListNew;
            facultad.setDepartamentoList(departamentoListNew);
            facultad = em.merge(facultad);
            for (Programa programaListNewPrograma : programaListNew) {
                if (!programaListOld.contains(programaListNewPrograma)) {
                    Facultad oldFacIdOfProgramaListNewPrograma = programaListNewPrograma.getFacId();
                    programaListNewPrograma.setFacId(facultad);
                    programaListNewPrograma = em.merge(programaListNewPrograma);
                    if (oldFacIdOfProgramaListNewPrograma != null && !oldFacIdOfProgramaListNewPrograma.equals(facultad)) {
                        oldFacIdOfProgramaListNewPrograma.getProgramaList().remove(programaListNewPrograma);
                        oldFacIdOfProgramaListNewPrograma = em.merge(oldFacIdOfProgramaListNewPrograma);
                    }
                }
            }
            for (Salon salonListNewSalon : salonListNew) {
                if (!salonListOld.contains(salonListNewSalon)) {
                    Facultad oldFacIdOfSalonListNewSalon = salonListNewSalon.getFacId();
                    salonListNewSalon.setFacId(facultad);
                    salonListNewSalon = em.merge(salonListNewSalon);
                    if (oldFacIdOfSalonListNewSalon != null && !oldFacIdOfSalonListNewSalon.equals(facultad)) {
                        oldFacIdOfSalonListNewSalon.getSalonList().remove(salonListNewSalon);
                        oldFacIdOfSalonListNewSalon = em.merge(oldFacIdOfSalonListNewSalon);
                    }
                }
            }
            for (Departamento departamentoListNewDepartamento : departamentoListNew) {
                if (!departamentoListOld.contains(departamentoListNewDepartamento)) {
                    Facultad oldFacIdOfDepartamentoListNewDepartamento = departamentoListNewDepartamento.getFacId();
                    departamentoListNewDepartamento.setFacId(facultad);
                    departamentoListNewDepartamento = em.merge(departamentoListNewDepartamento);
                    if (oldFacIdOfDepartamentoListNewDepartamento != null && !oldFacIdOfDepartamentoListNewDepartamento.equals(facultad)) {
                        oldFacIdOfDepartamentoListNewDepartamento.getDepartamentoList().remove(departamentoListNewDepartamento);
                        oldFacIdOfDepartamentoListNewDepartamento = em.merge(oldFacIdOfDepartamentoListNewDepartamento);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = facultad.getFacId();
                if (findFacultad(id) == null) {
                    throw new NonexistentEntityException("The facultad with id " + id + " no longer exists.");
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
            Facultad facultad;
            try {
                facultad = em.getReference(Facultad.class, id);
                facultad.getFacId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facultad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Programa> programaListOrphanCheck = facultad.getProgramaList();
            for (Programa programaListOrphanCheckPrograma : programaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facultad (" + facultad + ") cannot be destroyed since the Programa " + programaListOrphanCheckPrograma + " in its programaList field has a non-nullable facId field.");
            }
            List<Salon> salonListOrphanCheck = facultad.getSalonList();
            for (Salon salonListOrphanCheckSalon : salonListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facultad (" + facultad + ") cannot be destroyed since the Salon " + salonListOrphanCheckSalon + " in its salonList field has a non-nullable facId field.");
            }
            List<Departamento> departamentoListOrphanCheck = facultad.getDepartamentoList();
            for (Departamento departamentoListOrphanCheckDepartamento : departamentoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facultad (" + facultad + ") cannot be destroyed since the Departamento " + departamentoListOrphanCheckDepartamento + " in its departamentoList field has a non-nullable facId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(facultad);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Facultad> findFacultadEntities() {
        return findFacultadEntities(true, -1, -1);
    }

    public List<Facultad> findFacultadEntities(int maxResults, int firstResult) {
        return findFacultadEntities(false, maxResults, firstResult);
    }

    private List<Facultad> findFacultadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facultad.class));
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

    public Facultad findFacultad(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facultad.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacultadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facultad> rt = cq.from(Facultad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
