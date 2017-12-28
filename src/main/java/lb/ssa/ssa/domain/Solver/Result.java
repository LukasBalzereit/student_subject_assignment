package lb.ssa.ssa.domain.Solver;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Result<K> {
    /**
     * <Row, Column> of the optimal solution. Index or if available custom labels.
     */
    private Map<K, K> solution;
    /**
     * Rows which were not able to be matched to any columns (if length didn't match)
     */
    private List<K> noMatch;
    private int sum;
    private double average;
    private long duration;

    public Result(Map<K,K> solution, List<K> noMatch, int sum, long duration){
        this.noMatch = noMatch;
        this.solution = solution;
        this.sum = sum;
        average = (double) sum / solution.size();
        this.duration = duration;
    }

    public String toString(){
        String map = solution.toString();
        String set = " no match: " + noMatch;
        String sum = "sum: " + this.sum +"; avg: " + average;
        String time = "time: " + duration + " ms ";
        return map + set +"\n"+ sum + "\n" + time;
    }

    public Map<K, K> getSolution() {
        return solution;
    }

    public List<K> getNoMatch() {
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
