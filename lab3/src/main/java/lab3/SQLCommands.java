package lab3;

public interface SQLCommands {
    String sqlSelectTables = "SELECT table_name\n" +
                             "FROM information_schema.tables\n" +
                             "WHERE table_schema = 'public'\n" +
                             "ORDER BY table_name;";
    String[] sqlSelectColumns = new String[] { "SELECT column_name\n" +
                              "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                              "WHERE TABLE_NAME = '", "' AND column_default IS NULL;", "';"};

    String sqlGenerateRows = "DELETE FROM i_otime;\n" +
                             "DELETE FROM pass;\n" +
                             "DELETE FROM cardriver;\n" +
                             "DELETE FROM car;\n" +
                             "DELETE FROM driver;\n" +
                             "INSERT INTO car(car_id, model) " +
                             "SELECT car_id, chr(trunc(65 + random()*25)::int) model " +
                             "FROM generate_series(1, ?) car_id;\n" +
                             "INSERT INTO driver(driver_id, first_name, last_name, phone) " +
                             "SELECT driver_id, " +
                             "chr(trunc(65 + random()*25)::int) || chr(trunc(65 + random()*25)::int) first_name, " +
                             "chr(trunc(65 + random()*25)::int) || chr(trunc(65 + random()*25)::int) first_name, " +
                             "trunc(random() * 100000)::int phone " +
                             "FROM generate_series(1, ?) driver_id;\n" +
                             "INSERT INTO cardriver(car_id, driver_id) " +
                             "SELECT car_id, driver_id FROM generate_series(1, ?) car_id, generate_series(1, ?) driver_id " +
                             "ORDER BY random() LIMIT ?;\n" +
                             "INSERT INTO pass(car_id, place_number, has_charger, tariff, pass_id) " +
                             "SELECT 1 + random() * (? - 1), num, random() > 0.5 has_charger, random() * 20 + 10 tariff, num " +
                             "FROM generate_series(1, ?) num;" +
                             "INSERT INTO i_otime(pass_id, time_in, time_out, time_id) " +
                             "SELECT 1 + random() * (? - 1), timestamp '2022-01-10 20:00:00' + trunc(random() * 1000)::int * INTERVAL '1 hours', " +
                             "timestamp '2022-02-21 12:00:00' + trunc(random() * 1000) * INTERVAL '1 hours', id " +
                             "FROM generate_series(1, ?) id;";
    String[] sqlSelectFilterCarDriver = new String[] {"SELECT driver_id, first_name, last_name, phone, model, car_id " +
                             "FROM driver INNER JOIN cardriver USING(driver_id) INNER JOIN car USING(car_id) " +
                             "WHERE (model LIKE '", "' OR last_name LIKE '","') AND driver_id BETWEEN ? AND ? ;"};
    String sqlSelectFilterPassCarDriver = "SELECT first_name, last_name, phone, model, place_number, tariff " +
                             "FROM pass INNER JOIN car USING(car_id) " +
                             "INNER JOIN cardriver USING(car_id) " +
                             "INNER JOIN driver USING(driver_id) " +
                             "WHERE tariff BETWEEN ? AND ? AND (has_charger = ? OR place_number BETWEEN ? AND ?);";
    String[] sqlSelectFilterIOTimePassCar = new String[] {"SELECT car_id, model, place_number, tariff, time_in, time_out " +
                             "FROM i_otime INNER JOIN pass USING(pass_id) " +
                             "INNER JOIN car USING(car_id) " +
                             "WHERE time_in::TIMESTAMP BETWEEN ? AND ? AND has_charger = ? OR model LIKE '", "';"};
}
