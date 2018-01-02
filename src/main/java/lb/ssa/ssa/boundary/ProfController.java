package lb.ssa.ssa.boundary;

import lb.ssa.ssa.domain.*;
import lb.ssa.ssa.domain.Solver.Result;
import lb.ssa.ssa.security.domain.CurrentUser;
import lb.ssa.ssa.security.domain.LoginForm;
import lb.ssa.ssa.security.domain.Role;
import lb.ssa.ssa.service.ProfessorService;
import lb.ssa.ssa.service.ProfessorServiceImpl;
import lb.ssa.ssa.service.StudentService;
import lb.ssa.ssa.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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



    @RequestMapping(value="/")
    public String index (Model model ) {
        return "index";
    }


    @RequestMapping(value="/professor")
    public String professorOverview(Model model, Authentication authentication){
       // Professor foundProf = professorRepository.findByName("profName");
        String name =  getCurrentUser(authentication).getName();
        Professor foundProf = professorRepository.findByName(name);
        model.addAttribute("professor", foundProf);
        return "professor";
    }

    @RequestMapping(value = "/professor/subject", method = {RequestMethod.POST, RequestMethod.GET})
    public String showSubject(@RequestParam int id, Model model){
        Subject subject = subjectRepository.findOne(id);
        if (!subject.isReleased()) {
            return "redirect:/professor/subject/add?id=" + id ;
        }
        model.addAttribute("subject", subject);
        return "subject";
    }

    @RequestMapping(value = "/professor/subject/removeStudent", method =  RequestMethod.POST)
    public String removeStudent(@RequestParam int id, Model model, Authentication authentication){
        Student student = studentRepository.getOne(id);
        int subjectId = student.getSubject().getId();
        studentService.removeStudent(student);
        return "redirect:/professor/subject?id=" + subjectId;
    }

    @RequestMapping(value="/professor/subject/remove", method = RequestMethod.POST)
    public String removeSubject(@RequestParam int id,Authentication authentication, Model model){
        String currentName = getCurrentUser(authentication).getName();
        Professor professor = professorRepository.findByName(currentName);
        subjectService.removeSubject(subjectRepository.findOne(id));
        return "redirect:/professor";
    }

    @RequestMapping(value="/professor/subject/resolve", method = RequestMethod.POST)
    public void resolveSubject(@RequestParam("id") int subjectId, Model model, HttpServletResponse response){
        Subject subject = subjectRepository.findOne(subjectId);
        Result result = subject.resolve();
        //TODO: Refactor: does file need to be saved? delete or save with specified name|version and call MunkreSovler.resolve accordingly
        //handle file
        String fileName = "result.csv";
        //create file
        try(PrintWriter out = new PrintWriter(fileName)){//try with resource -> flush automatic
            out.print(result.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //download file
        try(InputStream in = new FileInputStream(fileName)){
            response.setContentType("text/csv");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName );
            FileCopyUtils.copy(in, response.getOutputStream());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return "redirect:/professor";
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
    public String addTopic(@RequestParam("topic") String topic,@RequestParam int id, Model model){
        //if(topic.charAt(0) == ',')
        //topic = topic.substring(1);
        Subject subject = subjectRepository.getOne(id).addTopic(topic);
        subjectRepository.save(subject);
        return "redirect:/professor/subject/add?id=" + id;
    }

    @RequestMapping(value="/professor/subject/add/release", method = RequestMethod.POST )
    public String release(@RequestParam int id, Model model){
        Subject subject = subjectRepository.findOne(id);
        subject.release();
        subjectRepository.save(subject);
        return "redirect:/professor";
    }

    @RequestMapping(value="/professor/subject/add/removeTopic", method = RequestMethod.POST)
    public String deleteTopic(@RequestParam String topic,@RequestParam("id") int subjectId, Model model){
        Subject subject = subjectRepository.getOne(subjectId).removeTopic(topic); //because not index was used for deletion, multiple entries get deleted on the first found instance
        subjectRepository.save(subject);
        return "redirect:/professor/subject/add?id=" + subjectId;
    }

    @RequestMapping(value="/professor/subject/add/newName", method = RequestMethod.POST )
    public String addSubjectName(@RequestParam String name,@RequestParam Optional<String> password, Model model,Authentication authentication){
        Professor professor = professorRepository.findByName(getCurrentUser(authentication).getName());
        Subject subject = null;
        int id = 0;
        if(subjectRepository.getByName(name) == null){
            String passwordString = "";
            if(password.isPresent()) passwordString = password.get();
            subject = new Subject().addProfessor(professor).addName(name).addUnhashedPassword(passwordString);
            //subject.setHashedPassword("$2a$10$a3H7CjsW6PZzHLcNRH0clOXlyaYE9dVapHiPhQifEw5I2STUI3HV2"); //pw
            subjectRepository.save(subject);
            professor.addSubject(subject);
           //professorRepository.save(professor);
            id = subject.getId();
        }

        return "redirect:/professor/subject/add?id=" + id ;
    }

    private CurrentUser getCurrentUser(Authentication authentication){
        CurrentUser currentUser = (CurrentUser)authentication.getPrincipal();
        return currentUser;
    }


}
