/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd;

import java.io.Serializable;
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
 * @author Win 8.1
 */
@Entity
@Table(name = "servicio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Servicio.findAll", query = "SELECT s FROM Servicio s")
    , @NamedQuery(name = "Servicio.findByRefPago", query = "SELECT s FROM Servicio s WHERE s.refPago = :refPago")
    , @NamedQuery(name = "Servicio.findByTitulo", query = "SELECT s FROM Servicio s WHERE s.titulo = :titulo")
    , @NamedQuery(name = "Servicio.findByDescripcion", query = "SELECT s FROM Servicio s WHERE s.descripcion = :descripcion")
    , @NamedQuery(name = "Servicio.findByMonto", query = "SELECT s FROM Servicio s WHERE s.monto = :monto")
    , @NamedQuery(name = "Servicio.findByIscancelado", query = "SELECT s FROM Servicio s WHERE s.iscancelado = :iscancelado")})
public class Servicio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "refPago")
    private String refPago;
    @Basic(optional = false)
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto")
    private Float monto;
    @Column(name = "iscancelado")
    private Boolean iscancelado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "servicio")
    private List<Pago> pagoList;

    public Servicio() {
    }

    public Servicio(String refPago) {
        this.refPago = refPago;
    }

    public Servicio(String refPago, String titulo) {
        this.refPago = refPago;
        this.titulo = titulo;
    }

    public String getRefPago() {
        return refPago;
    }

    public void setRefPago(String refPago) {
        this.refPago = refPago;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getMonto() {
        return monto;
    }

    public void setMonto(Float monto) {
        this.monto = monto;
    }

    public Boolean getIscancelado() {
        return iscancelado;
    }

    public void setIscancelado(Boolean iscancelado) {
        this.iscancelado = iscancelado;
    }

    @XmlTransient
    public List<Pago> getPagoList() {
        return pagoList;
    }

    public void setPagoList(List<Pago> pagoList) {
        this.pagoList = pagoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (refPago != null ? refPago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Servicio)) {
            return false;
        }
        Servicio other = (Servicio) object;
        if ((this.refPago == null && other.refPago != null) || (this.refPago != null && !this.refPago.equals(other.refPago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bd.Servicio[ refPago=" + refPago + " ]";
    }
    
}
