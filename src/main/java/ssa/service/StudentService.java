package ssa.service;

import ssa.domain.Student;
import ssa.domain.StudentForm;

public interface StudentService {
    void removeStudent(Student student);
    void addStudent(StudentForm form );
}
