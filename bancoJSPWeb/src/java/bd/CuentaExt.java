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
 * @author Win 8.1
 */
@Entity
@Table(name = "cuentaext")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CuentaExt.findAll", query = "SELECT c FROM CuentaExt c")
    , @NamedQuery(name = "CuentaExt.findByNumcuenta", query = "SELECT c FROM CuentaExt c WHERE c.numcuenta = :numcuenta")
    , @NamedQuery(name = "CuentaExt.findByPropietario", query = "SELECT c FROM CuentaExt c WHERE c.propietario = :propietario")
    , @NamedQuery(name = "CuentaExt.findByMonto", query = "SELECT c FROM CuentaExt c WHERE c.monto = :monto")})
public class CuentaExt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "numcuenta")
    private String numcuenta;
    @Basic(optional = false)
    @Column(name = "propietario")
    private String propietario;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto")
    private Float monto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuenta")
    private List<Transferencia> transferenciaList;
    @JoinColumn(name = "banco", referencedColumnName = "nombre")
    @ManyToOne(optional = false)
    private BancoExt banco;

    public CuentaExt() {
    }

    public CuentaExt(String numcuenta) {
        this.numcuenta = numcuenta;
    }

    public CuentaExt(String numcuenta, String propietario) {
        this.numcuenta = numcuenta;
        this.propietario = propietario;
    }

    public String getNumcuenta() {
        return numcuenta;
    }

    public void setNumcuenta(String numcuenta) {
        this.numcuenta = numcuenta;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public Float getMonto() {
        return monto;
    }

    public void setMonto(Float monto) {
        this.monto = monto;
    }

    @XmlTransient
    public List<Transferencia> getTransferenciaList() {
        return transferenciaList;
    }

    public void setTransferenciaList(List<Transferencia> transferenciaList) {
        this.transferenciaList = transferenciaList;
    }

    public BancoExt getBanco() {
        return banco;
    }

    public void setBanco(BancoExt banco) {
        this.banco = banco;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numcuenta != null ? numcuenta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CuentaExt)) {
            return false;
        }
        CuentaExt other = (CuentaExt) object;
        if ((this.numcuenta == null && other.numcuenta != null) || (this.numcuenta != null && !this.numcuenta.equals(other.numcuenta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bd.CuentaExt[ numcuenta=" + numcuenta + " ]";
    }
    
}
