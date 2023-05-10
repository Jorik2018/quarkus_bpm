package gob.regionancash.bpm.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import gob.regionancash.rh.jpa.Position;

@Entity
@Table(name = "bpm_activity")
public class BpmActivity extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Size(max = 200)
    @Column(name = "description")
    private String description;
    @Column(name = "dependency_id")
    private Integer dependencyId;
    @Column(name = "days_limit")
    private Integer limit;
    @Column(name = "position_id")
    private Integer positionId;
    @JoinColumn(name = "position_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    private Position position;
    @Column(name = "process_id")
    private Integer processId;
    @Transient
    private List<BpmField> fields;
    @Transient
    private Number progress;
    @Transient
    private String msg;

    public BpmActivity clone() {
    	BpmActivity a=new BpmActivity();
    	
    	a.id=this.id;
    	a.description=this.description;
    	a.dependencyId=this.dependencyId;
    	a.limit=this.limit;
    	a.positionId=this.positionId;
    	a.position=this.position;
    	a.processId=this.processId;
    	a.fields=this.fields;
    	a.progress=this.progress;
    	a.msg=this.msg;
    	return a;
    	
    }
    
    
    public Number getProgress() {
		return progress;
	}

	public void setProgress(Number progress) {
		this.progress = progress;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<BpmField> getFields() {
		return fields;
	}

	public void setFields(List<BpmField> fields) {
		this.fields = fields;
	}

	public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public BpmActivity() {
    }

    public BpmActivity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(Integer dependencyId) {
        this.dependencyId = dependencyId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
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
        if (!(object instanceof BpmActivity)) {
            return false;
        }
        BpmActivity other = (BpmActivity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.isobit.process.jpa.BpmActivity[ id=" + id + ",description="+description+" ]";
    }

}
