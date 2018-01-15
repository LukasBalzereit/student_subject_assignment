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

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Lukas Balzereit
 */

@SpringBootApplication
//@EnableConfigurationProperties
public class SsaApplication implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SsaApplication.class);
	@Autowired
	private ConfigurableApplicationContext ctx;
	@Autowired
	private ProfessorRepository professorRepository;

	public static void main(String[] args) {
		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		if(argsList.contains("--help")){
			System.out.println("--help shows this page, breaks boot of application");
			System.out.println("--list lists all professors(as Logger.info), boots anyway");
			System.out.println("--name=[name]");
			System.out.println("if not existent,creates new user, otherwise changes password. Asks in console or uses specified password.");
			System.out.println("    --password=[password]");
			System.out.println("--delete=[name] oder '=all'");
			System.out.println("");
			return;
		}else if(listContainsSubstring(argsList,"--name") && !listContainsSubstring(argsList,"--password")) {
			String pw = inputPasswordHidden();
			if (pw != null){
				argsList.add(pw);
				args = argsList.toArray(new String[argsList.size()]);}
		}
		SpringApplication.run(SsaApplication.class,args);
	}

	//https://memorynotfound.com/spring-boot-passing-command-line-arguments-example/
	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOGGER.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		if (args.containsOption("list")){
		    LOGGER.info("All existing users:");
			professorRepository.findAll().stream()
					.map(Professor::getName)
					.forEach(LOGGER::info);
		}
	}

	/**
	 * checks if any element of the list contains the parameter-String as substring (in the beginning)
	 */
	private static boolean listContainsSubstring(List<String> list, String string){
			return list.stream()
					.filter(arg -> arg.length()>=string.length())
					.map(arg -> arg.substring(0,string.length()))
					.filter(arg -> arg.equals(string))
					.findAny().isPresent();
	}

	/**
	 * prompts user to input password in console
	 * @return passes password as --password=[input] argument
	 */
	private static String inputPasswordHidden(){
		try {
			Console console = System.console();
			char passwordArray[] = console.readPassword("input password:");
			String pw1 = new String(passwordArray);
			char passwordArray2[] = console.readPassword("repeat password:");
			String pw2 = new String(passwordArray2);

			if (pw1.equals(""))
				return null; //otherwise exception from empty ApplicationArguments when called (in config/initialized)
			else if (pw1.equals(pw2)) {
				return "--password=" + pw1;
			} else {
				console.printf("\npasswords didnt't match\n");
				return inputPasswordHidden();
			}
		} catch( NullPointerException ex ){
			LOGGER.warn("No Console instance was found [if run in IDE, try running in Console], user was initialized without password.");
			return null;
		}
	}


	@Bean
	CommandLineRunner init(){
		return (evt) -> {


			//PriorityList priorityList = new PriorityList(nameOfList, topics);

		};
	}
}
