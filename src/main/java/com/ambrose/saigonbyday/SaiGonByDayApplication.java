package com.ambrose.saigonbyday;

import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.enums.Role;
import com.ambrose.saigonbyday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties
public class SaiGonByDayApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(SaiGonByDayApplication.class, args);
	}

	public void run(String...args){
		User adminAccount = userRepository.findByRole(Role.ADMIN);
		if(null == adminAccount){
			User user = new User();

			user.setEmail("admin@gmail.com");
			user.setFullname("admin");
			//user.setSecondname("admin");
			user.setRole(Role.ADMIN);
			user.setEnabled(true);
			user.setLogin("admin");
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			userRepository.save(user);
		}
	}

}
