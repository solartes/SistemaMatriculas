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
@Table(name = "GRUPO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Grupo.findAll", query = "SELECT g FROM Grupo g")
    , @NamedQuery(name = "Grupo.findByGruSeccion", query = "SELECT g FROM Grupo g WHERE g.gruSeccion = :gruSeccion")
    , @NamedQuery(name = "Grupo.findByGruCupos", query = "SELECT g FROM Grupo g WHERE g.gruCupos = :gruCupos")
    , @NamedQuery(name = "Grupo.findByGruEstado", query = "SELECT g FROM Grupo g WHERE g.gruEstado = :gruEstado")})
public class Grupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "GRU_SECCION")
    private String gruSeccion;
    @Basic(optional = false)
    @Column(name = "GRU_CUPOS")
    private short gruCupos;
    @Basic(optional = false)
    @Column(name = "GRU_ESTADO")
    private BigInteger gruEstado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupo")
    private List<MateriaGrupoProfesor> materiaGrupoProfesorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gruSeccion")
    private List<Horario> horarioList;

    public Grupo() {
    }

    public Grupo(String gruSeccion) {
        this.gruSeccion = gruSeccion;
    }

    public Grupo(String gruSeccion, short gruCupos, BigInteger gruEstado) {
        this.gruSeccion = gruSeccion;
        this.gruCupos = gruCupos;
        this.gruEstado = gruEstado;
    }

    public String getGruSeccion() {
        return gruSeccion;
    }

    public void setGruSeccion(String gruSeccion) {
        this.gruSeccion = gruSeccion;
    }

    public short getGruCupos() {
        return gruCupos;
    }

    public void setGruCupos(short gruCupos) {
        this.gruCupos = gruCupos;
    }

    public BigInteger getGruEstado() {
        return gruEstado;
    }

    public void setGruEstado(BigInteger gruEstado) {
        this.gruEstado = gruEstado;
    }

    @XmlTransient
    public List<MateriaGrupoProfesor> getMateriaGrupoProfesorList() {
        return materiaGrupoProfesorList;
    }

    public void setMateriaGrupoProfesorList(List<MateriaGrupoProfesor> materiaGrupoProfesorList) {
        this.materiaGrupoProfesorList = materiaGrupoProfesorList;
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
        hash += (gruSeccion != null ? gruSeccion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Grupo)) {
            return false;
        }
        Grupo other = (Grupo) object;
        if ((this.gruSeccion == null && other.gruSeccion != null) || (this.gruSeccion != null && !this.gruSeccion.equals(other.gruSeccion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Grupo[ gruSeccion=" + gruSeccion + " ]";
    }
    
}
