package ssa.service.solver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Result {
    /**
     * <Row, Column> of the optimal solution. Index or if available custom labels.
     */
    private Map<String, String> solution;
    /**
     * Rows which were not able to be matched to any columns (if length didn't match)
     */
    private List<String> noMatch;
    private int sum;
    private double average;
    private long duration;

    public Result(Map<String,String> solution, List<String> noMatch, int sum, long duration){
        this.noMatch = noMatch;
        this.solution = solution;
        this.sum = sum;
        average = (double) sum / solution.size();
        this.duration = duration;
    }

    public String toString(){
        String map = "matched:\n" + mapToString(solution);
        String set = "\n\nno match:\n" + listToString(noMatch);
        String sum = "\nsum: " + this.sum +";avg: " + average;
        String time = "time: " + duration + " ms ";
        return map + set +"\n"+ sum + "\n" + time;
    }

    /**
     * makes Map.toString() easier to read by using ";" which are different datacells if opened as .csv file (LibreOffice Calc, MS Exel)
     * @param map
     * @return
     */
    private String mapToString(Map<String, String> map){
        return solution.entrySet().stream()
                                .map(entry -> entry.getKey() + ";" + entry.getValue())
                                .collect(Collectors.joining(System.getProperty("line.separator")));
    }
    private String listToString(List<String> list){
        if(list == null )
            return "";
        else
            return list.stream()
                    .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    public Map<String, String> getSolution() {
        return solution;
    }

    public List<String> getNoMatch() {
        return noMatch;
    }

    public double getAverage() {
        return average;
    }

    public long getDuration() {
        return duration;
    }

    public int getSum(){
        return sum;
    }
}
