package lb.ssa.ssa.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Subject implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private boolean released = false;
    @ManyToOne
    private Professor professor;

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

    public String getName() {
        return name;
    }
    public Subject addName(String name){
        this.name = name;
        return this;
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

    public boolean isReleased() {
        return released;
    }

    public List<String> getTopicsList() {
        return topicsList;
    }

    public String[] getTopics() {
        return topics;
    }

    public long getId() {
        return id;
    }

}
