package gob.regionancash.bpm.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "bpm_field")
public class BpmField extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 100)
    private String name;
    @Size(max = 100)
    @Column(name = "label")
    private String label;
    @Size(max = 100)
    private String description;
    private String type;
    @Column(name = "activity_id")
    private Integer activityId;
    @Transient
    private Object value;
    @Transient
    private Integer dispatchFieldId;
    private Integer weight;
    private boolean required;

    public BpmField() {
    }

    public BpmField clone() {
    	BpmField field=new BpmField();
    	field.id=this.id;
    	field.name=this.name;
    	field.label=this.label;
    	field.description=this.description;
    	field.type=this.type;
    	field.activityId=this.activityId;
    	field.value=this.value;
    	field.weight=this.weight;
    	field.required=this.required;
    	return field;
    }
    
    public BpmField(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDispatchFieldId() {
		return dispatchFieldId;
	}

	public void setDispatchFieldId(Integer dispatchFieldId) {
		this.dispatchFieldId = dispatchFieldId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
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
        if (!(object instanceof BpmField)) {
            return false;
        }
        BpmField other = (BpmField) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.isobit.process.jpa.BpmField[ id=" + id + " ]";
    }

}
