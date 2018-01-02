package lb.ssa.ssa.config;

import lb.ssa.ssa.domain.*;
import lb.ssa.ssa.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
public class Initialize {
   @Autowired
    ProfessorRepository professorRepository;
   @Autowired
    ProfessorService professorService;
   @Autowired
    SubjectRepository subjectRepository;
   @Autowired
   StudentRepository studentRepository;

   @PostConstruct
    public void init(){
       String[] topics = {"topic1","topic2","topic3"};
       Subject subject = new Subject("subjectName", topics).addUnhashedPassword("pw");
       //subject.setHashedPassword("$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2");

       Student student = new Student();
       int[] ratings = {1,3,2};
       student.setRatings(ratings);
       student.setName("testStud");
       subjectRepository.save(subject);
       student.setSubject(subject);
       studentRepository.save(student);
       subject.addStudent(student);

       String name = "profName";
       Professor professor = new Professor(name);
       professor.setHashedPassword("$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2");
       professor.addSubject(subject);
       professorRepository.save(professor);
       subject.addProfessor(professor);
       subjectRepository.save(subject);

       String[] topics2 = {"topic11","topic22","topic33"};
       Subject subject2 = new Subject("subjectName2", topics2).addUnhashedPassword("pw");
       //subject2.setHashedPassword("$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2");
       subject2.addProfessor(professor);
       professor.addSubject(subject2);
       professorRepository.save(professor);
       subjectRepository.save(subject2);

       Professor professor1 = new Professor("profName2");
       professor1.setHashedPassword("$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2");
       professorRepository.save(professor1);

       //subjectRepository.save(subject2);

   }


}
