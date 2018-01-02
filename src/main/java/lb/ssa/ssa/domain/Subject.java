package lb.ssa.ssa.domain;

import lb.ssa.ssa.domain.Solver.MunkresSolver;
import lb.ssa.ssa.domain.Solver.Result;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Subject  implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String name;
    private boolean released = false;
    @ManyToOne
    private Professor professor;

    private String hashedPassword;

    @OneToMany(mappedBy = "subject")
    private List<Student> studentList = new ArrayList<>();

    private String[] topics;
    private ArrayList<String> topicsList = new ArrayList<>();

    public Subject(){};
    public Subject(String name, String[] topics){
        //this.professor = professor;
        this.name = name;
        this.topics = topics;
        released = true;
    }

    public Result resolve(){
        //topics = columns ; students = rows
        int[][] solvingArray = studentList.stream().map(Student::getRatings).toArray(int[][]::new);
        System.out.println("sovlvingArr(Result.resolve): " + Arrays.deepToString(solvingArray));
        String[] studentNameArr = studentList.stream().map(Student::getName).toArray(String[]::new);
        MunkresSolver solver = new MunkresSolver(solvingArray,studentNameArr, topics,true);
        return solver.solve();
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
        topics = topicsList.toArray(new String[topicsList.size()]);
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
        return topics;
    }

    public int getId() {
        return id;
    }

}
