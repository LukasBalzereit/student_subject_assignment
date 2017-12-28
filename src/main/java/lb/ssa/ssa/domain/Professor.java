package lb.ssa.ssa.domain;

import org.springframework.data.repository.cdi.Eager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Professor implements Serializable{

    @Id
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "professor")
    private List<Subject> subjects;

    public Professor(){
    }
    public Professor(String name){
        subjects = new ArrayList<>();
        this.name = name;
    }


    public Professor addSubject(Subject subject){
        subjects.add(subject);
        return this;
    }

    public Professor addName(String name) {
        this.name = name;
        return this;
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


    //    public void removeSubject(String name){
//        Optional<Subject> subject = findSubject(name);
//        if(subject.isPresent())
//            subjectList.remove(subject);
//    }

//    private Optional<Subject> findSubject(String name){
//        return subjectList.stream()
//                    .filter(subject -> subject.getName().equals(name))
//                    .findAny();
//    }
}