package ca.mcgill.ecse321.project6;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import ca.mcgill.ecse321.project6.controller.configuration.AndroidProperties;
import ca.mcgill.ecse321.project6.controller.configuration.WebFrontendProperties;
import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.persistence.IDb;
import ca.mcgill.ecse321.project6.service.authentication.AuthenticationService;

@SpringBootApplication
public class TreePLESpringApplication extends SpringBootServletInitializer {
	
	@Autowired
	private IDb db;
	
	@Autowired
	private TreePLE tp;
	
	@Autowired
	private AndroidProperties androidProperties;
	
	@Autowired
	private WebFrontendProperties webFrontendProperties;
	
	public static void main(String[] args) {
		SpringApplication.run(TreePLESpringApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.getConfiguration().setSourceNamingConvention(NamingConventions.NONE)
					.setDestinationNamingConvention(NamingConventions.NONE);
		return modelMapper;
	}
	
	@Bean
	public TreePLE treeple() {
		TreePLE tp = (TreePLE) db.loadFromDb();
		if (tp == null) {
			tp = new TreePLE();
			db.saveToDb(tp);
		}
		return tp;
	}
	
	@Bean
	public AuthenticationService authService() {
		return new AuthenticationService(tp);
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				String frontendUrl = "http://"+webFrontendProperties.getIp() + ":" + webFrontendProperties.getPort();
				String androidUrl = "http://"+androidProperties.getIp()+":"+androidProperties.getPort();
				registry.addMapping("/**").allowedOrigins(frontendUrl, androidUrl, "http://localhost:8087", "http://127.0.0.1:8087");
			}
		};
	}

}
