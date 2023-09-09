package org.microjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main application service launcher.
 * 
 * @author  Gaurav J
 * @since   10 May 19
 * @version 1.0
 */
@SpringBootApplication(scanBasePackageClasses = { Bootup.class })
public class Bootup {

	public static void main(String[] args) {
		System.out.println("---------------> " + args[0] + " <---------------");
		SpringApplication.run(Bootup.class, args);
	}
}