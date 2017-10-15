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
public class MateriaOfertadaPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "MAT_COD")
    private String matCod;
    @Basic(optional = false)
    @Column(name = "PER_ACA_ID")
    private BigDecimal perAcaId;

    public MateriaOfertadaPK() {
    }

    public MateriaOfertadaPK(String matCod, BigDecimal perAcaId) {
        this.matCod = matCod;
        this.perAcaId = perAcaId;
    }

    public String getMatCod() {
        return matCod;
    }

    public void setMatCod(String matCod) {
        this.matCod = matCod;
    }

    public BigDecimal getPerAcaId() {
        return perAcaId;
    }

    public void setPerAcaId(BigDecimal perAcaId) {
        this.perAcaId = perAcaId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (matCod != null ? matCod.hashCode() : 0);
        hash += (perAcaId != null ? perAcaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MateriaOfertadaPK)) {
            return false;
        }
        MateriaOfertadaPK other = (MateriaOfertadaPK) object;
        if ((this.matCod == null && other.matCod != null) || (this.matCod != null && !this.matCod.equals(other.matCod))) {
            return false;
        }
        if ((this.perAcaId == null && other.perAcaId != null) || (this.perAcaId != null && !this.perAcaId.equals(other.perAcaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.MateriaOfertadaPK[ matCod=" + matCod + ", perAcaId=" + perAcaId + " ]";
    }
    
}
