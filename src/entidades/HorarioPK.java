/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Sebastian Jimenez
 */
@Embeddable
public class HorarioPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "PER_ACA_ID")
    private BigDecimal perAcaId;
    @Basic(optional = false)
    @Column(name = "SAL_ID")
    private BigDecimal salId;
    @Basic(optional = false)
    @Column(name = "HOR_FRANJA_HORARIA")
    private String horFranjaHoraria;
    @Basic(optional = false)
    @Column(name = "HOR_DIA")
    private String horDia;

    public HorarioPK() {
    }

    public HorarioPK(BigDecimal perAcaId, BigDecimal salId, String horFranjaHoraria, String horDia) {
        this.perAcaId = perAcaId;
        this.salId = salId;
        this.horFranjaHoraria = horFranjaHoraria;
        this.horDia = horDia;
    }

    public BigDecimal getPerAcaId() {
        return perAcaId;
    }

    public void setPerAcaId(BigDecimal perAcaId) {
        this.perAcaId = perAcaId;
    }

    public BigDecimal getSalId() {
        return salId;
    }

    public void setSalId(BigDecimal salId) {
        this.salId = salId;
    }

    public String getHorFranjaHoraria() {
        return horFranjaHoraria;
    }

    public void setHorFranjaHoraria(String horFranjaHoraria) {
        this.horFranjaHoraria = horFranjaHoraria;
    }

    public String getHorDia() {
        return horDia;
    }

    public void setHorDia(String horDia) {
        this.horDia = horDia;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (perAcaId != null ? perAcaId.hashCode() : 0);
        hash += (salId != null ? salId.hashCode() : 0);
        hash += (horFranjaHoraria != null ? horFranjaHoraria.hashCode() : 0);
        hash += (horDia != null ? horDia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HorarioPK)) {
            return false;
        }
        HorarioPK other = (HorarioPK) object;
        if ((this.perAcaId == null && other.perAcaId != null) || (this.perAcaId != null && !this.perAcaId.equals(other.perAcaId))) {
            return false;
        }
        if ((this.salId == null && other.salId != null) || (this.salId != null && !this.salId.equals(other.salId))) {
            return false;
        }
        if ((this.horFranjaHoraria == null && other.horFranjaHoraria != null) || (this.horFranjaHoraria != null && !this.horFranjaHoraria.equals(other.horFranjaHoraria))) {
            return false;
        }
        if ((this.horDia == null && other.horDia != null) || (this.horDia != null && !this.horDia.equals(other.horDia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.HorarioPK[ perAcaId=" + perAcaId + ", salId=" + salId + ", horFranjaHoraria=" + horFranjaHoraria + ", horDia=" + horDia + " ]";
    }
    
}
