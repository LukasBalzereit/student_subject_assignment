package lb.ssa.ssa;

import lb.ssa.ssa.domain.Professor;
import lb.ssa.ssa.domain.ProfessorRepository;
import lb.ssa.ssa.domain.Solver.MunkresSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class SsaApplication {

	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode(""));//pw= $2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2
		System.out.println(new BCryptPasswordEncoder().matches("pw","$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2"));
		SpringApplication.run(SsaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(){
		return (evt) -> {


			//PriorityList priorityList = new PriorityList(nameOfList, topics);

		};
	}
}
