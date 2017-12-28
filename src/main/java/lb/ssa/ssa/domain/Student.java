package lb.ssa.ssa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class Student implements Serializable{

    @Id
    @GeneratedValue
    private int id;

    private String name;
    @ManyToOne
    private Subject subject;


    private int[] ratings;

    public Student addSubject(Subject subject) {
        this.subject = subject;
        ratings = new int[subject.getTopics().length];
        return this;
    }

    public Student addName(String name){
    this.name = name;
    return this;
    }

    public int[] getRatings() {
        return ratings;
    }

    public void setRatings(int[] ratings) {
        this.ratings = ratings;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getName() {
        return name;
    }
}
