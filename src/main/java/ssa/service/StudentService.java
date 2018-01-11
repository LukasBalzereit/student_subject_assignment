package ssa.service;

import ssa.domain.Student;
import ssa.domain.StudentForm;

public interface StudentService {
    void removeStudent(int id);
    void addStudent(StudentForm form );
}
