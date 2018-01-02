package lb.ssa.ssa.domain;

import lb.ssa.ssa.domain.Solver.MunkresSolver;
import lb.ssa.ssa.domain.Solver.Result;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.junit.Assert.*;

public class MunkresSolverTest {

    @Test
    public void test(){
        int[][] a = {{6,-4,2},
                {-2,1,3},
                {4,3,2}};
        int[][] b = {{16,1,52}, //0,1;1,0;2,2|92
                {41,72,80},
                {99,65,50}}	;

        int[][] c =  { //0,2 ; 1,1 ; 2,0 ; 3,3 | 140
                {82,83,69,92},
                {77,37,49,92},
                {11,69,5,86},
                {8,9,98,23}
        };
        int[][] c1= { //0,0; 1,1; 2,2; 3,3 | 22
                {10,11,8,13},
                {9,6,7,13},
                {4,8,1,12},
                {2,3,14,5}
        };

        int[][] d = { //0,0 ; 1,3 ; 2,2 ; 3,2 |164
                {94,81,63,53},
                {93,73,96,32},
                {89,84,20,19},
                {74,18,39,43}
        };

        int[][] e = { //min: 0,1 ; 1,2 ; 3,0 ; 3,3 | 142 max: 0,0 ; 1,3 ; 2,2 ; 3,2
                {42,40,55,40},
                {38,29,42,68},
                {5,70,75,39},
                {25,85,92,55}
        };
        int[][] f = {// |67
                { 68, 38, 9, 60, 46, 58, 83, 87, 84, 20 },
                { 53, 4, 16, 4, 44, 72, 34, 97, 69, 5 },
                { 21, 2, 45, 55, 34, 15, 2, 13, 12, 51 },
                { 8, 93, 22, 66, 25, 9, 59, 71, 12, 95 },
                { 42, 35, 33, 23, 3, 8, 8, 50, 23, 95 },
                { 74, 37, 15, 21, 36, 49, 80, 55, 79, 53 },
                { 21, 97, 55, 12, 25, 67, 10, 65, 2, 49 },
                { 8, 48, 1, 92, 8, 76, 41, 32, 87, 36 },
                { 32, 73, 71, 47, 94, 92, 16, 97, 5, 4 },
                { 58, 37, 54, 52, 84, 16, 34, 5, 72, 26 } };


        int[][] a1 = {{1,2,1,4},{3,1,1,2}};
        String[] a1Rows = {"a1","a2"};
        String[] a1Cols = {"a11","a22", "a33","a444"};

        MunkresSolver testSolverA = new MunkresSolver(a,false);
        MunkresSolver testSolverA1 = new MunkresSolver(a1,a1Rows, a1Cols,false);
        testSolverA.solve();
        Result rA1 =testSolverA1.solve();
//        try(PrintWriter out = new PrintWriter("src/main/resources/result.csv")){
//            out.print(rA1.toString());
//        } catch (FileNotFoundException ea) {
//            ea.printStackTrace();
//        }


        MunkresSolver testSolverB = new MunkresSolver(b,false);
		MunkresSolver testSolverC = new MunkresSolver(c,false);
		MunkresSolver testSolver = new MunkresSolver(c1,false);
		MunkresSolver testSolverD = new MunkresSolver(d,false);
		MunkresSolver testSolverE = new MunkresSolver(e,false);
        MunkresSolver testSolverEmax = new MunkresSolver(e,true);
		MunkresSolver testSolverF = new MunkresSolver(f,false);

        //Compared with solutions from http://hungarianalgorithm.com/solve.php
        //
		assertEquals(92, testSolverB.solve().getSum());
		assertEquals(22, testSolver.solve().getSum());
		assertEquals(140, testSolverC.solve().getSum());
		assertEquals(164, testSolverD.solve().getSum());
		assertEquals(142, testSolverE.solve().getSum());
        assertEquals(272, testSolverEmax.solve().getSum());

        assertEquals(67, testSolverF.solve().getSum(),67);
//

    }
}