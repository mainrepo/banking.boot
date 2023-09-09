package org.microjava.config;

import java.util.Arrays;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.microjava.application.ApplicationInit;
import org.microjava.application.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This file is part of pre-tested boot chassis library.
 * The ServiceConfig is for configuring CXF.
 *
 * @author  Gaurav J.
 * @version 1.0
 * @since   Jun 12, 2019
 */

@Configuration
public class ServiceConfig {

	@Value("${cxf.path}")
	private String basePath;
	
	@Autowired
	private ApplicationService service;

	@Bean(destroyMethod = "destroy")
	public Server jaxRsServer(Bus bus) {
		final JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
		factory.setApplication(new ApplicationInit());
		factory.setServiceBean(service);
		factory.setProvider(new JacksonJsonProvider());
		factory.setFeatures(Arrays.asList(createOpenApiFeature()));
		factory.setBus(bus);
		factory.setAddress("/");
		return factory.create();
	}
	
	private Feature createOpenApiFeature() {
		final OpenApiFeature openApiFeature = new OpenApiFeature();
		openApiFeature.setPrettyPrint(true);
		openApiFeature.setTitle("Microservice Chassis - Multi Module Boot");
		openApiFeature.setDescription("The project helps to quickly start with the multi module microservice development. The project is based on Spring boot, Liquidbase, CXF & OpenApi 3.0 with gradle build tool.");
		openApiFeature.setVersion("1.0");
		openApiFeature.setSwaggerUiConfig(new SwaggerUiConfig().url("/api/openapi.json"));
		openApiFeature.setContactName("Gaurav Joshi");
		openApiFeature.setContactEmail("gaurav.joshi@email.com");
		openApiFeature.setContactUrl("http://linkedin.com/in/shabdsnuti");
		openApiFeature.setLicense("Apache 2.0 License");
		openApiFeature.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
		return openApiFeature;
	}
}