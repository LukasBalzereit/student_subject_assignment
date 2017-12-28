package lb.ssa.ssa;

import lb.ssa.ssa.domain.Professor;
import lb.ssa.ssa.domain.ProfessorRepository;
import lb.ssa.ssa.domain.Solver.MunkresSolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class SsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(){
		return (evt) -> {


			//PriorityList priorityList = new PriorityList(nameOfList, topics);

		};
	}
}
