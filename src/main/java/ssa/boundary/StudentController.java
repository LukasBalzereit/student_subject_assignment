package ssa.boundary;

import ssa.domain.StudentForm;
import ssa.domain.Subject;
import ssa.domain.SubjectRepository;
import ssa.security.domain.CurrentUser;
import ssa.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping(value = "/student")
    public String addStudentForm(Model model, Authentication authentication){
        Subject subject = subjectRepository.getByName(getCurrentUser(authentication).getName());
        model.addAttribute("subject", subject );
        model.addAttribute("student", new StudentForm(subject));
        return "addStudent";
    }

    @PostMapping("/student")
    public String add(@ModelAttribute("student") StudentForm studentForm, HttpServletRequest httpServletRequest) throws Exception {//TODO save student in repository
        studentService.addStudent(studentForm);
        httpServletRequest.logout();
        return "redirect:/login?customerror=" + "Input successful.";
    }

    private CurrentUser getCurrentUser(Authentication authentication){
        CurrentUser currentUser = (CurrentUser)authentication.getPrincipal();
        return currentUser;
    }
}
