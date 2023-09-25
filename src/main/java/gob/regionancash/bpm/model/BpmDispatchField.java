package gob.regionancash.bpm.model;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "bpm_dispatch_field")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmDispatchField extends PanacheEntityBase implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	@Column(name = "field_id")
	private Integer fieldId;
	@Column(name = "run_id")
	private Integer dispatchId;
	private boolean canceled;
	@Size(max = 200)
	@Column(name = "value")
	private String value;
	@Transient
	private String name;
	@Transient
	private Object data;

}
