package ssa.service;

import ssa.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    StudentRepository studentRepository;

    @Override
    public void removeStudent(int id) {
        Student student = studentRepository.findOne(id);
        student.getSubject().getStudentList().remove(student);
        studentRepository.delete(student);
    }

    @Override
    public void addStudent(StudentForm form) {
        Subject subject = subjectRepository.findOne(form.getSubjectId());
        Student student = new Student(form, subject);
        studentRepository.save(student);
        subject.addStudent(student);
    }
}
