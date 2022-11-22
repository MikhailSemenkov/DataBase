import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    private final Model model;
    private final View view;
    private final Scanner scanner;

    public Controller() {
            model = new Model();
            view = new View();
            scanner = new Scanner(System.in);
            int menuNumber;
            do {
                menuNumber = scanner.nextInt();
                switch (menuNumber) {
                    case 1 -> {
                        String tableName = chooseTable();
                        List<String> list = model.getColumnsList(tableName, true);
                        List<Integer> numbers = chooseColumns(list);
                        printResultTable(tableName, list, numbers);
                    }
                    case 2 -> {
                        String tableName = chooseTable();
                        List<String> values = getValuesForCreate(tableName);
                        view.printCreateResult(model.create(tableName, values));
                    }
                    case 3 -> {
                        String tableName = chooseTable();
                        System.out.println("Choose value that you want change");
                        String columnName = getChangeColumnName(tableName);
                        String newValue = getChangeNewValue();
                        String[] equalsTo = getPrimaryKeyValues(tableName);
                        view.printUpdateResult(model.update(tableName, columnName, newValue, equalsTo));
                    }
                    case 4 -> {
                        String tableName = chooseTable();
                        String[] primaryKeyValues = getPrimaryKeyValues(tableName);
                        view.printDeleteResult(model.delete(tableName, primaryKeyValues));
                    }
                    case 5 -> model.enableAutocommit();
                    case 6 -> model.disableAutocommit();
                    case 7 -> model.commit();
                    case 8 -> model.rollback();
                    case 9 -> {
                        System.out.println("Enter amount of rows");
                        int amountOfRows = scanner.nextInt();
                        model.generateRows(amountOfRows);
                    }
                    case 10 -> {
                        int menuNum = getFilterMenuNum();
                        List<String> params = getFilterParams(menuNum);
                        List<String> columnsNames = getFilterColumnsNames(menuNum);
                        long begTime = System.currentTimeMillis();
                        view.printTable(model.readWithFilter(menuNum, params), columnsNames);
                        long endTIme = System.currentTimeMillis();
                        System.out.println("Query executed in " + (endTIme - begTime) + " ms");
                    }
                }
                view.printMenu();
            } while (menuNumber != 11);
            model.closeConnection();
    }

    private int getFilterMenuNum() {
        List<String> list = new LinkedList<>();
        list.add("Car, Driver");
        list.add("Pass, Car, Driver");
        list.add("IOTime, Pass, Car");
        view.printMenu(list, false);
        return scanner.nextInt();
    }

    private List<String> getFilterParams(int menuNum) {
        List<String> params = new LinkedList<>();
        switch (menuNum) {
            case 1 -> {
                scanner.nextLine();
                System.out.println("Enter template for string model filter)");
                System.out.println("(% - any kind and num of symbols");
                System.out.println("_ - any kind one symbol");
                params.add(scanner.nextLine());
                System.out.println("Enter template for string last_name filter)");
                System.out.println("(% - any kind and num of symbols");
                System.out.println("_ - any kind one symbol");
                params.add(scanner.nextLine());
                System.out.println("Range of driver id begins with");
                params.add(scanner.next());
                System.out.println("Range of driver id ends with");
                params.add(scanner.next());
            }
            case 2 -> {
                System.out.println("Range of tariff begins with");
                params.add(scanner.next());
                System.out.println("Range of tariff ends with");
                params.add(scanner.next());
                System.out.println("Did place has charger (boolean true or false)");
                params.add(scanner.next());
                System.out.println("Range of place number begins with");
                params.add(scanner.next());
                System.out.println("Range of place number ends with");
                params.add(scanner.next());
            }
            case 3 -> {
                scanner.nextLine();
                System.out.println("Range of time in begins with");
                params.add(scanner.nextLine());
                System.out.println("Range of time in ends with");
                params.add(scanner.nextLine());
                System.out.println("Did place has charger (boolean true or false)");
                params.add(scanner.next());
                scanner.nextLine();
                System.out.println("Enter template for string model filter)");
                System.out.println("(% - any kind and num of symbols");
                System.out.println("_ - any kind one symbol");
                params.add(scanner.nextLine());
            }
        }
        return params;
    }

    private List<String> getFilterColumnsNames(int menuNum) {
        List<String> columnsNames = new LinkedList<>();
        switch (menuNum) {
            case 1 -> {
                columnsNames.add("driver_id");
                columnsNames.add("first_name");
                columnsNames.add("last_name");
                columnsNames.add("phone");
                columnsNames.add("model");
                columnsNames.add("car_id");
            }
            case 2 -> {
                columnsNames.add("first_name");
                columnsNames.add("last_name");
                columnsNames.add("phone");
                columnsNames.add("model");
                columnsNames.add("place_number");
                columnsNames.add("tariff");
            }
            case 3 -> {
                columnsNames.add("car_id");
                columnsNames.add("model");
                columnsNames.add("place_number");
                columnsNames.add("tariff");
                columnsNames.add("time_in");
                columnsNames.add("time_out");
            }
        }
        return columnsNames;
    }

    private String[] getPrimaryKeyValues(String tableName) {
        System.out.println("Filter value (PK) should be equals to");
        String[] equalsTo = new String[2];
        equalsTo[0] = scanner.next();
        if (tableName.equals("cardriver")) {
            System.out.println("Second part of filter should be equals to");
            equalsTo[1] = scanner.next();
        }
        return equalsTo;
    }

    private String getChangeNewValue() {
        System.out.println("Enter new value");
        return scanner.next();
    }

    private String getChangeColumnName(String tableName) {
        List<String> list = model.getColumnsList(tableName, true);
        view.printMenu(list, false);
        int number = scanner.nextInt();
        return list.get(number - 1);
    }

    private List<String> getValuesForCreate(String tableName) {
        List<String> list = model.getColumnsList(tableName, false);
        List<String> values = new LinkedList<>();
        System.out.println("Set new values");
        scanner.nextLine();
        for (String field : list) {
            System.out.println("Enter " + field);
            values.add(scanner.nextLine());
        }
        return values;
    }

    private String chooseTable() {
        System.out.println("Choose table");
        List<String> list = model.getTablesList();
        view.printMenu(list, false);
        int menuNumber = scanner.nextInt();
        return list.get(menuNumber - 1);
    }

    private List<Integer> chooseColumns(List<String> list) {
        System.out.println("Choose columns (write numbers with comma without spaces)");
        view.printMenu(list, true);
        List<Integer> numbers = new LinkedList<>();
        String[] receivedNumbers = scanner.next().split(",");
        for (String number : receivedNumbers) {
            numbers.add(Integer.parseInt(number));
        }
        return numbers;
    }

    private void printResultTable(String tableName, List<String> list, List<Integer> numbers) {
        StringBuilder params = new StringBuilder();
        int amountOfColumn = 0;
        List<String> columnsNames = new LinkedList<>();
        for (int element : numbers) {
            if (element == 1) {
                params.append("*");
                amountOfColumn = list.size();
                columnsNames = list;
                break;
            }
            params.append(list.get(element - 2)).append(", ");
            columnsNames.add(list.get(element - 2));
            amountOfColumn++;
        }
        if (params.length() > 1) {
            params.delete(params.length() - 2, params.length());
        }
        view.printTable(model.read(params.toString(), tableName, amountOfColumn), columnsNames);
    }
}
