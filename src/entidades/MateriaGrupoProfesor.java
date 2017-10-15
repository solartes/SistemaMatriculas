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
import javax.persistence.JoinColumns;
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
@Table(name = "MATERIA_GRUPO_PROFESOR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MateriaGrupoProfesor.findAll", query = "SELECT m FROM MateriaGrupoProfesor m")
    , @NamedQuery(name = "MateriaGrupoProfesor.findByPerIdentificacion", query = "SELECT m FROM MateriaGrupoProfesor m WHERE m.materiaGrupoProfesorPK.perIdentificacion = :perIdentificacion")
    , @NamedQuery(name = "MateriaGrupoProfesor.findByGruSeccion", query = "SELECT m FROM MateriaGrupoProfesor m WHERE m.materiaGrupoProfesorPK.gruSeccion = :gruSeccion")
    , @NamedQuery(name = "MateriaGrupoProfesor.findByMatCod", query = "SELECT m FROM MateriaGrupoProfesor m WHERE m.materiaGrupoProfesorPK.matCod = :matCod")
    , @NamedQuery(name = "MateriaGrupoProfesor.findByPerAcaId", query = "SELECT m FROM MateriaGrupoProfesor m WHERE m.materiaGrupoProfesorPK.perAcaId = :perAcaId")})
public class MateriaGrupoProfesor implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MateriaGrupoProfesorPK materiaGrupoProfesorPK;
    @JoinColumn(name = "GRU_SECCION", referencedColumnName = "GRU_SECCION", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Grupo grupo;
    @JoinColumns({
        @JoinColumn(name = "MAT_COD", referencedColumnName = "MAT_COD", insertable = false, updatable = false)
        , @JoinColumn(name = "PER_ACA_ID", referencedColumnName = "PER_ACA_ID", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private MateriaOfertada materiaOfertada;
    @JoinColumn(name = "PER_IDENTIFICACION", referencedColumnName = "PER_IDENTIFICACION", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Profesor profesor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "materiaGrupoProfesor")
    private List<Nota> notaList;

    public MateriaGrupoProfesor() {
    }

    public MateriaGrupoProfesor(MateriaGrupoProfesorPK materiaGrupoProfesorPK) {
        this.materiaGrupoProfesorPK = materiaGrupoProfesorPK;
    }

    public MateriaGrupoProfesor(long perIdentificacion, String gruSeccion, String matCod, BigDecimal perAcaId) {
        this.materiaGrupoProfesorPK = new MateriaGrupoProfesorPK(perIdentificacion, gruSeccion, matCod, perAcaId);
    }

    public MateriaGrupoProfesorPK getMateriaGrupoProfesorPK() {
        return materiaGrupoProfesorPK;
    }

    public void setMateriaGrupoProfesorPK(MateriaGrupoProfesorPK materiaGrupoProfesorPK) {
        this.materiaGrupoProfesorPK = materiaGrupoProfesorPK;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public MateriaOfertada getMateriaOfertada() {
        return materiaOfertada;
    }

    public void setMateriaOfertada(MateriaOfertada materiaOfertada) {
        this.materiaOfertada = materiaOfertada;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    @XmlTransient
    public List<Nota> getNotaList() {
        return notaList;
    }

    public void setNotaList(List<Nota> notaList) {
        this.notaList = notaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (materiaGrupoProfesorPK != null ? materiaGrupoProfesorPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MateriaGrupoProfesor)) {
            return false;
        }
        MateriaGrupoProfesor other = (MateriaGrupoProfesor) object;
        if ((this.materiaGrupoProfesorPK == null && other.materiaGrupoProfesorPK != null) || (this.materiaGrupoProfesorPK != null && !this.materiaGrupoProfesorPK.equals(other.materiaGrupoProfesorPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.MateriaGrupoProfesor[ materiaGrupoProfesorPK=" + materiaGrupoProfesorPK + " ]";
    }
    
}
