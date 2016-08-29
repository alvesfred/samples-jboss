package br.com.alvesfred.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL) // using JTA by container
@Entity
@Audited
@Table(name="person")
@SequenceGenerator(name = "hilo_sequence_generator", sequenceName = "seq_id_person", allocationSize = 1)
@AttributeOverrides({
    @AttributeOverride(name="id", column=@Column(name = "person_id", unique = true, nullable = false, scale = 0)),
})

@NamedQueries({
    @NamedQuery(
    		name  = PersonEntity.LIST_BY_ID_NAME, 
    		query = "select new PersonEntity(p.id) "
    			  + "from PersonEntity p "
    			  + "where p.name = :name",
			hints = { 
					@QueryHint (name="org.hibernate.cacheable", value="true"),
			}
    ),
})

@NamedNativeQueries(
		value = {
			@NamedNativeQuery(
				name  = PersonEntity.LIST_NATIVE_BY_NAME,
				query = PersonEntity.SQL_NATIVE_BY_NAME,
			    resultSetMapping = PersonEntity.MAPPING_NATIVE_BY_NAME),
})

@SqlResultSetMappings(
		value = {
			@SqlResultSetMapping(		
					name    = PersonEntity.MAPPING_NATIVE_BY_NAME,
		    		columns = {
						@ColumnResult(name="size")}),
	    }
)
public class PersonEntity extends BaseModelVersionEntity<Long> {

	private static final long serialVersionUID = -1355055022303449688L;

	// Named Queries
	public static final String LIST_BY_ID_NAME = "PersonEntity.listByIdName";

	// Native Named Queries
	public static final String LIST_NATIVE_BY_NAME = "PersonEntity.nativeListByIdName";

	// SQL
	protected static final String SQL_NATIVE_BY_NAME = 
			" select count(*) as size "
		  + " from person "
		  + " where name = :name ";

	// MAPPING 
	public static final String MAPPING_NATIVE_BY_NAME = "PersonEntity.mappingListByIdName";

	@Lob
	@Column(name = "jsonInfo", nullable = false)
	private String jsonInfo; // a complete file text information, using json jackson parser
	
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "age", nullable = true)
	private int age;

}
