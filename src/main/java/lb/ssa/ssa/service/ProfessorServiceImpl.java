package lb.ssa.ssa.service;

import lb.ssa.ssa.domain.Professor;
import lb.ssa.ssa.domain.ProfessorRepository;
import lb.ssa.ssa.domain.Subject;
import lb.ssa.ssa.domain.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfessorServiceImpl implements ProfessorService {
    @Autowired
    ProfessorRepository professorRepository;
    @Autowired
    SubjectRepository subjectRepository;


}


