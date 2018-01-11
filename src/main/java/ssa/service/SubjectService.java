package ssa.service;

import ssa.domain.Professor;
import ssa.domain.Subject;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface SubjectService {
    Subject removeSubject(int id);
    Subject resolveSubject(int id, HttpServletResponse response);
    Subject addTopic(int id, String topic);
    Subject removeTopic(int id, String topic);
    Subject releaseSubject(int id);
    Subject newSubject(String name, Optional<String> password, Professor professor);
}
