package lab3;

import java.util.List;

public class View {
    View() {
        printMenu();
    }

    void printMenu() {
        System.out.println("Choose action:");
        System.out.println("1 Read");
        System.out.println("2 Create");
        System.out.println("3 Update");
        System.out.println("4 Delete");
        System.out.println("5 Commit");
        System.out.println("6 Rollback");
        System.out.println("7 Generate random values");
        System.out.println("8 Filter");
        System.out.println("9 close program");
    }

    void printMenu(List<String> list, boolean needAll) {
        if (needAll) {
            System.out.println("1 All");
        }
        int number = (needAll)? 2 : 1;
        for (String name : list) {
            System.out.println(number + " " + name);
            number++;
        }
    }

    public void printTable(List<List<String>> list, List<String> columnsNames) {
        for (String columnName : columnsNames) {
            System.out.printf("%-30s| ", columnName);
        }
        System.out.println();
        for (List<String> line : list) {
            for (String field : line) {
                System.out.printf("%-30s| ", field);
            }
            System.out.println();
        }
    }

    public void printCreateResult(int amount) {
        System.out.println("Successfully created " + amount + " rows");
    }

    public void printUpdateResult(int amount) {
        System.out.println("Successfully updated " + amount + " rows");
    }

    public void printDeleteResult(int amount) {
        System.out.println("Successfully deleted " + amount + " rows");
    }
}
