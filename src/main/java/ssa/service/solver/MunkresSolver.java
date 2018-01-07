package ssa.service.solver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Solving the assignment problem with the improved hungarian algorithm by James Munkres.
 * Published: Algorithm for the Assignment and Transportation Problems
 * in Journal of the Society of Industrial and Applied Mathematics, Vol. 5, p.32-38, 1957
 *
 * The main steps (methods) are: preliminaries(), step1(), step2(), step3(), which are named and structured exactly
 * like Munkres proposed.
 * Methods are ordered  by their access modifier and alphabetically.
 */
public class MunkresSolver implements Solver{
    //MIN_IN_TABLE: smallest value that may be used by values of the table, values below are used for marking
    //in the algorithm. Only valid after preliminaries() was executed.
    //Starred zero = 0*; primed zero = 0'
    private final int MIN_IN_TABLE = 0, STARRED = MIN_IN_TABLE - 1, PRIMED = MIN_IN_TABLE - 2;
    private int dummyRows = 0, dummyColumns = 0;//amount of rows added to make the array square (see makeSquare())
    private final int[][] solvingArray/*array used to compute solution(gets changed)*/, comparisonArray;//the original array, to get the original values later
    private  String[] rowLabel, columnLabel;
    private long startTime;
    private List<Integer> coveredRows, coveredColumns;
    private Result result;

    /**
     * Constructs MunkresSolver with labels matching rows/columns
     * @param array
     * @param rowLabel labels for rows, length has to match the array.length (if not square) or columnLabel.length (square)
     * @param columnLabel labels for columns
     */
    public MunkresSolver(int[][] array, String[] rowLabel, String[] columnLabel, boolean maximize) {
        this(array, maximize);
        this.rowLabel = rowLabel;
        this.columnLabel = columnLabel;
        if (!(((rowLabel.length == array.length) || (rowLabel.length == solvingArray.length)) || (columnLabel.length == solvingArray[0].length)))
            throw new IllegalArgumentException("Array size and labels don't match.");
    }

    /**
     *Constructs MunkresSolver
     * @param array
     */
    public MunkresSolver(int[][] array, boolean maximize) {
        this.solvingArray = makeSquare(array);
        //copy 2D solvingArray to comparisonArray
        comparisonArray = new int[solvingArray.length][];
        for (int i = 0; i < solvingArray.length; i++) {
            comparisonArray[i] = solvingArray[i].clone();
        }
        //convert minimisation problem into maximisation problem
        if(maximize) convertMaximisation();
        coveredRows = new ArrayList<>();
        coveredColumns = new ArrayList<>();
    }

    public Result getResult() {
        return result;
    }

    /**
     * Starts the hungarian algorithm, which ends with a (catched) SolvedException. Uses the {@link #solvingArray}.
     * @return
     */
    @Override
    public Result solve() {
        startTime = System.currentTimeMillis();
        try {
            preliminaries();
        } catch (SolvedException ex) {
            Result r = createSolution();
            return r;
        }
        return null;
    }

    /**
     * adds number to every element of array
     * @param array the number is going to be added to
     * @param number to be added to the array
     * @param belowZero whether values below zero ({@link #STARRED} or {@link #PRIMED} are to be altered
     * @return array with added number
     */
    private int[] addToArray(int[] array, int number, boolean belowZero) {
        IntStream arrayStream = Arrays.stream(array);
        if (!belowZero) {
            arrayStream = arrayStream.map(e -> { //If value is starred or primed it gets lowered by 'number',
                if (e < MIN_IN_TABLE)                       // so that it stays the same in the end
                    e -= number;
                return e;
            });
        }
        return arrayStream.map(e -> e + number).toArray();
    }

    /**
     * adds number to every element of a column of the {@link #solvingArray}
     * @param indexCol index of column in {@link #solvingArray}
     * @param number to be added to the column
     * @param belowZero whether values below zero (starred, altered) are to be altered
     */
    private void addToColumn(int indexCol, int number, boolean belowZero) {
        int[] addedArray = addToArray(columnToArray(indexCol), number, belowZero);
        IntStream.range(0, solvingArray.length)
                .forEach(i -> {
                    solvingArray[i][indexCol] = addedArray[i];
                });
    }

    /**
     * adds number to every element of a row of the {@link #solvingArray}
     * @param indexRow index of row in {@link #solvingArray}
     * @param number to be added to the row
     * @param belowZero whether values below zero (starred, altered) are to be altered
     */
    private void addToRow(int indexRow, int number, boolean belowZero) {
        solvingArray[indexRow] = addToArray(solvingArray[indexRow], number, belowZero);
    }

    private boolean columnContains(int indexColumn, int content) {
        return Arrays.stream(columnToArray(indexColumn))
                .filter(e -> e == content)
                .findAny()
                .isPresent();
    }

    /**
     * @param indexOfColumn index of column in {@link #solvingArray}
     * @param element searched number
     * @return index inside the column (equal to the index of the row)
     */
    private int columnIndexOfElement(int indexOfColumn, int element) {
        int r = convertArrayToList(columnToArray(indexOfColumn)).indexOf(element);
        return r;
    }

    /**
     * converts a column of the {@link #solvingArray} to a usable array.
     */
    private int[] columnToArray(int indexOfColumn) {
        return IntStream.range(0, solvingArray.length)
                .map(i -> solvingArray[i][indexOfColumn])
                .toArray();
    }

    /**
     * creates List<Integer> from array, which is useful eg. if the amount of actual elements is required, because array.length
     * also counts emtpy elements
     * @param array
     * @return
     */
    private List<Integer> convertArrayToList(int[] array) {
        return Arrays.stream(array)  //Stream because asList() doesn't work with primitives
                .boxed() // intStream -> Stream<Integer>
                .collect(Collectors.toList());  //list because array.length doesn't recognize empty elements
    }

    //every element of the solvingArray * (-1) (to convert minimisation to maximisation problem)
    private void convertMaximisation() {
        for (int iRow = 0; iRow < solvingArray.length; iRow++) {
            for (int iCol = 0; iCol < solvingArray[0].length; iCol++) {
                //every element of solvingArray
                solvingArray[iRow][iCol] *= -1;
            }
        }
        //Arrays.stream(solvingArray).flatMapToInt(Arrays::stream).map(i -> i= -i).toArray(new int[]);
    }

    /**
     * marks column of {@link #solvingArray} as covered ( used in algorithm)
     */
    private void coverColumn(int index) {
        coveredColumns.add(index);
    }

    /**
     * marks column of {@link #solvingArray} as covered ( used in algorithm)
     */
    private void coverRow(int index) {
        coveredRows.add(index);
    }

    /**
     * Creates String[] row and column labels with numbers 0..n, used if no custom labels were given (depends on
     * constructor used)
     */
    private void createLabels() {
        columnLabel = new String[solvingArray[0].length];
        Arrays.setAll(columnLabel, i -> "col" + String.valueOf(i++));
        rowLabel = new String[solvingArray.length];
        Arrays.setAll(rowLabel, i -> "row" + String.valueOf(i++));
    }

    /**
     * Creates result-object with the required values
     */
    private Result createSolution() {
        //length of origial array without added 'dummy' (0-value)(see makeSquare()) rows at the end
        int originalLength = solvingArray.length - dummyRows;
        int originalColumns = solvingArray[0].length - dummyColumns;


        //List of solutions in solvingArray(index of List = index of row, element in list = index of column)
        List<Integer> solutionList = IntStream.range(0, solvingArray.length)
                .map(i -> rowIndexOfElement(i, STARRED))
                .boxed()
                .collect(Collectors.toList());

        if ((columnLabel == null) || (rowLabel == null))
            createLabels();
        //Map<Row-index,Column-index>
        //converts solutionList (int-values) to Map with custom or default(see createLabels()) labels of the solutions
        Map<String, String> solutionStringMap = new HashMap<>();
        IntStream.range(0, solutionList.size()) //refactor: (0,originalLength)
                .filter(i -> (i < originalLength))
                .filter(i -> solutionList.get(i)<originalColumns)
                //.collect(Collectors.toMap(i -> Integer.valueOf(rowLabel[i]),i-> columnLabel[solutionList.get(i)]));
                .forEach(i -> {
                   solutionStringMap.put(rowLabel[i], columnLabel[solutionList.get(i)]);
                });

        //List of rows not matched to any columns (0-value rows were used (see makeSquare())
        List<String> noMatchList = null;
        if (originalLength != solvingArray.length) {
            noMatchList = IntStream.range(originalLength, columnLabel.length)
                    .mapToObj(i -> columnLabel[solutionList.get(i)])
                    .collect(Collectors.toList());
        }else if( (solvingArray[0].length != originalColumns)){
            noMatchList = IntStream.range(0, rowLabel.length)
                                    .filter(i -> solutionList.get(i)>=originalColumns)
                                    .mapToObj(i -> rowLabel[i])
                                    .collect(Collectors.toList());
        }

        //Sum of the maximum possible assignment (values of the elements described in SolutionStringMap)
        int sum =
                IntStream.range(0, originalLength)
                        .map(i -> comparisonArray[i][solutionList.get(i)])
                        .sum();

        //Time after which the algorithm is finished (started in solve())
        long time = System.currentTimeMillis() - startTime;

        result = new Result(solutionStringMap, noMatchList, sum, time); //result is accessible later via getResult()
        return result;
    }

    /**
     * Smallest element not covered by covered rows/columns
     */
    private int getSmallestValueFromUncovered() {
        int[][] uncoveredArray = getUncoveredArray();
        return minInArray(uncoveredArray, false);
    }

    /**
     * Array not covered by covered rows/columns
     */
    private int[][] getUncoveredArray() {
        Collections.sort(coveredColumns, Collections.reverseOrder());
        return IntStream.range(0, solvingArray.length)
                .filter(e -> !isRowCoverd(e))
                .mapToObj(e -> solvingArray[e])
                .map(e -> convertArrayToList(e))
                .map(e -> {
                    coveredColumns.forEach(i -> e.remove((int) i));
                    return e;
                })
                .map(list -> {
                    return list.stream().mapToInt(i -> i).toArray();
                })
                .toArray(int[][]::new);
    }

    private boolean isColumnCovered(int index) {
        return coveredColumns.contains(index);
    }

    private boolean isPrimed(int indexY, int indexX) {
        return (solvingArray[indexY][indexX] == PRIMED);
    }

    private boolean isRowCoverd(int index) {
        return coveredRows.contains(index);
    }

    private boolean isStarred(int indexY, int indexX) {
        return (solvingArray[indexY][indexX] == STARRED);
    }

    /**
     * If {@link #solvingArray} has an smaller or equal amount of rows and columns, 'dummy' rows (filled with 0s)
     * are added at the end.
     * @throws IllegalArgumentException if there are more rows than columns
     */
    private int[][] makeSquare(int[][] arrayParam) {
        //copies 2d-array so argument-array doesn't get changed
        int[][] array = new int[arrayParam.length][];
        for (int i = 0; i < array.length; i++) {
            array[i] = arrayParam[i].clone();
        }
        //length of the longest row (to prevent indexOutOfBoundsExceptions), shorter rows will be filled with 0s
        int columns = Arrays.stream(array)
                .mapToInt(row -> row.length)
                .max()
                .getAsInt();
        int rows = array.length;


        int max = Integer.max(columns, rows);
        dummyColumns = max - columns;

        int[][] squareArray = new int[max][max];
        //fill empty elements inside row to get each row to an equal length (max)
        int[][] finalArray = array;
        IntStream.range(0, rows)
                .forEach(i -> {
                    squareArray[i] = Arrays.copyOf(finalArray[i], max); //empty elements will be filled with 0s (see Arrays API)
                });


        //fill empty rows with 'dummy' rows (0 - values)
        IntStream.range(rows, max)
                .forEach(i -> {
                    IntStream.range(0, max).
                            forEach(e -> squareArray[i][e] = 0);
                    dummyRows++;
                });
        return squareArray;
    }


    /**
     * min in 2d-array
     * @param belowZero whether values below zero (later starred or primed values) shall be considered
     * @throws NoSuchElementException if array is empty (or if belowZero=false, only contains elements below zero)
     */
    private int minInArray(int[][] array, boolean belowZero) {
        if (array == null)
            throw new IllegalArgumentException("Array is null");
        OptionalInt r = Arrays.stream(array)
                .map(row -> minInArray(row, belowZero))
                .filter(i -> i.isPresent())
                .mapToInt(i -> i.getAsInt())
                .min();
        return r.getAsInt();
    }

    /**
     * min in 1d-array
     * @param belowZero whether values below zero (later starred or primed values) shall be considered
     */
    private OptionalInt minInArray(int[] array, boolean belowZero) {
        IntStream minStream = Arrays.stream(array);
        if (!belowZero)
            minStream = minStream.filter(i -> i >= MIN_IN_TABLE);
        OptionalInt min = minStream.min();
        return min;
    }

    /**
     * Minimum value in column in {@link #solvingArray}
     * @param belowZero whether values below zero (later starred or primed values) shall be considered
     */
    private int minInColumn(int index, boolean belowZero) {
        if (index > solvingArray[0].length)
            throw new IllegalArgumentException();
        return minInArray(columnToArray(index), belowZero).getAsInt();
    }

    /**
     * Minimum value in column in {@link #solvingArray}
     * @param belowZero whether values below zero (later starred or primed values) shall be considered
     */
    private int minInRow(int index, boolean belowZero) {
        if (index > solvingArray.length - 1)
            throw new IllegalArgumentException();
        int[] row = solvingArray[index];//Arrays.copyOf(solvingArray[index], solvingArray[index].length);
        return minInArray(row, belowZero).getAsInt();
    }

    /**
     * Prepares the array for the other steps of the hungarian algorithm by creating 0s and converting all negative values
     * to positive.
     * After this method the {@link #solvingArray} does not contain an natural(existing from the beginning) values below
     * zero, so negative values are used for {@link #STARRED} or {@link #STARRED} (which is important for the boolean
     * belowZero which is frequently used as argument).
     */
    private void preliminaries() throws SolvedException {
        // subtract the smallest element of each row from every element of that row
        for (int iRow = 0; iRow < solvingArray.length; iRow++) {
            int minInRow = minInRow(iRow, true);
            if (minInRow != 0) {
                addToRow(iRow, -minInRow, true);
            }
        }
        // subtract the smallest element of each column from every element in that column
        for (int iCol = 0; iCol < solvingArray[0].length; iCol++) {
            int minInCol = minInColumn(iCol, true);
            if (minInCol != 0) {
                addToColumn(iCol, -minInCol, true);
            }
        }
        // * every 0, where neither the row nor the columns contains any 0*
        //cover every column containing a 0*
        for (int iRow = 0; iRow < solvingArray.length; iRow++) {
            for (int iCol = 0; iCol < solvingArray[0].length; iCol++) {
                //every element of the array
                if (solvingArray[iRow][iCol] == 0) {
                    if ((!rowContains(iRow, STARRED)) && (!columnContains(iCol, STARRED))) {
                        setStarred(iRow, iCol);
                        coverColumn(iCol);
                    }
                }

            }
        }
        if (coveredColumns.size() == solvingArray[0].length) {
            throw new SolvedException();
        }
        step1();
    }

    /**
     * Print solvingArray and covered rows/colunms (FOR DEBUGGING USE ONLY)
     * @param text
     */
    private void printArray(String text) {
        System.out.println(text + Arrays.deepToString(solvingArray));
        System.out.println("covererd col/row: " + coveredColumns + " " + coveredRows);
    }

    private boolean rowContains(int indexRow, int content) {
        return convertArrayToList(solvingArray[indexRow]).contains(content);
    }

    /**
     * @param indexOfRow
     * @param element
     * @return Index inside the row ( equal to the index of the column)
     */
    private int rowIndexOfElement(int indexOfRow, int element) {
        int r = convertArrayToList(solvingArray[indexOfRow]).indexOf(Integer.valueOf(element));
        return r;
    }

    private void setPrimed(int indexY, int indexX) {
        solvingArray[indexY][indexX] = PRIMED;
    }

    private void setStarred(int indexY, int indexX) {
        solvingArray[indexY][indexX] = STARRED;
    }

    private void setZero(int indexY, int indexX) {
        solvingArray[indexY][indexX] = 0;
    }

    /**
     * Consider every uncovered 0 of the array and prime it.
     * If row of this 0' contains 0* cover this row and uncover column of 0*, otherwise see step2.
     */
    private void step1() throws SolvedException {
        //possible improvement: getuncoveredArray() ?
        for (int iRow = 0; iRow < solvingArray.length; iRow++) {
            if (!isRowCoverd(iRow)) {
                for (int iCol = 0; iCol < solvingArray[0].length; iCol++) {
                    if (!isColumnCovered(iCol) && solvingArray[iRow][iCol] == 0) {
                        //every uncovered 0 of the solvingArray
                        setPrimed(iRow, iCol);
                        if (rowContains(iRow, STARRED)) {
                            coverRow(iRow);
                            uncoverColumn(rowIndexOfElement(iRow, STARRED));
                            step1(); //restart(otherwise last un-/cover operations are not enforced)
                        } else {
                            step2(iRow, iCol);
                            return;
                        }
                    }

                }
            }
        }
        if (coveredColumns.size() == solvingArray[0].length) {
            throw new SolvedException();
        }
        step3();
    }

    /**
     * Creates sequence of alternating 0* and 0', after using this sequence the amount of
     * @param indexX of the last 0 which was primed
     */
    private void step2(int indexY, int indexX) throws SolvedException {
        coveredColumns.clear();
        coveredRows.clear();
        //zXXXX [0] = Y (Row)
        //zXXXX [1] = X (Column)
        List<Integer> sequenceY = new ArrayList();
        List<Integer> sequenceX = new ArrayList();
        int[] zPrime = {indexY, indexX};
        sequenceY.add(indexY);
        sequenceX.add(indexX);
        int[] zStar = new int[2];


        while (columnContains(zPrime[1], STARRED)) {
            zStar[1] = zPrime[1];
            zStar[0] = columnIndexOfElement(zPrime[1], STARRED);
            sequenceY.add(zStar[0]);
            sequenceX.add(zStar[1]);

            zPrime[0] = zStar[0];
            zPrime[1] = rowIndexOfElement(zStar[0], PRIMED);
            sequenceY.add(zPrime[0]);
            sequenceX.add(zPrime[1]);

        }
        //Every 0 of the sequence:
        //0* -> 0 ; 0' -> 0*
        IntStream.range(0, sequenceX.size())
                .forEach(i -> {
                    int x = sequenceX.get(i);
                    int y = sequenceY.get(i);
                    if (isPrimed(y, x)) {
                        setStarred(y, x);
                        coverColumn(x);
                    } else
                        setZero(y, x);
                });

        //because all previously covered columns were uncovered, columns of 0* which were not part of the sequence are covered
        IntStream.range(0, solvingArray[0].length)
                .filter(i -> !isColumnCovered(i))
                .filter(i -> columnContains(i, STARRED))
                .forEach(i -> coverColumn(i));

        if (coveredColumns.size() == solvingArray.length) {
            throw new SolvedException();
        } else {
            step1();
        }
    }

    /**
     * The smallest not covered value gets added to each twice-covered element of the solvingArray and sutracted from each
     * noncovered element.
     * This leads to at least one more uncovered 0, which can get utilised in step1().
     * Starred and Primed Zeroes are not modified (belowZero argument is false) because they are covered just once.
     */
    private void step3() throws SolvedException {
        int h = getSmallestValueFromUncovered();
        //subtract h from each uncovered column
        IntStream.range(0, solvingArray[0].length)
                .filter(i -> !isColumnCovered(i))
                .forEach(i -> {
                    addToColumn(i, -h, false);
                });
        //add h to each covered row
        coveredRows.forEach(i -> addToRow(i, h, false));
        step1();
    }

    private void uncoverColumn(int index) {
        coveredColumns.remove(Integer.valueOf(index));
    }

    private void uncoverRow(int index) {
        coveredRows.remove(Integer.valueOf(index));
    }

}

