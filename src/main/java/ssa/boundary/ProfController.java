package ssa.boundary;

import org.springframework.security.access.prepost.PreAuthorize;
import ssa.domain.*;
import ssa.service.solver.Result;
import ssa.security.domain.CurrentUser;
import ssa.service.StudentService;
import ssa.service.SubjectService;
import ssa.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Optional;

@Controller
public class ProfController {
    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentService studentService;

    /**
     * Overview of all subjects which the currentUser (Professor) owns
     */
    @RequestMapping(value="/professor")
    public String professorOverview(Model model, Authentication authentication){
       // Professor foundProf = professorRepository.findByName("profName");
        String name =  getCurrentUser(authentication).getName();
        Professor foundProf = professorRepository.findByName(name);
        model.addAttribute("professor", foundProf);
        return "professor";
    }

    /**
     * Overview of specifics of one subject
     */
    @PreAuthorize("@currentUserDetailsService.canAccessUser(principal, ${subject.professor.name})")
    @RequestMapping(value = "/professor/subject", method = {RequestMethod.POST, RequestMethod.GET})
    public String showSubject(@RequestParam int id, Model model, Authentication authentication){
        Subject subject = subjectRepository.findOne(id);
        canAccess(subject.getProfessor().getName(),authentication);
        if (!subject.isReleased()) {
            return "redirect:/professor/subject/add?id=" + id ;
        }
        model.addAttribute("subject", subject);
        return "subject";
    }

    @RequestMapping(value = "/professor/subject/removeStudent", method =  RequestMethod.POST)
    public String removeStudent(@RequestParam("id") int studentId, Model model, Authentication authentication){
        Subject subject = studentRepository.getOne(studentId).getSubject();
        canAccess(subject.getProfessor().getName(),authentication);
        int subjectId = subject.getId();
        studentService.removeStudent(studentId);
        return "redirect:/professor/subject?id=" + subjectId;
    }

    @RequestMapping(value="/professor/subject/remove", method = RequestMethod.POST)
    public String removeSubject(@RequestParam int id,Authentication authentication, Model model){
        canAccess(id, authentication);
        subjectService.removeSubject(id);
        return "redirect:/professor";
    }

    @RequestMapping(value="/professor/subject/resolve", method = RequestMethod.POST)
    public void resolveSubject(@RequestParam("id") int subjectId, Model model, HttpServletResponse response,Authentication authentication){
        canAccess(subjectRepository.getOne(subjectId).getProfessor().getName(),authentication);
        subjectService.resolveSubject(subjectId, response);
    }


    @RequestMapping(value = "professor/subject/add", method = {RequestMethod.POST,RequestMethod.GET})
    public String addSubject(@RequestParam int id, Model model,Authentication authentication){
        Subject subject;
        String name = getCurrentUser(authentication).getName();
        Professor professor = professorRepository.findByName(name);
        if(id == 0) {
            subject = null;
        }else if(subjectRepository.getOne(id) == null){
            throw new IllegalArgumentException("No subject specified");
        } else {
            subject = subjectRepository.getOne(id);
        }
        model.addAttribute("subject", subject);
        return "addSubject";
    }



    @RequestMapping(value="/professor/subject/add/newTopic", method = {RequestMethod.POST,RequestMethod.GET})
    public String addTopic(@RequestParam("topic") String topic,@RequestParam int id, Model model,Authentication authentication){
        canAccess(id, authentication);
        subjectService.addTopic(id, topic);
        return "redirect:/professor/subject/add?id=" + id;
    }

    @RequestMapping(value="/professor/subject/add/release", method = RequestMethod.POST )
    public String release(@RequestParam int id, Model model, Authentication authentication){
        canAccess(id, authentication);
        subjectService.releaseSubject(id);
        return "redirect:/professor";
    }

    @RequestMapping(value="/professor/subject/add/removeTopic", method = RequestMethod.POST)
    public String deleteTopic(@RequestParam String topic,@RequestParam("id") int subjectId, Model model,Authentication authentication){
        Subject subject = subjectRepository.getOne(subjectId);
        subjectService.removeTopic(subjectId, topic);
        return "redirect:/professor/subject/add?id=" + subjectId;
    }

    @RequestMapping(value="/professor/subject/add/newName", method = RequestMethod.POST )
    public String addSubjectName(@RequestParam String name,@RequestParam Optional<String> password, Model model,Authentication authentication){
        int id = 0;
        if(subjectRepository.getByName(name) == null){
            Professor professor = professorRepository.findByName(getCurrentUser(authentication).getName());
            id = subjectService.newSubject(name,password,professor).getId();
        }
        return "redirect:/professor/subject/add?id=" + id ;
    }

    private void canAccess(int subjectId, Authentication authentication){
        Subject subject = subjectRepository.getOne(subjectId);
        canAccess(subject.getProfessor().getName(), authentication);
    }

    /**
     * Breaks program if logged in user is not the owner of th processed data. Supposed to happen, if a user tries
     * to access data of another user without permission (direct via URL).
     * @param professorName name of professor, who is the owner of processed data
     * @throws RuntimeException
     */
    private void canAccess(String professorName,Authentication authentication){
        if(!professorName.equals(getCurrentUser(authentication).getName())){
            System.out.println("verboten");
           throw new RuntimeException("Entry forbidden, because owner and current user don`t match");
        }
    }

    /**
     * Returns currently logged in user (see config/SecurityConfiguration and /security/ -> CurrentUser + CurrentUserDetailsService
     * @param authentication
     * @return
     */
    private CurrentUser getCurrentUser(Authentication authentication){
        CurrentUser currentUser = (CurrentUser)authentication.getPrincipal();
        return currentUser;
    }


}
