/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
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
@Table(name = "MATERIA")
@XmlRootElement

@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "Materia.materiasSemestreActual",
                query   =   "SELECT MAT_NOMBRE FROM MATERIA JOIN PROGRAMA ON (MATERIA.PRG_ID=PROGRAMA.PRG_ID) "+
                            "JOIN PROGRAMA_ESTUDIANTE PE ON (PE.PRG_ID=PROGRAMA.PRG_ID) " +
                            "JOIN ESTUDIANTE ON (ESTUDIANTE.Per_Identificacion=PE.Per_Identificacion) " +
                            "WHERE ESTUDIANTE.EST_SEMESTRE+1 = MATERIA.MAT_SEMESTRE",
                            resultClass=Materia.class
        ),
        @NamedNativeQuery(
                name    =   "Materia.materiasPerdidas_NoVistas",
                query   =   "SELECT MAT_NOMBRE FROM MATERIA MT JOIN NOTA NT ON (MT.MAT_COD=NT.MAT_COD) "+
                            "WHERE NT.NOTA_DEFINITIVA<3 OR (NT.NOTA_DEFINITIVA IS NULL AND MT.MAT_SEMESTRE<=8)",
                            resultClass=Materia.class
        )
})
@NamedQueries({
    @NamedQuery(name = "Materia.findAll", query = "SELECT m FROM Materia m")
    , @NamedQuery(name = "Materia.findByMatCod", query = "SELECT m FROM Materia m WHERE m.matCod = :matCod")
    , @NamedQuery(name = "Materia.findByMatNombre", query = "SELECT m FROM Materia m WHERE m.matNombre = :matNombre")
    , @NamedQuery(name = "Materia.findByMatCreditos", query = "SELECT m FROM Materia m WHERE m.matCreditos = :matCreditos")
    , @NamedQuery(name = "Materia.findByMatSemestre", query = "SELECT m FROM Materia m WHERE m.matSemestre = :matSemestre")
    , @NamedQuery(name = "Materia.findByMatTipo", query = "SELECT m FROM Materia m WHERE m.matTipo = :matTipo")
    , @NamedQuery(name = "Materia.findByMatEstado", query = "SELECT m FROM Materia m WHERE m.matEstado = :matEstado")})
public class Materia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "MAT_COD")
    private String matCod;
    @Column(name = "MAT_NOMBRE")
    private String matNombre;
    @Column(name = "MAT_CREDITOS")
    private Short matCreditos;
    @Basic(optional = false)
    @Column(name = "MAT_SEMESTRE")
    private short matSemestre;
    @Basic(optional = false)
    @Column(name = "MAT_TIPO")
    private String matTipo;
    @Basic(optional = false)
    @Column(name = "MAT_ESTADO")
    private BigInteger matEstado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "materia")
    private List<MateriaOfertada> materiaOfertadaList;
    @JoinColumn(name = "PRG_ID", referencedColumnName = "PRG_ID")
    @ManyToOne(optional = false)
    private Programa prgId;

    public Materia() {
    }

    public Materia(String matCod) {
        this.matCod = matCod;
    }

    public Materia(String matCod, short matSemestre, String matTipo, BigInteger matEstado) {
        this.matCod = matCod;
        this.matSemestre = matSemestre;
        this.matTipo = matTipo;
        this.matEstado = matEstado;
    }

    public String getMatCod() {
        return matCod;
    }

    public void setMatCod(String matCod) {
        this.matCod = matCod;
    }

    public String getMatNombre() {
        return matNombre;
    }

    public void setMatNombre(String matNombre) {
        this.matNombre = matNombre;
    }

    public Short getMatCreditos() {
        return matCreditos;
    }

    public void setMatCreditos(Short matCreditos) {
        this.matCreditos = matCreditos;
    }

    public short getMatSemestre() {
        return matSemestre;
    }

    public void setMatSemestre(short matSemestre) {
        this.matSemestre = matSemestre;
    }

    public String getMatTipo() {
        return matTipo;
    }

    public void setMatTipo(String matTipo) {
        this.matTipo = matTipo;
    }

    public BigInteger getMatEstado() {
        return matEstado;
    }

    public void setMatEstado(BigInteger matEstado) {
        this.matEstado = matEstado;
    }

    @XmlTransient
    public List<MateriaOfertada> getMateriaOfertadaList() {
        return materiaOfertadaList;
    }

    public void setMateriaOfertadaList(List<MateriaOfertada> materiaOfertadaList) {
        this.materiaOfertadaList = materiaOfertadaList;
    }

    public Programa getPrgId() {
        return prgId;
    }

    public void setPrgId(Programa prgId) {
        this.prgId = prgId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (matCod != null ? matCod.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Materia)) {
            return false;
        }
        Materia other = (Materia) object;
        if ((this.matCod == null && other.matCod != null) || (this.matCod != null && !this.matCod.equals(other.matCod))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Materia[ matCod=" + matCod + " ]";
    }
    
}
