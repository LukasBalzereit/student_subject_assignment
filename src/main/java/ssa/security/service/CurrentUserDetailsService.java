package ssa.security.service;

import ssa.domain.Professor;
import ssa.domain.ProfessorRepository;
import ssa.domain.Subject;
import ssa.domain.SubjectRepository;
import ssa.security.domain.CurrentUser;
import ssa.security.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserDetailsService implements UserDetailsService{

    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    ProfessorRepository professorRepository;

    @Override
    public CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
        CurrentUser currentUser ;
        Subject subject = subjectRepository.getByName(username);
        Role role;
        if(subject != null){
            currentUser = new CurrentUser(username, subject.getHashedPassword(), Role.STUDENT);
        } else {
            Professor professor = professorRepository.findByName(username);
            if (professor != null)
                currentUser = new CurrentUser(username, professor.getHashedPassword(), Role.PROFESSOR);
            else
                throw new UsernameNotFoundException("");
        }
        return currentUser ;
    }
}
