package com.cvtv.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.cvtv.app.pwa.CustomBootstrapListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application implements VaadinServiceInitListener
{

	public static void main( String[] args )
	{
		SpringApplication.run( Application.class, args );
	}

	@Override
	public void serviceInit( ServiceInitEvent event )
	{
		event.addBootstrapListener( new CustomBootstrapListener() );
	}
}
