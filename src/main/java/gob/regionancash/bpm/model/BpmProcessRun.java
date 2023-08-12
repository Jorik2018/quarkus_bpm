/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

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
    private String subject;
    
    public Integer getDelegatedUserId() {
		return delegatedUserId;
	}

    
    
	public Date getFinalDate() {
		return finalDate;
	}



	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}



	public Date getLimitDate() {
		return limitDate;
	}



	public Integer getYear() {
		return year;
	}



	public void setYear(Integer year) {
		this.year = year;
	}



	public Integer getNumber() {
		return number;
	}



	public void setNumber(Integer number) {
		this.number = number;
	}



	public String getSubject() {
		return subject;
	}



	public void setSubject(String subject) {
		this.subject = subject;
	}



	public void setLimitDate(Date limitDate) {
		this.limitDate = limitDate;
	}



	public boolean isPrescribed() {
		return prescribed;
	}



	public void setPrescribed(boolean prescribed) {
		this.prescribed = prescribed;
	}



	public String getDelegatedUser() {
		return delegatedUser;
	}



	public void setDelegatedUser(String delegatedUser) {
		this.delegatedUser = delegatedUser;
	}



	public void setDelegatedUserId(Integer delegatedUserId) {
		this.delegatedUserId = delegatedUserId;
	}

	public List getPeoples() {
		return peoples;
	}

	public void setPeoples(List peoples) {
		this.peoples = peoples;
	}

	private boolean canceled;
    
    public Object getExt() {
		return ext;
	}

	public void setExt(Object ext) {
		this.ext = ext;
	}

	public BpmProcessRun() {
    }

    public BpmProcessRun(Integer id) {
        this.id = id;
    }

    public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public BpmProcessRun(Integer id, Date insertDate) {
        this.id = id;
        this.insertDate = insertDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public BpmActivity getActivity() {
		return activity;
	}

	public void setActivity(BpmActivity activity) {
		this.activity = activity;
	}

	public BpmDispatch getDispatch() {
		return dispatch;
	}

	public void setDispatch(BpmDispatch dispatch) {
		this.dispatch = dispatch;
	}

	public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Integer getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(Integer dependencyId) {
        this.dependencyId = dependencyId;
    }

    public Integer getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(Integer peopleId) {
        this.peopleId = peopleId;
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
        if (!(object instanceof BpmProcessRun)) {
            return false;
        }
        BpmProcessRun other = (BpmProcessRun) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ocpsoft.rewrite.servlet.BpmProcessRun[ id=" + id + " ]";
    }
    
}
