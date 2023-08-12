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

import lombok.Data;

@Data
@Entity
@Table(name = "position")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    private String name;

    @Size(max = 1)
    private String level;
    @Size(max = 6)
    @Column(name = "cod_pdt")
    private String codPdt;
    
    private Integer nivel;
    
    @Column(name = "orden_firma")
    private Integer ordenFirma;

    @Size(max = 15)
    @Column(name = "abreviatura")
    private String abrev;

    private Character status='1';

}
