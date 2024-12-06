
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Advent {
    public static void main(String[] args) throws FileNotFoundException {
        puzzleFour();
    }

    static void puzzleOne() throws FileNotFoundException {
        Scanner in = new Scanner(new File("./inputs/puzzleOne.txt"));

        PriorityQueue<Integer> leftNumbers = new PriorityQueue<>();
        PriorityQueue<Integer> rightNumbers = new PriorityQueue<>();

        Map<Integer, Integer> rightCounts = new HashMap<>();

        while (in.hasNextLine()) {
            String[] line = in.nextLine().split("   ");
            int leftNumber = Integer.parseInt(line[0]);
            leftNumbers.add(leftNumber);
            int rightNumber = Integer.parseInt(line[1]);
            rightNumbers.add(rightNumber);

            rightCounts.merge(rightNumber, 1, Integer::sum);
        }

        int totalDistance = 0;
        int similarityScore = 0;

        while (!leftNumbers.isEmpty()) {
            int leftNumber = leftNumbers.poll();
            int rightNumber = rightNumbers.poll();
            totalDistance += Math.abs(leftNumber - rightNumber);
            similarityScore += leftNumber * rightCounts.getOrDefault(leftNumber, 0);
        }

        // Part A
        System.out.println(totalDistance);

        // Part B
        System.out.println(similarityScore);
    }

    static void puzzleTwo() throws FileNotFoundException {
        Scanner in = new Scanner(new File("./inputs/puzzleTwo.txt"));
        int numSafe = 0;

        while (in.hasNextLine()) {
            String line = in.nextLine();
            List<Integer> levels = Arrays.asList((line.split(" "))).stream().map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));

            if (isSafe(levels)) {
                numSafe++;
                continue;
            }

            for (int i = 0; i < levels.size(); i++) {
                int toRemove = levels.get(i);
                levels.remove(i);

                if(isSafe(levels)) {
                    numSafe++;
                    break;
                }

                levels.add(i, toRemove);
            }
        }

        System.out.println(numSafe);
    }

    static int difference(int int1, int int2) {
        return Math.abs(int1 - int2);
    }

    static boolean isSafe(List<Integer> levels) {
        if (levels.size() <= 1) {
            return false;
        }

        int firstTwoNumbersDiff = difference(levels.get(0), levels.get(1));

        if (firstTwoNumbersDiff > 3 || firstTwoNumbersDiff < 1) {
            return false;
        }

        boolean firstTwoNumbersAreIncreasing = levels.get(0) < levels.get(1);
        boolean isSafe = true;

        for (int i = 2; i < levels.size(); i++) {
            int current = levels.get(i);
            int prev = levels.get(i-1);

            boolean isIncreasing = prev < current;

            if (isIncreasing != firstTwoNumbersAreIncreasing) {
                isSafe = false;
                break;
            }

            int diff = difference(current, prev);

            if (diff > 3 || diff < 1) {
                isSafe = false;
                break;
            }
        }

        return isSafe;
    }

    static void puzzleThree() throws FileNotFoundException {
        Scanner in = new Scanner(new File("./inputs/puzzleThree.txt"));
        int sum = 0;

        while (in.hasNext()) {
            String line = in.nextLine();
            sum += getMuls(line);
        }

        System.out.println(sum);
    }

    static boolean mulsEnabled = true;

    static int getMuls(String line) {
        String regex = "mul\\((\\d{1,3}),(\\d{1,3})\\)|do\\(\\)|don't\\(\\)";
        int sum = 0;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            if (matcher.group().equals("do()")) {
                mulsEnabled = true;
            } else if (matcher.group().equals("don't()")) {
                mulsEnabled = false;
            } else if (mulsEnabled) {
                sum += Integer.parseInt(matcher.group(1)) * Integer.parseInt(matcher.group(2));
            }
        }

        return sum;
    }

    static void puzzleFour() throws FileNotFoundException {
        Scanner in = new Scanner(new File("./inputs/puzzleFour.txt"));
        List<List<Character>> map = new ArrayList<>();

        while (in.hasNext()) {
            String line = in.nextLine();
            map.add(line.chars().mapToObj(c -> (char) c).toList());
        }

        int numXmas = 0;

        for (int row = 0; row < map.size(); row++) {
            for (int col = 0; col < map.get(0).size(); col++) {
                // if (map.get(row).get(col).equals('X')) {
                //     numXmas += getXmasAt(row, col, map);
                // }

                if (isXmas(row, col, map)) {
                    numXmas++;
                }
            }
        }

        System.out.println(numXmas);
    }

    static int getXmasAt(int row, int col, List<List<Character>> map) {
        int numXmas = 0;

        int[] offRows = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] offCols = {-1,  0,  1,-1, 0, 1,-1, 0, 1};
        char[] expectedChars = {'M', 'A', 'S'};

        for (int i = 0; i < offRows.length; i++) {
            int currRow = row + offRows[i];
            int currCol = col + offCols[i];

            for (int j = 0; j < expectedChars.length; j++) {
                char expectedChar = expectedChars[j];

                if (!isExpected(currRow, currCol, expectedChar, map)) {
                    break;
                }

                currRow += offRows[i];
                currCol += offCols[i];

                if (j == expectedChars.length - 1) {
                    numXmas++;
                }
            }
        }

        return numXmas;
    }

    static boolean isExpected(int row, int col, char expectedChar, List<List<Character>> map) {
        if (row < 0 || row >= map.size() || col < 0 || col >= map.get(0).size()) {
            return false;
        }

        return map.get(row).get(col).equals(expectedChar);
    }

    static boolean isXmas(int row, int col, List<List<Character>> map) {
        // Part B
        if (!map.get(row).get(col).equals('A')) {
            return false;
        }

        if (row - 1 < 0 || row + 1 >= map.size() || col - 1 < 0 || col + 1 >= map.get(0).size()) {
            // A is on the edge
            return false;
        }

        if (map.get(row - 1).get(col - 1).equals(map.get(row + 1).get(col + 1)) ||
            map.get(row - 1).get(col + 1).equals(map.get(row + 1).get(col - 1))) {
            // Diagonals form MAM or SAS
            return false;
        }

        int numM = 0;
        int numS = 0;

        int[] offRows = {-1, -1, 1, 1};
        int[] offCols = {-1, 1, -1, 1};

        for (int i = 0; i < offRows.length; i++) {
            int currRow = row + offRows[i];
            int currCol = col + offCols[i];

            if (map.get(currRow).get(currCol).equals('M')) {
                numM++;
            } else if (map.get(currRow).get(currCol).equals('S')) {
                numS++;
            }
        }

        return numM == 2 && numS == 2;
    }
}
