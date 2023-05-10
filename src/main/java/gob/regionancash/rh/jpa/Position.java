package gob.regionancash.rh.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "position")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "name")
    private String name;
    @Size(max = 1)
    @Column(name = "level")
    private String level;
    @Size(max = 6)
    @Column(name = "cod_pdt")
    private String codPdt;
    @Column(name = "nivel")
    private Integer nivel;
    @Column(name = "orden_firma")
    private Integer ordenFirma;
    @Size(max = 15)
    @Column(name = "abreviatura")
    private String abrev;
    @Column(name = "estado")
    private Character status='1';

    public String getCodPdt() {
        return codPdt;
    }

    public void setCodPdt(String codPdt) {
        this.codPdt = codPdt;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getOrdenFirma() {
        return ordenFirma;
    }

    public void setOrdenFirma(Integer ordenFirma) {
        this.ordenFirma = ordenFirma;
    }

    public String getAbreviatura() {
        return abrev;
    }

    public void setAbreviatura(String abreviatura) {
        this.abrev = abreviatura;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Position() {
    }

    public Position(Integer id) {
        this.id = id;
    }

    public Position(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Position)) {
            return false;
        }
        Position other = (Position) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.regionancash.rh.jpa.Position[ id=" + id + " ]";
    }

}
