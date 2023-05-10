package gob.regionancash.rh.jpa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.isobit.directory.jpa.Province;
import org.isobit.directory.jpa.Dependency;
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
import org.isobit.directory.jpa.Company;
import org.isobit.directory.jpa.People;

@Entity
@Table(name = "contract")
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
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
    @Column(name = "document")
    private String document;
    @Basic(optional = true)
    @Column(name = "charge")
    private Boolean charge;
    private Boolean canceled=false;
    @Basic(optional = true)
    @Column(name = "active")
    private Boolean active = Boolean.TRUE;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private boolean status = Boolean.TRUE;

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public String getRemunerativeLevelName() {
        return remunerativeLevelName;
    }

    public void setRemunerativeLevelName(String remunerativeLevelName) {
        this.remunerativeLevelName = remunerativeLevelName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getRemunerativeLevelId() {
        return remunerativeLevelId;
    }

    public void setRemunerativeLevelId(Integer remunerativeLevelId) {
        this.remunerativeLevelId = remunerativeLevelId;
    }

    public long getPeopleIdLong() {
        return peopleIdLong;
    }

    public void setPeopleIdLong(long peopleIdLong) {
        this.peopleIdLong = peopleIdLong;
    }

    public Integer getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(Integer dependencyId) {
        this.dependencyId = dependencyId;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Boolean getCharge() {
        return charge;
    }

    public void setCharge(Boolean charge) {
        this.charge = charge;
    }

    @Transient
    private Object ext;

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public Contract() {
    }

    public Contract(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(int peopleId) {
        this.peopleId = peopleId;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public People getPeople() {
        return people;
    }

    public void setPeople(People people) {
        this.people = people;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getFechaReg() {
        return fechaReg;
    }

    public void setFechaReg(Date fechaReg) {
        this.fechaReg = fechaReg;
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
        if (!(object instanceof Contract)) {
            return false;
        }
        Contract other = (Contract) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.regionancash.rh.jpa.Consejero[ id=" + id + " ]";
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

}
