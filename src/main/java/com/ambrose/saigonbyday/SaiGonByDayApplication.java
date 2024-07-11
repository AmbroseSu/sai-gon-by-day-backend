package com.ambrose.saigonbyday;

import com.ambrose.saigonbyday.entities.City;
import com.ambrose.saigonbyday.entities.User;
import com.ambrose.saigonbyday.entities.enums.Role;
import com.ambrose.saigonbyday.repository.CityRepository;
import com.ambrose.saigonbyday.repository.UserRepository;
import com.ambrose.saigonbyday.services.CityService;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class SaiGonByDayApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private CityService cityService;


	public static void main(String[] args) {
		SpringApplication.run(SaiGonByDayApplication.class, args);
	}

	public void run(String...args) throws IOException {
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
		List<City> city = cityRepository.findAllBy();
		if (city == null || city.isEmpty()){

			String filePath = "src/main/resources/City.xlsx"; // Đường dẫn tới file Excel của bạn
			log.info("filePath: " + filePath);
			cityService.saveCitiesFromExcel(filePath);

		}

	}

}
