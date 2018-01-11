package ssa.service;

import ssa.domain.Professor;
import ssa.domain.ProfessorRepository;
import ssa.domain.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfessorServiceImpl implements ProfessorService {
    @Autowired
    ProfessorRepository professorRepository;
    @Autowired
    SubjectService subjectService;
    @Autowired
    SubjectRepository subjectRepository;


    @Override
    public void deleteProfessor(Professor professor) {
        subjectRepository.findAll().stream().filter(sub -> sub.getProfessor().equals(professor)).forEach(subjectRepository::delete);
        professor.setSubjects(null);
        professorRepository.delete(professor);
    }
}


