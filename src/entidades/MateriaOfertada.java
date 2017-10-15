/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
@Table(name = "MATERIA_OFERTADA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MateriaOfertada.findAll", query = "SELECT m FROM MateriaOfertada m")
    , @NamedQuery(name = "MateriaOfertada.findByMatCod", query = "SELECT m FROM MateriaOfertada m WHERE m.materiaOfertadaPK.matCod = :matCod")
    , @NamedQuery(name = "MateriaOfertada.findByPerAcaId", query = "SELECT m FROM MateriaOfertada m WHERE m.materiaOfertadaPK.perAcaId = :perAcaId")})
public class MateriaOfertada implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MateriaOfertadaPK materiaOfertadaPK;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "materiaOfertada")
    private List<MateriaGrupoProfesor> materiaGrupoProfesorList;
    @JoinColumn(name = "MAT_COD", referencedColumnName = "MAT_COD", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Materia materia;
    @JoinColumn(name = "PER_ACA_ID", referencedColumnName = "PER_ACA_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PeriodoAcademico periodoAcademico;

    public MateriaOfertada() {
    }

    public MateriaOfertada(MateriaOfertadaPK materiaOfertadaPK) {
        this.materiaOfertadaPK = materiaOfertadaPK;
    }

    public MateriaOfertada(String matCod, BigDecimal perAcaId) {
        this.materiaOfertadaPK = new MateriaOfertadaPK(matCod, perAcaId);
    }

    public MateriaOfertadaPK getMateriaOfertadaPK() {
        return materiaOfertadaPK;
    }

    public void setMateriaOfertadaPK(MateriaOfertadaPK materiaOfertadaPK) {
        this.materiaOfertadaPK = materiaOfertadaPK;
    }

    @XmlTransient
    public List<MateriaGrupoProfesor> getMateriaGrupoProfesorList() {
        return materiaGrupoProfesorList;
    }

    public void setMateriaGrupoProfesorList(List<MateriaGrupoProfesor> materiaGrupoProfesorList) {
        this.materiaGrupoProfesorList = materiaGrupoProfesorList;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (materiaOfertadaPK != null ? materiaOfertadaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MateriaOfertada)) {
            return false;
        }
        MateriaOfertada other = (MateriaOfertada) object;
        if ((this.materiaOfertadaPK == null && other.materiaOfertadaPK != null) || (this.materiaOfertadaPK != null && !this.materiaOfertadaPK.equals(other.materiaOfertadaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.MateriaOfertada[ materiaOfertadaPK=" + materiaOfertadaPK + " ]";
    }
    
}
