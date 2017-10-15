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
@Table(name = "PERIODO_ACADEMICO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeriodoAcademico.findAll", query = "SELECT p FROM PeriodoAcademico p")
    , @NamedQuery(name = "PeriodoAcademico.findByPerAcaId", query = "SELECT p FROM PeriodoAcademico p WHERE p.perAcaId = :perAcaId")
    , @NamedQuery(name = "PeriodoAcademico.findByPerAcaPeriodo", query = "SELECT p FROM PeriodoAcademico p WHERE p.perAcaPeriodo = :perAcaPeriodo")})
public class PeriodoAcademico implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "PER_ACA_ID")
    private BigDecimal perAcaId;
    @Column(name = "PER_ACA_PERIODO")
    private String perAcaPeriodo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "periodoAcademico")
    private List<MateriaOfertada> materiaOfertadaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "periodoAcademico")
    private List<Horario> horarioList;

    public PeriodoAcademico() {
    }

    public PeriodoAcademico(BigDecimal perAcaId) {
        this.perAcaId = perAcaId;
    }

    public BigDecimal getPerAcaId() {
        return perAcaId;
    }

    public void setPerAcaId(BigDecimal perAcaId) {
        this.perAcaId = perAcaId;
    }

    public String getPerAcaPeriodo() {
        return perAcaPeriodo;
    }

    public void setPerAcaPeriodo(String perAcaPeriodo) {
        this.perAcaPeriodo = perAcaPeriodo;
    }

    @XmlTransient
    public List<MateriaOfertada> getMateriaOfertadaList() {
        return materiaOfertadaList;
    }

    public void setMateriaOfertadaList(List<MateriaOfertada> materiaOfertadaList) {
        this.materiaOfertadaList = materiaOfertadaList;
    }

    @XmlTransient
    public List<Horario> getHorarioList() {
        return horarioList;
    }

    public void setHorarioList(List<Horario> horarioList) {
        this.horarioList = horarioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (perAcaId != null ? perAcaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PeriodoAcademico)) {
            return false;
        }
        PeriodoAcademico other = (PeriodoAcademico) object;
        if ((this.perAcaId == null && other.perAcaId != null) || (this.perAcaId != null && !this.perAcaId.equals(other.perAcaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.PeriodoAcademico[ perAcaId=" + perAcaId + " ]";
    }
    
}
