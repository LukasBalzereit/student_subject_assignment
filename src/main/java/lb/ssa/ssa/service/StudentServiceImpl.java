package lb.ssa.ssa.service;

import lb.ssa.ssa.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    StudentRepository studentRepository;

    @Override
    public void removeStudent(Student student) {
        student.getSubject().getStudentList().remove(student);
        studentRepository.delete(student);
    }

    @Override
    public void addStudent(StudentForm form) {
        Subject subject = subjectRepository.findOne(form.getSubjectId());
        Student student = new Student(form, subject);
        studentRepository.save(student);
        subject.addStudent(student);
        //subjectRepository.save(subject);
    }
}
