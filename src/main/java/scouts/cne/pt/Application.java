package scouts.cne.pt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.~
 * 
 * @author anco62000465 2018-09-11
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer
{
	public static void main( String[] args )
	{
		SpringApplication.run( Application.class, args );
	}
}
