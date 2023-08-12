package gob.regionancash.rh.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isobit.directory.model.Company;
import org.isobit.directory.model.Dependency;
import org.isobit.directory.model.People;
import org.isobit.directory.model.Province;

@Data
@Entity
@Table(name = "contract")
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "people_id")
    private int peopleId;
    
    @Column(name = "people_id", insertable = false, updatable = false)
    private long peopleIdLong;
    
    @Column(name = "employee_id")
    private Integer employeeId;
    
    @JoinColumn(name = "company_id", referencedColumnName = "id_dir", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    private Company company;
    
    @Column(name = "company_id")
    private Integer companyId;
    @Column(name = "dependency_id")
    
    private Integer dependencyId;
    @JoinColumn(name = "dependency_id", referencedColumnName = "id_dep", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    
    private Dependency dependency;
    @JoinColumn(name = "people_id", referencedColumnName = "id_dir", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    
    private People people;
    @JoinColumn(name = "position_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    private Position position;
    @Basic(optional = false)
    @Column(name = "remunerative_level_id")
    private Integer remunerativeLevelId;
    @Transient
    private String remunerativeLevelName;
    @Column(name = "position_id")
    private Integer positionId;
    @Column(name = "fecha_ini")
    @Temporal(TemporalType.DATE)
    private Date fechaIni;
    @Basic(optional = true)
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @Basic(optional = true)
    @Column(name = "province_id")
    private String provinceId;
    @Transient
    private Province province;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_reg")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaReg;

    @Basic(optional = true)
    private String document;
    
    @Basic(optional = true)
    private Boolean charge;

    private Boolean canceled=false;
    
    @Basic(optional = true)
    private Boolean active = Boolean.TRUE;

    @Basic(optional = false)
    @NotNull
    private boolean status = Boolean.TRUE;

    @Transient
    private Object ext;

}
