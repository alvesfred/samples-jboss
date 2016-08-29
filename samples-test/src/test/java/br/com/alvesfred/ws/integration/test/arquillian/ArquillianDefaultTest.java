package br.com.alvesfred.ws.integration.test.arquillian;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Arquillian Default Test
 *
 * @author alvesfred
 *
 */
//@WarpTest - extension
@RunWith(Arquillian.class)
public class ArquillianDefaultTest {

	//@ArquillianResource
	//private URL deploymentURL;

	// Arquillian Extension
	//@ArquillianResource
    //private RestContext restContext;

	@Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test-arquillian.war")
				.addPackages(true, Filters.exclude(".*Test.*"),
						ArquillianDefaultTest.class.getPackage())
				.addAsLibraries(Maven.resolver().resolve("javax.ws.rs:javax.ws.rs-api:2.0").withTransitivity().asFile())
				.addAsLibraries(Maven.resolver().resolve("org.jboss.arquillian.extension:arquillian-rest-warp-api:1.0.0.Alpha2").withTransitivity().asFile())
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

	@Test
	public void testTrue() {
		//Assert.fail("Not yet implemented");
		Assert.assertTrue(true);
	}

	@RunAsClient
	@Test
	//public void testWS(@ArquillianResteasyResource WebTarget webTarget) {
	public void testWS(@ArquillianResource URL deploymentURL) throws URISyntaxException {
		//final <Entity> result = webTarget.path("/rest/{var}").request().post(null).readEntity(<Entity>.class);
		WebTarget target = buildWebTarget(deploymentURL);
		System.out.println("WebTarget: " + target);
		assertNotNull(target);
	}

	protected static WebTarget buildWebTarget(URL deploymentURL) throws URISyntaxException {
		Client client = ClientBuilder.newClient();
		return client.target(deploymentURL.toURI());
	}
}
