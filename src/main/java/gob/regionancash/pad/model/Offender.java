package gob.regionancash.pad.model;


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
@Table(name = "pad_offender")
public class Offender implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    private String fullName;

    private String code;

    @Size(max = 100)
    private String address;

    @Size(max = 100)
    private String position;

    private boolean canceled;

    private Integer dispatchId;

}
