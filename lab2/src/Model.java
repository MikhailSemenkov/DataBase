import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Model {
    private final Connection connection;

    Model() {
        connection = getConnection();
    }

    List<String> getTablesList() {
        List<String> tablesList = new LinkedList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQLCommands.sqlSelectTables)) {
            while (rs.next()) {
                tablesList.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return tablesList;
    }

    List<String> getColumnsList(String tableName, boolean fullList) {
        List<String> tablesList = new LinkedList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SQLCommands.sqlSelectColumns[0] + tableName +
                     SQLCommands.sqlSelectColumns[(fullList) ? 2 : 1])) {
            while (rs.next()) {
                tablesList.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return tablesList;
    }

    List<List<String>> read(String params, String tableName, int amountOfColumns) {
        List<List<String>> tablesList = new LinkedList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = getSelectResultSet(stmt, params, tableName)) {
            tablesList.add(new LinkedList<>());
            int lineNum = 0;
            while (rs.next()) {
                for (int i = 1; i <= amountOfColumns; i++) {
                    tablesList.get(lineNum).add(rs.getString(i));
                }
                tablesList.add(new LinkedList<>());
                lineNum++;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return tablesList;
    }

    ResultSet getSelectResultSet(Statement stmt, String params, String tableName) throws SQLException {
        return stmt.executeQuery(SQLCommands.sqlSelect[0] + params + SQLCommands.sqlSelect[1] + tableName + SQLCommands.sqlSelect[2]);
    }

    public List<List<String>> readWithFilter(int menuNum, List<String> params) {
        List<List<String>> result = new LinkedList<>();
        try (PreparedStatement ps = readWithFilterPrepareStatement(menuNum, params);
             ResultSet rs = ps.executeQuery()) {
//            System.out.println(ps);
            result.add(new LinkedList<>());
            int lineNum = 0;
            int amountOfColumns = 6;
            while (rs.next()) {
                for (int i = 1; i <= amountOfColumns; i++) {
                    result.get(lineNum).add(rs.getString(i));
                }
                result.add(new LinkedList<>());
                lineNum++;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    private PreparedStatement readWithFilterPrepareStatement(int menuNum, List<String> params) throws SQLException {
        PreparedStatement ps;
        switch (menuNum) {
            case 1 -> {
                ps = connection.prepareStatement(SQLCommands.sqlSelectFilterCarDriver[0] + params.get(0) +
                        SQLCommands.sqlSelectFilterCarDriver[1] + params.get(1) +
                        SQLCommands.sqlSelectFilterCarDriver[2]);
                ps.setInt(1, Integer.parseInt(params.get(2)));
                ps.setInt(2, Integer.parseInt(params.get(3)));
            }
            case 2 -> {
                ps = connection.prepareStatement(SQLCommands.sqlSelectFilterPassCarDriver);
                ps.setInt(1, Integer.parseInt(params.get(0)));
                ps.setInt(2, Integer.parseInt(params.get(1)));
                ps.setBoolean(3, Boolean.parseBoolean(params.get(2)));
                ps.setInt(4, Integer.parseInt(params.get(3)));
                ps.setInt(5, Integer.parseInt(params.get(4)));
            }
            case 3 -> {
                ps = connection.prepareStatement(SQLCommands.sqlSelectFilterIOTimePassCar[0] + params.get(3) +
                        SQLCommands.sqlSelectFilterIOTimePassCar[1]);
                ps.setTimestamp(1, Timestamp.valueOf(params.get(0)));
                ps.setTimestamp(2, Timestamp.valueOf(params.get(1)));
                ps.setBoolean(3, Boolean.parseBoolean(params.get(2)));
            }
            default -> throw new SQLException("Wrong menu number");
        }
        return ps;
    }

    int create(String tableName, List<String> values) {
        int result = -1;
        try (PreparedStatement ps = createPrepareStatement(tableName, values)) {
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    private PreparedStatement createPrepareStatement(String tableName, List<String> values) throws SQLException {
        PreparedStatement ps;
        switch (tableName) {
            case "car" -> {
                ps = connection.prepareStatement(SQLCommands.sqlInsertInCar);
                ps.setInt(1, Integer.parseInt(values.get(0)));
                ps.setString(2, values.get(1));
            }
            case "driver" -> {
                ps = connection.prepareStatement(SQLCommands.sqlInsertInDriver);
                ps.setInt(1, Integer.parseInt(values.get(0)));
                ps.setString(2, values.get(1));
                ps.setString(3, values.get(2));
                ps.setString(4, values.get(3));
            }
            case "cardriver" -> {
                ps = connection.prepareStatement(SQLCommands.sqlInsertInCarDriver);
                ps.setInt(1, Integer.parseInt(values.get(0)));
                ps.setInt(2, Integer.parseInt(values.get(1)));
            }
            case "pass" -> {
                ps = connection.prepareStatement(SQLCommands.sqlInsertInPass);
                ps.setInt(1, Integer.parseInt(values.get(0)));
                ps.setInt(2, Integer.parseInt(values.get(1)));
                ps.setBoolean(3, Boolean.parseBoolean(values.get(2)));
                ps.setInt(4, Integer.parseInt(values.get(3)));
            }
            case "i/otime" -> {
                ps = connection.prepareStatement(SQLCommands.sqlInsertInIOTime);
                ps.setInt(1, Integer.parseInt(values.get(0)));
                ps.setTimestamp(2, Timestamp.valueOf(values.get(1)));
                ps.setTimestamp(3, Timestamp.valueOf(values.get(2)));
            }
            default -> throw new SQLException("Wrong table name");
        }
        return ps;
    }

    int update(String tableName, String changeValue, String newValue, String[] filterValues) {
        int result = -1;
        try (PreparedStatement ps = updatePrepareStatement(tableName, changeValue, newValue, filterValues)) {
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    private PreparedStatement updatePrepareStatement(String tableName, String changeValue, String newValue, String[] filterValues) throws SQLException {
        PreparedStatement ps = null;
        switch (tableName) {
            case "car" -> {
                switch (changeValue) {
                    case "car_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateCarID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "model" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateCarModel);
                        ps.setString(1, newValue);
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                }
            }
            case "driver" -> {
                switch (changeValue) {
                    case "driver_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateDriverID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "first_name" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateDriverFirstName);
                        ps.setString(1, newValue);
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "last_name" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateDriverLastName);
                        ps.setString(1, newValue);
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "phone" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateDriverPhone);
                        ps.setString(1, newValue);
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                }
            }
            case "cardriver" -> {
                switch (changeValue) {
                    case "car_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateCarDriverCarID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                        ps.setInt(3, Integer.parseInt(filterValues[1]));
                    }
                    case "driver_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateCarDriverDriverID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                        ps.setInt(3, Integer.parseInt(filterValues[1]));
                    }
                }
            }
            case "pass" -> {
                switch (changeValue) {
                    case "pass_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdatePassID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "has_charger" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdatePassHasCharger);
                        ps.setBoolean(1, Boolean.parseBoolean(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "place_number" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdatePassPlaceNumber);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "tariff" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdatePassTariff);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "car_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdatePassCarID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                }
            }
            case "i/otime" -> {
                switch (changeValue) {
                    case "time_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateIOTimeID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "pass_id" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateIOTimePassID);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setInt(2, Integer.parseInt(filterValues[0]));
                    }
                    case "time_in" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateIOTimeIn);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setTimestamp(2, Timestamp.valueOf(filterValues[0]));
                    }
                    case "time_out" -> {
                        ps = connection.prepareStatement(SQLCommands.sqlUpdateIOTimeOut);
                        ps.setInt(1, Integer.parseInt(newValue));
                        ps.setTimestamp(2, Timestamp.valueOf(filterValues[0]));
                    }
                }
            }
            default -> throw new SQLException("Wrong table name");
        }
        return ps;
    }

    int delete(String tableName, String[] primaryKeyValues) {
        int result = -1;
        try (PreparedStatement ps = deletePrepareStatement(tableName)) {
            ps.setInt(1, Integer.parseInt(primaryKeyValues[0]));
            if (tableName.equals("cardriver")) {
                ps.setInt(2, Integer.parseInt(primaryKeyValues[1]));
            }
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    private PreparedStatement deletePrepareStatement(String tableName) throws SQLException {
        PreparedStatement ps;
        switch (tableName) {
            case "car" -> ps = connection.prepareStatement(SQLCommands.sqlDeleteCar);
            case "driver" -> ps = connection.prepareStatement(SQLCommands.sqlDeleteDriver);
            case "cardriver" -> ps = connection.prepareStatement(SQLCommands.sqlDeleteCarDriver);
            case "pass" -> ps = connection.prepareStatement(SQLCommands.sqlDeletePass);
            case "i/otime" -> ps = connection.prepareStatement(SQLCommands.sqlDeleteIOTime);
            default -> throw new SQLException("Wrong table name");
        }
        return ps;
    }

    void generateRows(int rowsAmount) {
        try (PreparedStatement ps = connection.prepareStatement(SQLCommands.sqlGenerateRows)) {
            ps.setInt(1, rowsAmount);
            ps.setInt(2, rowsAmount);
            ps.setInt(3, rowsAmount);
            ps.setInt(4, rowsAmount);
            ps.setInt(5, rowsAmount);
            ps.setInt(6, rowsAmount);
            ps.setInt(7, rowsAmount);
            ps.setInt(8, rowsAmount);
            ps.setInt(9, rowsAmount);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    Connection getConnection() {
        try {
            String[] connectionInfo = getConnectionInfo();
            return DriverManager.getConnection(connectionInfo[0], connectionInfo[1], connectionInfo[2]);
        } catch (SQLException ex) {
            System.out.println("Can not connect to data base");
        } catch (IOException ex) {
            System.out.println("Can not open file");
        }
        return null;
    }

    private String[] getConnectionInfo() throws IOException {
        String[] connectionInfo = new String[3];
        try (FileReader fileReader = new FileReader(Main.CONN_INFO_FILE_LOCATION);
             Scanner scanner = new Scanner(fileReader)) {
            while (scanner.hasNext()) {
                String line = scanner.next();
                switch (line) {
                    case "URL:" -> connectionInfo[0] = scanner.next();
                    case "USER:" -> connectionInfo[1] = scanner.next();
                    case "PASSWORD:" -> connectionInfo[2] = scanner.next();
                }
            }
        }
        return connectionInfo;
    }

    void disableAutocommit() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println("Can not change autocommit status");
        }
    }

    void enableAutocommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            System.out.println("Can not change autocommit status");
        }
    }

    void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Can not commit changes");
        }
    }

    void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println("Can not rollback");
        }
    }

    void closeConnection() {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.out.println("Can not close connection");
        }
    }
}
