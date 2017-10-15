/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Jimenez
 */
@Entity
@Table(name = "PERSONA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Persona.findAll", query = "SELECT p FROM Persona p")
    , @NamedQuery(name = "Persona.findByPerIdentificacion", query = "SELECT p FROM Persona p WHERE p.perIdentificacion = :perIdentificacion")
    , @NamedQuery(name = "Persona.findByPerTipoidentificacion", query = "SELECT p FROM Persona p WHERE p.perTipoidentificacion = :perTipoidentificacion")
    , @NamedQuery(name = "Persona.findByPerNombres", query = "SELECT p FROM Persona p WHERE p.perNombres = :perNombres")
    , @NamedQuery(name = "Persona.findByPerApellidos", query = "SELECT p FROM Persona p WHERE p.perApellidos = :perApellidos")
    , @NamedQuery(name = "Persona.findByPerGenero", query = "SELECT p FROM Persona p WHERE p.perGenero = :perGenero")
    , @NamedQuery(name = "Persona.findByPerFechanacimiento", query = "SELECT p FROM Persona p WHERE p.perFechanacimiento = :perFechanacimiento")
    , @NamedQuery(name = "Persona.findByPerRh", query = "SELECT p FROM Persona p WHERE p.perRh = :perRh")
    , @NamedQuery(name = "Persona.findByPerCorreo", query = "SELECT p FROM Persona p WHERE p.perCorreo = :perCorreo")
    , @NamedQuery(name = "Persona.findByPerEstado", query = "SELECT p FROM Persona p WHERE p.perEstado = :perEstado")
    , @NamedQuery(name = "Persona.findByPerTipo", query = "SELECT p FROM Persona p WHERE p.perTipo = :perTipo")})
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PER_IDENTIFICACION")
    private Long perIdentificacion;
    @Basic(optional = false)
    @Column(name = "PER_TIPOIDENTIFICACION")
    private String perTipoidentificacion;
    @Basic(optional = false)
    @Column(name = "PER_NOMBRES")
    private String perNombres;
    @Basic(optional = false)
    @Column(name = "PER_APELLIDOS")
    private String perApellidos;
    @Column(name = "PER_GENERO")
    private String perGenero;
    @Column(name = "PER_FECHANACIMIENTO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date perFechanacimiento;
    @Basic(optional = false)
    @Column(name = "PER_RH")
    private String perRh;
    @Basic(optional = false)
    @Column(name = "PER_CORREO")
    private String perCorreo;
    @Column(name = "PER_ESTADO")
    private String perEstado;
    @Column(name = "PER_TIPO")
    private String perTipo;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "persona")
    private Profesor profesor;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "persona")
    private Estudiante estudiante;

    public Persona() {
    }

    public Persona(Long perIdentificacion) {
        this.perIdentificacion = perIdentificacion;
    }

    public Persona(Long perIdentificacion, String perTipoidentificacion, String perNombres, String perApellidos, String perRh, String perCorreo) {
        this.perIdentificacion = perIdentificacion;
        this.perTipoidentificacion = perTipoidentificacion;
        this.perNombres = perNombres;
        this.perApellidos = perApellidos;
        this.perRh = perRh;
        this.perCorreo = perCorreo;
    }

    public Long getPerIdentificacion() {
        return perIdentificacion;
    }

    public void setPerIdentificacion(Long perIdentificacion) {
        this.perIdentificacion = perIdentificacion;
    }

    public String getPerTipoidentificacion() {
        return perTipoidentificacion;
    }

    public void setPerTipoidentificacion(String perTipoidentificacion) {
        this.perTipoidentificacion = perTipoidentificacion;
    }

    public String getPerNombres() {
        return perNombres;
    }

    public void setPerNombres(String perNombres) {
        this.perNombres = perNombres;
    }

    public String getPerApellidos() {
        return perApellidos;
    }

    public void setPerApellidos(String perApellidos) {
        this.perApellidos = perApellidos;
    }

    public String getPerGenero() {
        return perGenero;
    }

    public void setPerGenero(String perGenero) {
        this.perGenero = perGenero;
    }

    public Date getPerFechanacimiento() {
        return perFechanacimiento;
    }

    public void setPerFechanacimiento(Date perFechanacimiento) {
        this.perFechanacimiento = perFechanacimiento;
    }

    public String getPerRh() {
        return perRh;
    }

    public void setPerRh(String perRh) {
        this.perRh = perRh;
    }

    public String getPerCorreo() {
        return perCorreo;
    }

    public void setPerCorreo(String perCorreo) {
        this.perCorreo = perCorreo;
    }

    public String getPerEstado() {
        return perEstado;
    }

    public void setPerEstado(String perEstado) {
        this.perEstado = perEstado;
    }

    public String getPerTipo() {
        return perTipo;
    }

    public void setPerTipo(String perTipo) {
        this.perTipo = perTipo;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (perIdentificacion != null ? perIdentificacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Persona)) {
            return false;
        }
        Persona other = (Persona) object;
        if ((this.perIdentificacion == null && other.perIdentificacion != null) || (this.perIdentificacion != null && !this.perIdentificacion.equals(other.perIdentificacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Persona[ perIdentificacion=" + perIdentificacion + " ]";
    }
    
}
