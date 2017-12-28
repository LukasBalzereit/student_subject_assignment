package lb.ssa.ssa.config;

import lb.ssa.ssa.domain.Professor;
import lb.ssa.ssa.domain.ProfessorRepository;
import lb.ssa.ssa.domain.Subject;
import lb.ssa.ssa.domain.SubjectRepository;
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

   @PostConstruct
    public void init(){
       String[] topics = {"topic1","topic2","topic3"};
       Subject subject = new Subject("SubjectName", topics);

       String name = "profName";
       Professor professor = new Professor(name);
       professor.addSubject(subject);
       subject.addProfessor(professor);
       professorRepository.save(professor);
      subjectRepository.save(subject);


       String[] topics2 = {"topic11","topic22","topic33"};
       Subject subject2 = new Subject("SubjectName2", topics2);
       Professor fProf = professorRepository.findOne("profName");
       subject2.addProfessor(fProf);
       fProf.addSubject(subject2);
       professorRepository.save(fProf);

       subjectRepository.save(subject2);

   }


}
