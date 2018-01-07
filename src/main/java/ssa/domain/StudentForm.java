package ssa.domain;

public class StudentForm {
    private int subjectId;
    private String name;
    private int[] ratings;


    public StudentForm(){}
    public StudentForm(Subject subject){
        setSubject(subject);
    }
    public int getSubjectId() {
        return subjectId;
    }

    public void setSubject(Subject subject) {
        ratings = new int[subject.getTopics().length];
        this.subjectId = (int)subject.getId();
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getRatings() {
        return ratings;
    }

    public void setRatings(int[] ratings) {
        this.ratings = ratings;
    }
}
