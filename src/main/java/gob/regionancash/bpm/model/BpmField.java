package gob.regionancash.bpm.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Data
@Entity
@Table(name = "bpm_field")
public class BpmField extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 100)
    private String name;
    @Size(max = 100)
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
    
}
