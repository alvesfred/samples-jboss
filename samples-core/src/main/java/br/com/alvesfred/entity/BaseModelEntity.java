package br.com.alvesfred.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Model for Entities
 *
 * @author alvesfred
 *
 * @param <ID>
 */
@MappedSuperclass
public abstract class BaseModelEntity<ID> implements BaseModel<ID> {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 3958601406206249649L;

	//@Id
	//@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_id_generator")
    @Id
    @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hilo_sequence_generator")
	@GenericGenerator(
	        name       = "hilo_sequence_generator",
	        strategy   = "org.hibernate.id.enhanced.SequenceStyleGenerator",
	        parameters = { // default parameters
	                @Parameter(name = "sequence_name",  value = "sq_id"),
	                @Parameter(name = "initial_value",  value = "1"),
	                @Parameter(name = "increment_size", value = "1"),
	                @Parameter(name = "optimizer",      value = "hilo")
    })
	protected ID id;

	public BaseModelEntity() {
	}

	public BaseModelEntity(ID id) {
		setId(id);
	}

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.id);

		return hcb.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass().isAssignableFrom(BaseModelEntity.class)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		BaseModelEntity<ID> other = (BaseModelEntity<ID>) obj;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getId(), other.getId());
		
		return eb.isEquals();
	}
    
}