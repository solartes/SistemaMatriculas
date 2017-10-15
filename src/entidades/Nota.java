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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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
@Table(name = "NOTA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Nota.findAll", query = "SELECT n FROM Nota n")
    , @NamedQuery(name = "Nota.findByPerIdentificacion", query = "SELECT n FROM Nota n WHERE n.notaPK.perIdentificacion = :perIdentificacion")
    , @NamedQuery(name = "Nota.findByMatPerIdentificacion", query = "SELECT n FROM Nota n WHERE n.notaPK.matPerIdentificacion = :matPerIdentificacion")
    , @NamedQuery(name = "Nota.findByMatCod", query = "SELECT n FROM Nota n WHERE n.notaPK.matCod = :matCod")
    , @NamedQuery(name = "Nota.findByPerAcaId", query = "SELECT n FROM Nota n WHERE n.notaPK.perAcaId = :perAcaId")
    , @NamedQuery(name = "Nota.findByNotaSetenta", query = "SELECT n FROM Nota n WHERE n.notaSetenta = :notaSetenta")
    , @NamedQuery(name = "Nota.findByNotaTreinta", query = "SELECT n FROM Nota n WHERE n.notaTreinta = :notaTreinta")
    , @NamedQuery(name = "Nota.findByNotaDefinitiva", query = "SELECT n FROM Nota n WHERE n.notaDefinitiva = :notaDefinitiva")})
public class Nota implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected NotaPK notaPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "NOTA_SETENTA")
    private BigDecimal notaSetenta;
    @Column(name = "NOTA_TREINTA")
    private BigDecimal notaTreinta;
    @Column(name = "NOTA_DEFINITIVA")
    private BigDecimal notaDefinitiva;
    @JoinColumn(name = "PER_IDENTIFICACION", referencedColumnName = "PER_IDENTIFICACION", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Estudiante estudiante;
    @JoinColumns({
        @JoinColumn(name = "MAT_PER_IDENTIFICACION", referencedColumnName = "PER_IDENTIFICACION", insertable = false, updatable = false)
        , @JoinColumn(name = "GRU_SECCION", referencedColumnName = "GRU_SECCION")
        , @JoinColumn(name = "MAT_COD", referencedColumnName = "MAT_COD", insertable = false, updatable = false)
        , @JoinColumn(name = "PER_ACA_ID", referencedColumnName = "PER_ACA_ID", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private MateriaGrupoProfesor materiaGrupoProfesor;

    public Nota() {
    }

    public Nota(NotaPK notaPK) {
        this.notaPK = notaPK;
    }

    public Nota(NotaPK notaPK, BigDecimal notaSetenta) {
        this.notaPK = notaPK;
        this.notaSetenta = notaSetenta;
    }

    public Nota(long perIdentificacion, long matPerIdentificacion, String matCod, BigDecimal perAcaId) {
        this.notaPK = new NotaPK(perIdentificacion, matPerIdentificacion, matCod, perAcaId);
    }

    public NotaPK getNotaPK() {
        return notaPK;
    }

    public void setNotaPK(NotaPK notaPK) {
        this.notaPK = notaPK;
    }

    public BigDecimal getNotaSetenta() {
        return notaSetenta;
    }

    public void setNotaSetenta(BigDecimal notaSetenta) {
        this.notaSetenta = notaSetenta;
    }

    public BigDecimal getNotaTreinta() {
        return notaTreinta;
    }

    public void setNotaTreinta(BigDecimal notaTreinta) {
        this.notaTreinta = notaTreinta;
    }

    public BigDecimal getNotaDefinitiva() {
        return notaDefinitiva;
    }

    public void setNotaDefinitiva(BigDecimal notaDefinitiva) {
        this.notaDefinitiva = notaDefinitiva;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public MateriaGrupoProfesor getMateriaGrupoProfesor() {
        return materiaGrupoProfesor;
    }

    public void setMateriaGrupoProfesor(MateriaGrupoProfesor materiaGrupoProfesor) {
        this.materiaGrupoProfesor = materiaGrupoProfesor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notaPK != null ? notaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Nota)) {
            return false;
        }
        Nota other = (Nota) object;
        if ((this.notaPK == null && other.notaPK != null) || (this.notaPK != null && !this.notaPK.equals(other.notaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Nota[ notaPK=" + notaPK + " ]";
    }
    
}
