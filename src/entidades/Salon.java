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
@Table(name = "SALON")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Salon.findAll", query = "SELECT s FROM Salon s")
    , @NamedQuery(name = "Salon.findBySalId", query = "SELECT s FROM Salon s WHERE s.salId = :salId")
    , @NamedQuery(name = "Salon.findBySalNombre", query = "SELECT s FROM Salon s WHERE s.salNombre = :salNombre")
    , @NamedQuery(name = "Salon.findBySalCupo", query = "SELECT s FROM Salon s WHERE s.salCupo = :salCupo")
    , @NamedQuery(name = "Salon.findBySalTipo", query = "SELECT s FROM Salon s WHERE s.salTipo = :salTipo")})
public class Salon implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "SAL_ID")
    private BigDecimal salId;
    @Column(name = "SAL_NOMBRE")
    private String salNombre;
    @Basic(optional = false)
    @Column(name = "SAL_CUPO")
    private short salCupo;
    @Column(name = "SAL_TIPO")
    private String salTipo;
    @JoinColumn(name = "FAC_ID", referencedColumnName = "FAC_ID")
    @ManyToOne(optional = false)
    private Facultad facId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "salon")
    private List<Horario> horarioList;

    public Salon() {
    }

    public Salon(BigDecimal salId) {
        this.salId = salId;
    }

    public Salon(BigDecimal salId, short salCupo) {
        this.salId = salId;
        this.salCupo = salCupo;
    }

    public BigDecimal getSalId() {
        return salId;
    }

    public void setSalId(BigDecimal salId) {
        this.salId = salId;
    }

    public String getSalNombre() {
        return salNombre;
    }

    public void setSalNombre(String salNombre) {
        this.salNombre = salNombre;
    }

    public short getSalCupo() {
        return salCupo;
    }

    public void setSalCupo(short salCupo) {
        this.salCupo = salCupo;
    }

    public String getSalTipo() {
        return salTipo;
    }

    public void setSalTipo(String salTipo) {
        this.salTipo = salTipo;
    }

    public Facultad getFacId() {
        return facId;
    }

    public void setFacId(Facultad facId) {
        this.facId = facId;
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
        hash += (salId != null ? salId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Salon)) {
            return false;
        }
        Salon other = (Salon) object;
        if ((this.salId == null && other.salId != null) || (this.salId != null && !this.salId.equals(other.salId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Salon[ salId=" + salId + " ]";
    }
    
}
