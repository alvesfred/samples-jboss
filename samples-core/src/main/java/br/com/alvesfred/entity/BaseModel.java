package br.com.alvesfred.entity;

import java.io.Serializable;

/**
 * Base Model Interface
 *
 * @author alvesfred
 *
 */
public interface BaseModel<ID> extends Serializable {

	/**
	 * Set ID definition
	 *
	 * @param id
	 */
	void setId(ID id);
	
	/**
	 * Get ID
	 *
	 * @return
	 */
	ID getId();
}
