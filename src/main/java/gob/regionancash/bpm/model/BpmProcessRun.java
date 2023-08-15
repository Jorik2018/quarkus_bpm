package gob.regionancash.bpm.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bpm_process_run")
public class BpmProcessRun extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Column(name = "activity_id")
    private Integer activityId;
    @Transient
    private BpmActivity activity;
    @Column(name = "entity_id")
    private Integer entityId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "insert_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertDate;
    @Column(name = "limit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date limitDate;
    @Column(name = "final_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalDate;
    @Column(name = "dependency_id")
    private Integer dependencyId;
    @Column(name = "people_id")
    private Integer peopleId;
    @Column(name = "delegated_user_id")
    private Integer delegatedUserId;
    @Transient
    private BpmDispatch dispatch;

    private boolean prescribed=false;
    
    @Transient
    private String delegatedUser;

    @Transient
    private Object ext;

    @Transient
    private List peoples;
    
    private Integer year;
    
    private Integer number;

@Column(length = 2048)	
    private String subject;
    
	private boolean canceled;

}
