/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Sebastian Jimenez
 */
@Entity
@Table(name = "PROFESOR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Profesor.findAll", query = "SELECT p FROM Profesor p")
    , @NamedQuery(name = "Profesor.findByPerIdentificacion", query = "SELECT p FROM Profesor p WHERE p.perIdentificacion = :perIdentificacion")
    , @NamedQuery(name = "Profesor.findByProCodigo", query = "SELECT p FROM Profesor p WHERE p.proCodigo = :proCodigo")
    , @NamedQuery(name = "Profesor.findByProContratacion", query = "SELECT p FROM Profesor p WHERE p.proContratacion = :proContratacion")})
public class Profesor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PER_IDENTIFICACION")
    private Long perIdentificacion;
    @Column(name = "PRO_CODIGO")
    private String proCodigo;
    @Column(name = "PRO_CONTRATACION")
    private String proContratacion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profesor")
    private List<MateriaGrupoProfesor> materiaGrupoProfesorList;
    @JoinColumn(name = "DEP_ID", referencedColumnName = "DEP_ID")
    @ManyToOne(optional = false)
    private Departamento depId;
    @JoinColumn(name = "PER_IDENTIFICACION", referencedColumnName = "PER_IDENTIFICACION", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Persona persona;

    public Profesor() {
    }

    public Profesor(Long perIdentificacion) {
        this.perIdentificacion = perIdentificacion;
    }

    public Long getPerIdentificacion() {
        return perIdentificacion;
    }

    public void setPerIdentificacion(Long perIdentificacion) {
        this.perIdentificacion = perIdentificacion;
    }

    public String getProCodigo() {
        return proCodigo;
    }

    public void setProCodigo(String proCodigo) {
        this.proCodigo = proCodigo;
    }

    public String getProContratacion() {
        return proContratacion;
    }

    public void setProContratacion(String proContratacion) {
        this.proContratacion = proContratacion;
    }

    @XmlTransient
    public List<MateriaGrupoProfesor> getMateriaGrupoProfesorList() {
        return materiaGrupoProfesorList;
    }

    public void setMateriaGrupoProfesorList(List<MateriaGrupoProfesor> materiaGrupoProfesorList) {
        this.materiaGrupoProfesorList = materiaGrupoProfesorList;
    }

    public Departamento getDepId() {
        return depId;
    }

    public void setDepId(Departamento depId) {
        this.depId = depId;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (perIdentificacion != null ? perIdentificacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Profesor)) {
            return false;
        }
        Profesor other = (Profesor) object;
        if ((this.perIdentificacion == null && other.perIdentificacion != null) || (this.perIdentificacion != null && !this.perIdentificacion.equals(other.perIdentificacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Profesor[ perIdentificacion=" + perIdentificacion + " ]";
    }
    
}
