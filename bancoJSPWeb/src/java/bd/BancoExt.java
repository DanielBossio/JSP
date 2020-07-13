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
@Table(name = "bancoext")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BancoExt.findAll", query = "SELECT b FROM BancoExt b")
    , @NamedQuery(name = "BancoExt.findByNombre", query = "SELECT b FROM BancoExt b WHERE b.nombre = :nombre")})
public class BancoExt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "banco")
    private List<CuentaExt> cuentaExtList;

    public BancoExt() {
    }

    public BancoExt(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<CuentaExt> getCuentaExtList() {
        return cuentaExtList;
    }

    public void setCuentaExtList(List<CuentaExt> cuentaExtList) {
        this.cuentaExtList = cuentaExtList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BancoExt)) {
            return false;
        }
        BancoExt other = (BancoExt) object;
        if ((this.nombre == null && other.nombre != null) || (this.nombre != null && !this.nombre.equals(other.nombre))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bd.BancoExt[ nombre=" + nombre + " ]";
    }
    
}
