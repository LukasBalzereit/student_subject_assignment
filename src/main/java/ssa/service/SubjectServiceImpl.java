package ssa.service;

import org.springframework.util.FileCopyUtils;
import ssa.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ssa.service.solver.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImpl implements SubjectService{
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    StudentService studentService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ProfessorRepository professorRepository;

    @Override
    public Subject removeSubject(int id) {
        Subject subject = subjectRepository.findOne(id);
        List<Student> students = new ArrayList<>(subject.getStudentList());
        subject.setStudentList(null);
        if(subject.isReleased()){
            students.forEach(student -> {
                studentRepository.delete(student);
            });
        }
        Professor professor = subject.getProfessor();
        professor.removeSubject(subject.getName());
        subjectRepository.delete(subject);
        return subject;
    }

    /**
     * starts download of .csv file of the result (from the solved algorithm /solver/MunkresSolver)
     */
    @Override
    public Subject resolveSubject(int id, HttpServletResponse response) {
        Subject subject = subjectRepository.getOne(id);
        Result result = subject.resolve();

        String fileName = "result_" + subject.getName() + ".csv";
        File resultFile = new File(fileName);
        //create file
        try(PrintWriter out = new PrintWriter(resultFile)){//try with resource -> flush automatic
            out.print(result.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //download file
        try(InputStream in = new FileInputStream(resultFile)){
            response.setContentType("text/csv");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName );
            FileCopyUtils.copy(in, response.getOutputStream());

            //delete file
            resultFile.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subject;
    }

    @Override
    public Subject addTopic(int id, String topic) {
        Subject subject = subjectRepository.getOne(id).addTopic(topic);
        subjectRepository.save(subject);
        return subject;
    }

    @Override
    public Subject removeTopic(int id, String topic) {
        Subject subject = subjectRepository.getOne(id).removeTopic(topic); //because topics got no unique index,
        // if multiple topics with same name, the first found instance gets deleted
        subjectRepository.save(subject);
        return subject;
    }

    @Override
    public Subject releaseSubject(int id) {
        Subject subject = subjectRepository.findOne(id);
        subject.release();
        subjectRepository.save(subject);
        return subject;
    }

    @Override
    public Subject newSubject(String name, Optional<String> password, Professor professor) {
        String passwordString = "";
        if(password.isPresent()) passwordString = password.get();
        Subject subject = new Subject().addProfessor(professor).addName(name).addUnhashedPassword(passwordString);
        subjectRepository.save(subject);
        professor.addSubject(subject);
        return subject;
    }
}
