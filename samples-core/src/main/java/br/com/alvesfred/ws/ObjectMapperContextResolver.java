package br.com.alvesfred.ws;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Provider
 * 
 * @author <a href="mailto:fredericocerqueiraalves@gmail.com">Frederico Alves</a>
 *
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
	
	private final ObjectMapper mapper;

    public ObjectMapperContextResolver() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Module() {

        	private final Version version = new Version(1, 0, 0, "");
        	
			@Override
			public String getModuleName() {
				return "NullResolverModule";
			}

			@Override
			public void setupModule(SetupContext arg0) {
			}

			@Override
			public Version version() {
				return version;
			}
		});

		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }

    @Override
    public ObjectMapper getContext(Class<?> cls) {
        return mapper;
    }

}
