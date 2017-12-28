package lb.ssa.ssa.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Subject getByNameAndProfessor(String name, Professor professor);

}