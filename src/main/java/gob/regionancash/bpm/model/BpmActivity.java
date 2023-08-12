package gob.regionancash.bpm.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import java.io.Serializable;
import java.util.List;

import gob.regionancash.rh.model.Position;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "bpm_activity")
public class BpmActivity extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max = 200)
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
    
}
