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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Sebastian Jimenez
 */
@Entity
@Table(name = "PROGRAMA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Programa.findAll", query = "SELECT p FROM Programa p")
    , @NamedQuery(name = "Programa.findByPrgId", query = "SELECT p FROM Programa p WHERE p.prgId = :prgId")
    , @NamedQuery(name = "Programa.findByPrgNombre", query = "SELECT p FROM Programa p WHERE p.prgNombre = :prgNombre")})
public class Programa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PRG_ID")
    private String prgId;
    @Column(name = "PRG_NOMBRE")
    private String prgNombre;
    @ManyToMany(mappedBy = "programaList")
    private List<Estudiante> estudianteList;
    @JoinColumn(name = "FAC_ID", referencedColumnName = "FAC_ID")
    @ManyToOne(optional = false)
    private Facultad facId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prgId")
    private List<Materia> materiaList;

    public Programa() {
    }

    public Programa(String prgId) {
        this.prgId = prgId;
    }

    public String getPrgId() {
        return prgId;
    }

    public void setPrgId(String prgId) {
        this.prgId = prgId;
    }

    public String getPrgNombre() {
        return prgNombre;
    }

    public void setPrgNombre(String prgNombre) {
        this.prgNombre = prgNombre;
    }

    @XmlTransient
    public List<Estudiante> getEstudianteList() {
        return estudianteList;
    }

    public void setEstudianteList(List<Estudiante> estudianteList) {
        this.estudianteList = estudianteList;
    }

    public Facultad getFacId() {
        return facId;
    }

    public void setFacId(Facultad facId) {
        this.facId = facId;
    }

    @XmlTransient
    public List<Materia> getMateriaList() {
        return materiaList;
    }

    public void setMateriaList(List<Materia> materiaList) {
        this.materiaList = materiaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (prgId != null ? prgId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Programa)) {
            return false;
        }
        Programa other = (Programa) object;
        if ((this.prgId == null && other.prgId != null) || (this.prgId != null && !this.prgId.equals(other.prgId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Programa[ prgId=" + prgId + " ]";
    }
    
}
