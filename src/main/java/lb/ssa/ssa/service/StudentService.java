package lb.ssa.ssa.service;

import lb.ssa.ssa.domain.Student;
import lb.ssa.ssa.domain.StudentForm;
import lb.ssa.ssa.domain.Subject;

public interface StudentService {
    void removeStudent(Student student);
    void addStudent(StudentForm form );
}
