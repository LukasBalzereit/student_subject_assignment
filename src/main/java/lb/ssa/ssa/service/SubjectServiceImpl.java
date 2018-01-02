package lb.ssa.ssa.service;

import lb.ssa.ssa.domain.StudentRepository;
import lb.ssa.ssa.domain.Subject;
import lb.ssa.ssa.domain.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl implements SubjectService{
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    StudentService studentService;
    @Autowired
    StudentRepository studentRepository;

    @Override
    public void removeSubject(Subject subject) {
        if(subject.isReleased()){
            subject.getStudentList().forEach(student -> studentRepository.delete(student));//student -> studentService.removeStudent(student));
        }
        subject.getProfessor().removeSubject(subject.getName());
        subjectRepository.delete(subject);
    }
}
