package ssa.config;

import ssa.domain.*;
import ssa.service.ProfessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Initialize {
   private static final Logger LOGGER = LoggerFactory.getLogger(Initialize.class);

   @Value("${name:#{null}}")
   private String newProfName;

   @Value("${password:#{null}}")
   private String newProfPw;

   @Value("${delete:#{null}}")
   private String deleteString;


   @Autowired
   ProfessorRepository professorRepository;
   @Autowired
    ProfessorService professorService;
   @Autowired
   SubjectRepository subjectRepository;
   @Autowired
   StudentRepository studentRepository;

   @PostConstruct
    public void init() {

       if(deleteString != null){
           if(deleteString.equals("all")){
               //professorRepository.findAll().forEach(professorService::deleteProfessor);
               studentRepository.deleteAll();
               subjectRepository.deleteAll();
               professorRepository.deleteAll();
               LOGGER.warn("All Profs deleted.");
           }else {
               Professor professor = professorRepository.findByName(deleteString);
               if(professor != null) {
                   professorService.deleteProfessor(professor);
                   LOGGER.info("Deleted " + deleteString);
               }else
                   LOGGER.info("Professor " + deleteString + " could not be deleted." );
           }
       }

       if (newProfName != null) {
           if (professorRepository.findByName(newProfName) == null) {
               String password;
               if (newProfPw == null) {
                   LOGGER.info("Initialized professor " + newProfName + " without password.");
                   password = "";
               } else {
                   password = newProfPw;
                   LOGGER.info("Initialized professor " + newProfName);
               }

               Professor professor = new Professor(newProfName);
               professor.setUnhashedPassword(password);
               professorRepository.save(professor);
           } else{
               Professor professor = professorRepository.findByName(newProfName);
               if(newProfPw == null) newProfPw="";
               professor.setUnhashedPassword(newProfPw);
               professorRepository.save(professor);
               LOGGER.info("Changed Password of " + newProfName);
           }
       }

       if(professorRepository.findByName("profName")==null) {
          LOGGER.info("Initialize DB with default data");
         String[] topics = {"topic1", "topic2", "topic3"};
         Subject subject = new Subject("subjectName", topics).addUnhashedPassword("pw");
         //subject.setHashedPassword("$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2");
         subjectRepository.save(subject);

         Student student = new Student();
         int[] ratings = {1, 3, 0};
         student.setRatings(ratings);
         student.setName("testStud");
         student.setSubject(subject);
         studentRepository.save(student);

         subject.addStudent(student);

         String name = "profName";
         Professor professor = new Professor(name);
         professor.setHashedPassword("$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2");//pw
         professor.addSubject(subject);
         professorRepository.save(professor);

         subject.addProfessor(professor);
         subjectRepository.save(subject);

      }

   }


}
