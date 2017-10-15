/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Jimenez
 */
@Entity
@Table(name = "HORARIO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Horario.findAll", query = "SELECT h FROM Horario h")
    , @NamedQuery(name = "Horario.findByPerAcaId", query = "SELECT h FROM Horario h WHERE h.horarioPK.perAcaId = :perAcaId")
    , @NamedQuery(name = "Horario.findBySalId", query = "SELECT h FROM Horario h WHERE h.horarioPK.salId = :salId")
    , @NamedQuery(name = "Horario.findByHorFranjaHoraria", query = "SELECT h FROM Horario h WHERE h.horarioPK.horFranjaHoraria = :horFranjaHoraria")
    , @NamedQuery(name = "Horario.findByHorDia", query = "SELECT h FROM Horario h WHERE h.horarioPK.horDia = :horDia")})
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected HorarioPK horarioPK;
    @JoinColumn(name = "GRU_SECCION", referencedColumnName = "GRU_SECCION")
    @ManyToOne(optional = false)
    private Grupo gruSeccion;
    @JoinColumn(name = "PER_ACA_ID", referencedColumnName = "PER_ACA_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PeriodoAcademico periodoAcademico;
    @JoinColumn(name = "SAL_ID", referencedColumnName = "SAL_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Salon salon;

    public Horario() {
    }

    public Horario(HorarioPK horarioPK) {
        this.horarioPK = horarioPK;
    }

    public Horario(BigDecimal perAcaId, BigDecimal salId, String horFranjaHoraria, String horDia) {
        this.horarioPK = new HorarioPK(perAcaId, salId, horFranjaHoraria, horDia);
    }

    public HorarioPK getHorarioPK() {
        return horarioPK;
    }

    public void setHorarioPK(HorarioPK horarioPK) {
        this.horarioPK = horarioPK;
    }

    public Grupo getGruSeccion() {
        return gruSeccion;
    }

    public void setGruSeccion(Grupo gruSeccion) {
        this.gruSeccion = gruSeccion;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (horarioPK != null ? horarioPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Horario)) {
            return false;
        }
        Horario other = (Horario) object;
        if ((this.horarioPK == null && other.horarioPK != null) || (this.horarioPK != null && !this.horarioPK.equals(other.horarioPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Horario[ horarioPK=" + horarioPK + " ]";
    }
    
}
