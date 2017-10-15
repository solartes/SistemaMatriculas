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
public class MateriaGrupoProfesorPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "PER_IDENTIFICACION")
    private long perIdentificacion;
    @Basic(optional = false)
    @Column(name = "GRU_SECCION")
    private String gruSeccion;
    @Basic(optional = false)
    @Column(name = "MAT_COD")
    private String matCod;
    @Basic(optional = false)
    @Column(name = "PER_ACA_ID")
    private BigDecimal perAcaId;

    public MateriaGrupoProfesorPK() {
    }

    public MateriaGrupoProfesorPK(long perIdentificacion, String gruSeccion, String matCod, BigDecimal perAcaId) {
        this.perIdentificacion = perIdentificacion;
        this.gruSeccion = gruSeccion;
        this.matCod = matCod;
        this.perAcaId = perAcaId;
    }

    public long getPerIdentificacion() {
        return perIdentificacion;
    }

    public void setPerIdentificacion(long perIdentificacion) {
        this.perIdentificacion = perIdentificacion;
    }

    public String getGruSeccion() {
        return gruSeccion;
    }

    public void setGruSeccion(String gruSeccion) {
        this.gruSeccion = gruSeccion;
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
        hash += (int) perIdentificacion;
        hash += (gruSeccion != null ? gruSeccion.hashCode() : 0);
        hash += (matCod != null ? matCod.hashCode() : 0);
        hash += (perAcaId != null ? perAcaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MateriaGrupoProfesorPK)) {
            return false;
        }
        MateriaGrupoProfesorPK other = (MateriaGrupoProfesorPK) object;
        if (this.perIdentificacion != other.perIdentificacion) {
            return false;
        }
        if ((this.gruSeccion == null && other.gruSeccion != null) || (this.gruSeccion != null && !this.gruSeccion.equals(other.gruSeccion))) {
            return false;
        }
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
        return "entidades.MateriaGrupoProfesorPK[ perIdentificacion=" + perIdentificacion + ", gruSeccion=" + gruSeccion + ", matCod=" + matCod + ", perAcaId=" + perAcaId + " ]";
    }
    
}
