package gob.regionancash.bpm.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "bpm_relation")
public class BpmRelation extends PanacheEntityBase implements Serializable {

    public static void main(String[] args) {
        Scanner tokenize = new Scanner("$3 <= 4000");
        ArrayList tokens = new ArrayList();
        while (tokenize.hasNext()) {
            tokens.add(tokenize.next());
        }
        System.out.println(tokens);
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "activity_from_id")
    private Integer activityFromId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activity_to_id")
    private int activityToId;
    @Size(max = 40)
    @Column(name = "_condition")
    private String condition;
    @Transient
    private Object ext;

    public BpmRelation() {
    }

    public BpmRelation(Integer id) {
        this.id = id;
    }

    public BpmRelation(Integer id, int activityToId) {
        this.id = id;
        this.activityToId = activityToId;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityFromId() {
        return activityFromId;
    }

    public void setActivityFromId(Integer activityFromId) {
        this.activityFromId = activityFromId;
    }

    public int getActivityToId() {
        return activityToId;
    }

    public void setActivityToId(int activityToId) {
        this.activityToId = activityToId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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
        if (!(object instanceof BpmRelation)) {
            return false;
        }
        BpmRelation other = (BpmRelation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.isobit.process.jpa.BpmRelation[ id=" + id + " ]";
    }

}
