package edu.sdsu.its;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        checkEnvVars();

        final Scanner scanner = new Scanner(System.in);
        System.out.println(" ==== Grade Distiller ==== ");
        System.out.print("Course Selector: ");
        final String courseSelector = scanner.nextLine();

        System.out.print("Column Selector: ");
        final String columnSelector = scanner.nextLine();

        System.out.print("Update Course DB [y/N]: ");
        final boolean updateCourseList = scanner.nextLine().matches("[yY]");

        System.out.print("Output File [grades.csv]: ");
        String line = scanner.nextLine();
        final String fileName = !line.isEmpty() ? line : "grades.csv";

        System.out.print("\n");

        Extractor extractor = Extractor.builder()
                .courseSelector(courseSelector)
                .columnSelector(columnSelector)
                .updateCourseList(updateCourseList)
                .build();

        try {
            extractor.extract(fileName);
            System.out.println("Extraction Completed - Saved output to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkEnvVars() {
        try {
            if (System.getenv("BB_URL") == null || System.getenv("BB_URL").isEmpty())
                throw new RuntimeException("Blackboard Learn URL Not Defined");
            if (System.getenv("BB_KEY") == null || System.getenv("BB_KEY").isEmpty())
                throw new RuntimeException("Blackboard API Key Not Defined");
            if (System.getenv("BB_SECRET") == null || System.getenv("BB_SECRET").isEmpty())
                throw new RuntimeException("Blackboard API Secret Not Defined");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.err.println("At least one environment variable has not been set - aborting");
            System.exit(1);
        }
    }
}
