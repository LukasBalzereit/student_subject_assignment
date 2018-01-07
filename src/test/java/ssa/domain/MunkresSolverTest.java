package ssa.domain;

import ssa.service.solver.Result;
import ssa.service.solver.MunkresSolver;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int[][] c2= { //0,0; 1,1; 2,2; 3,3 | 22
                {10,8},
                {9,6,7,13},
                {4,8,1,12},
                {2,3,14,5}
        };

        int[][] d = { //0,0 ; 1,3 ; 2,2 ; 3,2 |164
                {94,81,-12,53},
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



        int[][] steps =
                {{1,3,0},
                {2,3,1}};

        int[][] notFullArray = new int[2][2];
        notFullArray[0][0] = 3;
        notFullArray[0][1] = 5;
        notFullArray[1][1] = 12;

        MunkresSolver testSolverNotFull = new MunkresSolver(notFullArray,true);
        MunkresSolver testSolverstepS = new MunkresSolver(steps,true);
        MunkresSolver testSolverA = new MunkresSolver(a,false);
        MunkresSolver testSolverB = new MunkresSolver(b,false);
		MunkresSolver testSolverC = new MunkresSolver(c,false);
		MunkresSolver testSolver = new MunkresSolver(c1,false);
        MunkresSolver testSolverC2 = new MunkresSolver(c2,false);
		MunkresSolver testSolverD = new MunkresSolver(d,false);
		MunkresSolver testSolverE = new MunkresSolver(e,false);
        MunkresSolver testSolverEmax = new MunkresSolver(e,true);
		MunkresSolver testSolverF = new MunkresSolver(f,false);


        //Compared with solutions from http://hungarianalgorithm.com/solve.php
        assertEquals(15, testSolverNotFull.solve().getSum()); // one element is empty (not end of row)
        assertEquals(5, testSolverstepS.solve().getSum()); //not all rows
        assertEquals(-4, testSolverA.solve().getSum()); //negative values
		assertEquals(92, testSolverB.solve().getSum());
		assertEquals(22, testSolver.solve().getSum());
		assertEquals(140, testSolverC.solve().getSum());
        assertEquals(9, testSolverC2.solve().getSum());//first row not equal length
		assertEquals(118, testSolverD.solve().getSum());
		assertEquals(142, testSolverE.solve().getSum());
        assertEquals(272, testSolverEmax.solve().getSum());
        assertEquals(67, testSolverF.solve().getSum());

        //test more columns than rows, check result with labels
        int[][] a1 = {{1,2,1,4},{3,1,1,2}};
        String[] a1Rows = {"a1","a2"};
        String[] a1Cols = {"a11","a22", "a33","a44"};
        MunkresSolver testSolverA1 = new MunkresSolver(a1,a1Rows, a1Cols,false);
        Result rA1 = testSolverA1.solve();

        List<String> rA1ExpectedNoMatch = Arrays.asList("a33","a44");
        Map<String,String> rA1ExpectedMatch = new HashMap<>();
        rA1ExpectedMatch.put("a1","a11");
        rA1ExpectedMatch.put("a2","a22");
        assertEquals(rA1ExpectedNoMatch,rA1.getNoMatch());
        assertEquals(rA1ExpectedMatch,rA1.getSolution());
        assertEquals(2,rA1.getSum());
        //System.out.println(testSolverA1.getResult());


        //test less columns than rows, check result with labels
        int[][] g = {{1,2},{5,2},{5,3},{4,2}};
        String[] gRows = {"a1","a2","a3","a4"};
        String[] gCols = {"a11","a22"};
        MunkresSolver testSolverG = new MunkresSolver(g,gRows,gCols,true);
        Result rG = testSolverG.solve();

        List<String> gExpectedNoMatch = Arrays.asList("a1","a4");
        Map<String,String> gExpectedMatch = new HashMap<>();
        gExpectedMatch.put("a2","a11");
        gExpectedMatch.put("a3","a22");
        assertEquals(gExpectedNoMatch,rG.getNoMatch());
        assertEquals(gExpectedMatch,rG.getSolution());
        assertEquals(8,rG.getSum());
        //System.out.println(testSolverG.getResult());

    }
}