package lb.ssa.ssa.domain;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
public class Student  implements Serializable{
    //@Autowired
    //SubjectRepository subjectRepository;

    @Id
    @GeneratedValue
    private int id;

    private String name ;

    @ManyToOne
    private Subject subject;

    private int[] ratings;

    public Student(){}
    public Student(StudentForm form, Subject subject){
       this.name = form.getName();
       this.subject = subject;//subjectRepository.getOne(form.getSubjectId());
       this.ratings = form.getRatings();
    }

    public int[] getRatings() {
        return ratings;
    }

    public void setRatings(int[] ratings) {
        this.ratings = ratings;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        System.out.println(id);
        System.out.println(name);
        System.out.println(subject);
        System.out.println(Arrays.toString(ratings));
        return "id: " + id
                + " name: " + name
                + " sub. id: " + subject.getId()
                + " ratings: " + Arrays.toString(ratings);
    }
}
