package lb.ssa.ssa.boundary;

import lb.ssa.ssa.domain.*;
import lb.ssa.ssa.service.ProfessorService;
import lb.ssa.ssa.service.ProfessorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfController {
    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private SubjectRepository subjectRepository;


    @RequestMapping("/")
    public String index (Model model){
//        Professor professor = new Professor("profName");
//        professorService.save(professor);
//        Professor fProf = professorService.getByName("profName");
//        fProf.addName("profName2");
//        professorService.save(fProf);
        System.out.println(professorRepository.getOne("profName"));

        return "index";
    }


    @RequestMapping(value={"/professor","error"})
    public String professorOverview(Model model){
        Professor foundProf = professorRepository.findByName("profName");
        model.addAttribute("professor", foundProf);
        return "professor";
    }
//
//    @RequestMapping(value = "/professor/add")
//    public String addSubject( Model model){
//
//        return "addSubject";
//
//    }
//
    @RequestMapping(value = "/professor/subject", method = RequestMethod.POST)
    public String showSubject(@RequestParam int id, Model model){
       // Subject subject1 = init1();
       // Subject subject = subjectRepository.findOneById(id);
        Subject subject = subjectRepository.findOne(id);
        if (!subject.isReleased()) {
            return "redirect:/professor/subject/add?id=" + id ;
        }
        model.addAttribute("subject", subject);
        return "subject";
    }

    @RequestMapping(value = "professor/subject/add", method = {RequestMethod.POST,RequestMethod.GET})
    public String addSubject(@RequestParam int id, Model model){
        Subject subject;
        Professor professor = professorRepository.findByName("profName");//später durch currentUser bzw hidden field
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
        //TODO: (workaround) topic gets unintentionally appended to a ','
        System.out.println("addtopic: comma ? )" + topic);
        if(topic.charAt(0) == ',')
            topic = topic.substring(1);
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

    @RequestMapping(value="/professor/subject/add/newName", method = RequestMethod.POST )
    public String addSubjectName(@RequestParam String name, Model model){
        Professor professor = professorRepository.findByName("profName");
        Subject subject = null;
        int id = 0;
        if(subjectRepository.getByNameAndProfessor(name, professor) == null){
            subject = new Subject().addProfessor(professor).addName(name);
            subjectRepository.save(subject);
            professor.addSubject(subject);
            professorRepository.save(professor);
            id = (int)subject.getId();
        }

        return "redirect:/professor/subject/add?id=" + id ;
    }

    @RequestMapping(value = "/student")
    public String addStudent( Model model){
        // Subject subject1 = init1();
        // Subject subject = subjectRepository.findOneById(id);
        Subject subject = subjectRepository.findOne(1);
        Student student = new Student();
        int[] ratings = {0,2,3};
        student.addName("studName").addSubject(subject).setRatings(ratings);
        model.addAttribute("subject", subject);
        model.addAttribute("student", student);
        return "addStudent";
    }
}
