package gob.regionancash.rh.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
