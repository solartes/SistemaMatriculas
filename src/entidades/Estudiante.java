/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "ESTUDIANTE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estudiante.findAll", query = "SELECT e FROM Estudiante e")
    , @NamedQuery(name = "Estudiante.findByPerIdentificacion", query = "SELECT e FROM Estudiante e WHERE e.perIdentificacion = :perIdentificacion")
    , @NamedQuery(name = "Estudiante.findByEstCodigo", query = "SELECT e FROM Estudiante e WHERE e.estCodigo = :estCodigo")
    , @NamedQuery(name = "Estudiante.findByEstPromCarrera", query = "SELECT e FROM Estudiante e WHERE e.estPromCarrera = :estPromCarrera")
    , @NamedQuery(name = "Estudiante.findByEstPromSemestre", query = "SELECT e FROM Estudiante e WHERE e.estPromSemestre = :estPromSemestre")
    , @NamedQuery(name = "Estudiante.findByEstSemestre", query = "SELECT e FROM Estudiante e WHERE e.estSemestre = :estSemestre")})
public class Estudiante implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PER_IDENTIFICACION")
    private Long perIdentificacion;
    @Basic(optional = false)
    @Column(name = "EST_CODIGO")
    private String estCodigo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "EST_PROM_CARRERA")
    private BigDecimal estPromCarrera;
    @Column(name = "EST_PROM_SEMESTRE")
    private BigDecimal estPromSemestre;
    @Column(name = "EST_SEMESTRE")
    private Short estSemestre;
    @JoinTable(name = "PROGRAMA_ESTUDIANTE", joinColumns = {
        @JoinColumn(name = "PER_IDENTIFICACION", referencedColumnName = "PER_IDENTIFICACION")}, inverseJoinColumns = {
        @JoinColumn(name = "PRG_ID", referencedColumnName = "PRG_ID")})
    @ManyToMany
    private List<Programa> programaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estudiante")
    private List<Nota> notaList;
    @JoinColumn(name = "PER_IDENTIFICACION", referencedColumnName = "PER_IDENTIFICACION", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Persona persona;

    public Estudiante() {
    }

    public Estudiante(Long perIdentificacion) {
        this.perIdentificacion = perIdentificacion;
    }

    public Estudiante(Long perIdentificacion, String estCodigo) {
        this.perIdentificacion = perIdentificacion;
        this.estCodigo = estCodigo;
    }

    public Long getPerIdentificacion() {
        return perIdentificacion;
    }

    public void setPerIdentificacion(Long perIdentificacion) {
        this.perIdentificacion = perIdentificacion;
    }

    public String getEstCodigo() {
        return estCodigo;
    }

    public void setEstCodigo(String estCodigo) {
        this.estCodigo = estCodigo;
    }

    public BigDecimal getEstPromCarrera() {
        return estPromCarrera;
    }

    public void setEstPromCarrera(BigDecimal estPromCarrera) {
        this.estPromCarrera = estPromCarrera;
    }

    public BigDecimal getEstPromSemestre() {
        return estPromSemestre;
    }

    public void setEstPromSemestre(BigDecimal estPromSemestre) {
        this.estPromSemestre = estPromSemestre;
    }

    public Short getEstSemestre() {
        return estSemestre;
    }

    public void setEstSemestre(Short estSemestre) {
        this.estSemestre = estSemestre;
    }

    @XmlTransient
    public List<Programa> getProgramaList() {
        return programaList;
    }

    public void setProgramaList(List<Programa> programaList) {
        this.programaList = programaList;
    }

    @XmlTransient
    public List<Nota> getNotaList() {
        return notaList;
    }

    public void setNotaList(List<Nota> notaList) {
        this.notaList = notaList;
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
        if (!(object instanceof Estudiante)) {
            return false;
        }
        Estudiante other = (Estudiante) object;
        if ((this.perIdentificacion == null && other.perIdentificacion != null) || (this.perIdentificacion != null && !this.perIdentificacion.equals(other.perIdentificacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Estudiante[ perIdentificacion=" + perIdentificacion + " ]";
    }
    
}
