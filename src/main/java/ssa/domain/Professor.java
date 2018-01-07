package ssa.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Professor implements Serializable{

    private static final long serialVersionUID = 1L ;

    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String name;

    private String hashedPassword;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "professor")
    private List<Subject> subjects;

    public Professor(){
    }
    public Professor(String name){
        subjects = new ArrayList<>();
        this.name = name;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Professor addUnhashedPassword(String unhashedPassword){
        hashedPassword = new BCryptPasswordEncoder().encode(unhashedPassword);
        return this;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Professor addSubject(Subject subject){
        subjects.add(subject);
        return this;
    }

    public Professor addName(String name) {
        this.name = name;
        return this;
    }
    public void setUnhashedPassword(String unhashedPassword){
        hashedPassword = new BCryptPasswordEncoder().encode(unhashedPassword);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null|| getClass() != o.getClass()) return false;
        Professor prof = (Professor) o;
        return (name.equals(prof.name));
    }
//Id verwenden ? https://stackoverflow.com/questions/4388360/should-i-write-equals-methods-in-jpa-entities
    @Override
    public int hashCode(){
        return Objects.hash( name);
    }

    public List<Subject> getSubjects() {
        return subjects;
    }


        public void removeSubject(String name){
            subjects.remove(name);
        }

//    private Optional<Subject> findSubject(String name){
//        return subjectList.stream()
//                    .filter(subject -> subject.getName().equals(name))
//                    .findAny();
//    }
}
