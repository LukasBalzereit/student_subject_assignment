package ssa.domain;

import ssa.service.solver.MunkresSolver;
import ssa.service.solver.Result;
import ssa.service.solver.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Entity
public class Subject  implements Serializable {

    private static final long serialVersionUID = 3L ;

    private static Logger LOGGER = LoggerFactory.getLogger(Subject.class);

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private boolean released = false;
    @ManyToOne
    private Professor professor;

    private String hashedPassword;

    @OneToMany(mappedBy = "subject",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Student> studentList = new ArrayList<>();

   // @ElementCollection
   // @OrderColumn( name ="id" )
   // @Column(name = "topics")
   // private String[] topics;

    @ElementCollection
    @Column(length = 1000)
    @CollectionTable(name="topicsList", joinColumns = @JoinColumn(name = "id") )
    private List<String> topicsList = new ArrayList<>();

    public Subject(){};
    public Subject(String name, String[] topics){
        //this.professor = professor;
        this.name = name;
        this.topicsList = Arrays.asList(topics);
        released = true;
    }

    public Result resolve(){
        //topics = columns ; students = rows
        String[] topics = topicsList.toArray(new String[topicsList.size()]);
        int[][] solvingArray = studentList.stream().map(Student::getRatings).toArray(int[][]::new);
        String[] studentNameArr = studentList.stream().map(Student::getName).toArray(String[]::new);
        LOGGER.info("Solving assignment problem with: " + Arrays.deepToString(solvingArray));
        Solver solver = new MunkresSolver(solvingArray,studentNameArr, topics,true);
        Result result = solver.solve();
        LOGGER.info("Resolved: \n" + result);
        return result;
    }

    public String getName() {
        return name;
    }
    public Subject addName(String name){
        this.name = name;
        return this;
    }

    public Subject addStudent(Student student){
        studentList.add(student);
        return this;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Subject addUnhashedPassword(String unhashedPassword){
        hashedPassword = new BCryptPasswordEncoder().encode(unhashedPassword);
        return this;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }

    public Subject addProfessor(Professor professor){
        this.professor = professor;
        return this;
    }
    public Professor getProfessor() {
        return professor;
    }

    public void release(){
        released = true;
        //topics = topicsList.toArray(new String[topicsList.size()]);
    }


    public Subject addTopic(String topic){
        if (!released)
            topicsList.add(topic);
        return this;
    }

    public Subject removeTopic(String topic){
        if(!released)
            topicsList.remove(topic);
        return this;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public List<Student> getStudentList()
    {
        return studentList;
    }

    public boolean isReleased() {
        return released;
    }

    public List<String> getTopicsList() {
        return topicsList;
    }

    public String[] getTopics() {
        return topicsList.toArray(new String[topicsList.size()]);
    }

    public int getId() {
        return id;
    }

}
