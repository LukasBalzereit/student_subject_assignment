package lb.ssa.ssa.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor,String> {
    Professor findByName(String name);
}
