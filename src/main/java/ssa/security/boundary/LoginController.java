package ssa.security.boundary;

import ssa.domain.Subject;
import ssa.domain.SubjectRepository;
import ssa.security.domain.CurrentUser;
import ssa.security.domain.LoginForm;
import ssa.security.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


@Controller
public class LoginController {
   @Autowired
   SubjectRepository subjectRepository;

    @RequestMapping(value={"/","/login"}, method = RequestMethod.GET)
    public String login(@RequestParam Optional<String> customerror, Model model){
        String errorS;
        if(!customerror.isPresent())
            errorS = null;
        else
            errorS = customerror.get();
        model.addAttribute("form", new LoginForm());
        model.addAttribute("customerror", errorS);
        return "login";
    }


    @RequestMapping("/divide")
    public String divide(Model model,Authentication authentication){
        CurrentUser currentUser = getCurrentUser(authentication);
        if(currentUser.getRole() == Role.PROFESSOR)
            return "redirect:/professor";
        else {
            Subject subject = subjectRepository.getByName(authentication.getName());
            if(subject.isReleased())
                return "redirect:/student";
            else
                return "redirect:/login?customerror=" + "Subject not released.";
        }
    }

    private CurrentUser getCurrentUser(Authentication authentication){
        CurrentUser currentUser = (CurrentUser)authentication.getPrincipal();
        return currentUser;
    }
}
