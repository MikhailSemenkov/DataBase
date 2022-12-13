package lab3;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lab3.hibernate.HibernateUtil;
import lab3.hibernate.entities.Car;
import lab3.hibernate.entities.Driver;
import lab3.hibernate.entities.IOTime;
import lab3.hibernate.entities.Pass;
import org.hibernate.JDBCException;
import org.hibernate.Session;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Model {
    private final Connection connection;
    private final Session session;

    Model() {
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
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

    List<List<String>> read(List<String> params, String tableName) {
        List<List<String>> tablesList = new LinkedList<>();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        switch (tableName) {
            case "car" -> {
                CriteriaQuery<Car> cq = cb.createQuery(Car.class);
                Root<Car> rootEntry = cq.from(Car.class);
                CriteriaQuery<Car> all = cq.select(rootEntry);

                TypedQuery<Car> allQuery = session.createQuery(all);
                List<Car> resultList = allQuery.getResultList();

                int i = 0;
                for (Car car : resultList) {
                    tablesList.add(new LinkedList<>());
                    for (String param : params) {
                        switch (param) {
                            case "car_id" -> tablesList.get(i).add(String.valueOf(car.getCarId()));
                            case "model" -> tablesList.get(i).add(car.getModel());
                        }
                    }
                    i++;
                }
            }
            case "cardriver" -> {
                CriteriaQuery<Car> cq = cb.createQuery(Car.class);
                Root<Car> rootEntry = cq.from(Car.class);
                CriteriaQuery<Car> all = cq.select(rootEntry);

                TypedQuery<Car> allQuery = session.createQuery(all);
                List<Car> resultList = allQuery.getResultList();

                int i = 0;
                for (Car car : resultList) {
                    for (Driver driver : car.getDrivers()) {
                        tablesList.add(new LinkedList<>());
                        for (String param : params) {
                            switch (param) {
                                case "car_id" -> tablesList.get(i).add(String.valueOf(car.getCarId()));
                                case "driver_id" -> tablesList.get(i).add(String.valueOf(driver.getDriverId()));
                            }
                        }
                        i++;
                    }
                }
            }
            case "driver" -> {
                CriteriaQuery<Driver> cq = cb.createQuery(Driver.class);
                Root<Driver> rootEntry = cq.from(Driver.class);
                CriteriaQuery<Driver> all = cq.select(rootEntry);

                TypedQuery<Driver> allQuery = session.createQuery(all);
                List<Driver> resultList = allQuery.getResultList();

                int i = 0;
                for (Driver driver : resultList) {
                    tablesList.add(new LinkedList<>());
                    for (String param : params) {
                        switch (param) {
                            case "driver_id" -> tablesList.get(i).add(String.valueOf(driver.getDriverId()));
                            case "first_name" -> tablesList.get(i).add(driver.getFirstName());
                            case "last_name" -> tablesList.get(i).add(driver.getLastName());
                            case "phone" -> tablesList.get(i).add(driver.getPhone());
                        }
                    }
                    i++;
                }
            }
            case "pass" -> {
                CriteriaQuery<Pass> cq = cb.createQuery(Pass.class);
                Root<Pass> rootEntry = cq.from(Pass.class);
                CriteriaQuery<Pass> all = cq.select(rootEntry);

                TypedQuery<Pass> allQuery = session.createQuery(all);
                List<Pass> resultList = allQuery.getResultList();

                int i = 0;
                for (Pass pass : resultList) {
                    tablesList.add(new LinkedList<>());
                    for (String param : params) {
                        switch (param) {
                            case "car_id" -> tablesList.get(i).add(String.valueOf(pass.getCarId()));
                            case "place_number" -> tablesList.get(i).add(String.valueOf(pass.getPlaceNumber()));
                            case "has_charger" -> tablesList.get(i).add(String.valueOf(pass.isHasCharger()));
                            case "tariff" -> tablesList.get(i).add(String.valueOf(pass.getTariff()));
                            case "pass_id" -> tablesList.get(i).add(String.valueOf(pass.getPassId()));
                        }
                    }
                    i++;
                }
            }
            case "i_otime" -> {
                CriteriaQuery<IOTime> cq = cb.createQuery(IOTime.class);
                Root<IOTime> rootEntry = cq.from(IOTime.class);
                CriteriaQuery<IOTime> all = cq.select(rootEntry);

                TypedQuery<IOTime> allQuery = session.createQuery(all);
                List<IOTime> resultList = allQuery.getResultList();

                int i = 0;
                for (IOTime ioTime : resultList) {
                    tablesList.add(new LinkedList<>());
                    for (String param : params) {
                        switch (param) {
                            case "pass_id" -> tablesList.get(i).add(String.valueOf(ioTime.getPassId()));
                            case "time_in" -> tablesList.get(i).add(String.valueOf(ioTime.getTimeIn()));
                            case "time_out" -> tablesList.get(i).add(String.valueOf(ioTime.getTimeOut()));
                            case "time_id" -> tablesList.get(i).add(String.valueOf(ioTime.getTimeId()));
                        }
                    }
                    i++;
                }
            }
            default -> System.out.println("Wrong table name");
        }

        return tablesList;
    }

    public List<List<String>> readWithFilter(int menuNum, List<String> params) {
        List<List<String>> result = new LinkedList<>();
        try (PreparedStatement ps = readWithFilterPrepareStatement(menuNum, params);
             ResultSet rs = ps.executeQuery()) {
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
        try {
            switch (tableName) {
                case "car" -> {
                    Car car = new Car();
                    car.setCarId(Integer.parseInt(values.get(0)));
                    car.setModel(values.get(1));
                    session.persist(car);
                    result = 1;
                }
                case "cardriver" -> {
                    Car car = session.find(Car.class, Integer.parseInt(values.get(0)));
                    Driver driver = session.find(Driver.class, Integer.parseInt(values.get(1)));
                    if (car != null && driver != null) {
                        car.getDrivers().add(driver);
                        result = 1;
                    } else {
                        System.out.println("Wrong car_id or driver_id value");
                    }
                }
                case "driver" -> {
                    Driver driver = new Driver();
                    driver.setDriverId(Integer.parseInt(values.get(0)));
                    driver.setFirstName(values.get(1));
                    driver.setLastName(values.get(2));
                    driver.setPhone(values.get(3));
                    session.persist(driver);
                    result = 1;
                }
                case "pass" -> {
                    Car car = session.find(Car.class, Integer.parseInt(values.get(0)));
                    if (car != null) {
                        Pass pass = new Pass();
                        pass.setCarId(Integer.parseInt(values.get(0)));
                        pass.setPlaceNumber(Integer.parseInt(values.get(1)));
                        pass.setHasCharger(Boolean.parseBoolean(values.get(2)));
                        pass.setTariff(Integer.parseInt(values.get(3)));
                        pass.setCar(car);
                        session.persist(pass);
                        result = 1;
                    }else {
                        System.out.println("Wrong car_id value");
                    }
                }
                case "i_otime" -> {
                    Pass pass = session.find(Pass.class, Integer.parseInt(values.get(0)));
                    if (pass != null) {
                        IOTime ioTime = new IOTime();
                        ioTime.setPassId(Integer.parseInt(values.get(0)));
                        ioTime.setTimeIn(Timestamp.valueOf(values.get(1)));
                        ioTime.setTimeOut(Timestamp.valueOf(values.get(2)));
                        ioTime.setPass(pass);
                        session.persist(ioTime);
                        result = 1;
                    } else {
                        System.out.println("Wrong pass_id value");
                    }
                }
                default -> System.out.println("Wrong table name");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Wrong value");
        }
        return result;
    }

    int update(String tableName, String changeValue, String newValue, String[] filterValues) {
        int result = -1;
        switch (tableName) {
            case "car" -> {
                Car car = session.find(Car.class, filterValues[0]);
                if (car != null) {
                    switch (changeValue) {
                        case "car_id" -> car.setCarId(Integer.parseInt(newValue));
                        case "model" -> car.setModel(newValue);
                    }
                    result = 1;
                }
            }
            case "cardriver" -> {
                Car car = session.find(Car.class, filterValues[0]);
                Driver driver = session.find(Driver.class, filterValues[1]);
                if (car != null && driver != null) {
                    car.getDrivers().remove(driver);
                    driver.getCars().remove(car);
                    switch (changeValue) {
                        case "car_id" -> car = session.find(Car.class, Integer.parseInt(newValue));
                        case "driver_id" -> driver = session.find(Driver.class, Integer.parseInt(newValue));
                    }
                    driver.getCars().add(car);
                    car.getDrivers().add(driver);
                    result = 1;
                }
            }
            case "driver" -> {
                Driver driver = session.find(Driver.class, filterValues[0]);
                if (driver != null) {
                    switch (changeValue) {
                        case "driver_id" -> driver.setDriverId(Integer.parseInt(newValue));
                        case "first_name" -> driver.setFirstName(newValue);
                        case "last_name" -> driver.setLastName(newValue);
                        case "phone" -> driver.setPhone(newValue);
                    }
                    result = 1;
                }
            }
            case "pass" -> {
                Pass pass = session.find(Pass.class, filterValues[0]);
                if (pass != null) {
                    switch (changeValue) {
                        case "car_id" -> pass.setCarId(Integer.parseInt(newValue));
                        case "place_number" -> pass.setPlaceNumber(Integer.parseInt(newValue));
                        case "has_charger" -> pass.setHasCharger(Boolean.parseBoolean(newValue));
                        case "tariff" -> pass.setTariff(Integer.parseInt(newValue));
                        case "pass_id" -> pass.setPassId(Integer.parseInt(newValue));
                    }
                    result = 1;
                }
            }
            case "i_otime" -> {
                IOTime ioTime = session.find(IOTime.class, filterValues[0]);
                if (ioTime != null) {
                    switch (changeValue) {
                        case "pass_id" -> ioTime.setPassId(Integer.parseInt(newValue));
                        case "time_in" -> ioTime.setTimeIn(Timestamp.valueOf(newValue));
                        case "time_out" -> ioTime.setTimeOut(Timestamp.valueOf(newValue));
                        case "time_id" -> ioTime.setTimeId(Integer.parseInt(newValue));
                    }
                    result = 1;
                }
            }
            default -> System.out.println("Wrong table name");
        }
        return result;
    }

    int delete(String tableName, String[] primaryKeyValues) {
        int result = -1;
        switch (tableName) {
            case "car" -> {
                Car car = session.find(Car.class, primaryKeyValues[0]);
                if (car != null) {
                    session.remove(car);
                    result = 1;
                }
            }
            case "cardriver" -> {
                Car car = session.find(Car.class, primaryKeyValues[0]);
                Driver driver = session.find(Driver.class, primaryKeyValues[1]);
                if (car != null && driver != null) {
                    car.getDrivers().remove(driver);
                    driver.getCars().remove(car);
                    result = 1;
                }
            }
            case "driver" -> {
                Driver driver = session.find(Driver.class, primaryKeyValues[0]);
                if (driver != null) {
                    session.remove(driver);
                    result = 1;
                }
            }
            case "pass" -> {
                Pass pass = session.find(Pass.class, primaryKeyValues[0]);
                if (pass != null) {
                    session.remove(pass);
                    result = 1;
                }
            }
            case "i_otime" -> {
                IOTime ioTime = session.find(IOTime.class, primaryKeyValues[0]);
                if (ioTime != null) {
                    session.remove(ioTime);
                    result = 1;
                }
            }
            default -> System.out.println("Wrong table name");
        }
        return result;
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
        final Connection[] resultConnection = new Connection[1];
        session.doWork(connection -> resultConnection[0] = connection);
        return resultConnection[0];
    }

    void commit() {
        try {
            session.getTransaction().commit();
        } catch (JDBCException e) {
            System.out.println("Can not commit changes");
        }
    }

    void rollback() {
        try {
            session.getTransaction().rollback();
        } catch (JDBCException e) {
            System.out.println("Can not rollback");
        }
    }

    void closeConnection() {
        try {
            connection.close();
            session.close();
        } catch (SQLException ex) {
            System.out.println("Can not close connection");
        }
    }
}
