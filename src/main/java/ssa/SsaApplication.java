package ssa;

import ssa.domain.Professor;
import ssa.domain.ProfessorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
//@EnableConfigurationProperties
public class SsaApplication implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SsaApplication.class);
	@Autowired
	private ConfigurableApplicationContext ctx;
	@Autowired
	private ProfessorRepository professorRepository;

	public static void main(String[] args) {
		if(Arrays.asList(args).contains("--help")){
			System.out.println("--help shows this page, breaks boot of application");
			System.out.println("--list lists all professors(as Logger.info), boots anyway");
			System.out.println("--name=[name]");
			System.out.println("if not existent, create with default(empty) or specified password, otherwise changes password");
			System.out.println("    --password=[password]");
			System.out.println("--delete=[name] oder '=all'");
			System.out.println("");

		}else
			SpringApplication.run(SsaApplication.class, args);
	}

	//https://memorynotfound.com/spring-boot-passing-command-line-arguments-example/
	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOGGER.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		if (args.containsOption("list")){
			professorRepository.findAll().stream()
					.map(Professor::getName)
					.forEach(LOGGER::info);
		}
	}


	@Bean
	CommandLineRunner init(){
		return (evt) -> {


			//PriorityList priorityList = new PriorityList(nameOfList, topics);

		};
	}
}
