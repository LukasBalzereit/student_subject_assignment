package ssa.service;

import ssa.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService{
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    StudentService studentService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ProfessorRepository professorRepository;

    @Override
    public void removeSubject(Subject subject) {
        List<Student> students = new ArrayList<>(subject.getStudentList());
        subject.setStudentList(null);
        if(subject.isReleased()){
            students.forEach(student -> {
                //subject.getStudentList().remove(student);
                studentRepository.delete(student);
            });//student -> studentService.removeStudent(student));
        }
        Professor professor = subject.getProfessor();
        professor.removeSubject(subject.getName());
       // subjectRepository.save(subject);
       // professorRepository.save(professor);
        subjectRepository.delete(subject);
    }
}
