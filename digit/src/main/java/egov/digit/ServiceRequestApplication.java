package egov.digit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceRequestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRequestApplication.class, args);
		System.out.println("Service-request is up and running!!");
	}

}
