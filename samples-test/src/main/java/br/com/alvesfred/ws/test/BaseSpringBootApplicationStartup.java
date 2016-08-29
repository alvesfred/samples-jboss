package br.com.alvesfred.ws.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Spring Boot Start Class
 *
 * @author alvesfred
 *
 */
@Controller
@EnableAutoConfiguration
//@SpringBootApplication
public class BaseSpringBootApplicationStartup {

	@RequestMapping("/infoMock")
	@ResponseBody
	public String infoMock() {
		return "Mock - WS Test Client!";
	}

	@RequestMapping("/homeMock")
	@ResponseBody
	public String homeMock() {
		return "Spring Boot Tests for Mock!";
	}

	/**
	 * main
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(BaseSpringBootApplicationStartup.class);
	}
}
