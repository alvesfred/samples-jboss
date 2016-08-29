package br.com.alvesfred.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Lock version entity - base model
 *
 * @author alvesfred
 *
 * @param <ID>
 * @param <VERSION>
 */
@MappedSuperclass
public abstract class BaseModelVersionEntity<ID> extends BaseModelEntity<ID> {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 9205887194329558055L;

	//@Generated(GenerationTime.NEVER)
	@Version
	@Column(name = "versionId", nullable = false)
	private Long version;

	public Long getVersion() {
		return (this.version != null) ? this.version : this.hashCode();
	}

	public void setVersion(Long versao) {
		this.version = versao;
	}
}
