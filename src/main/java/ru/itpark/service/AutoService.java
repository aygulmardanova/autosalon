package ru.itpark.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import ru.itpark.domain.Auto;
import ru.itpark.utils.Utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
1. Просмотра каталога (каталог - набор строк с автомобилями)
2. Поиска в каталоге (по марке, году, цвету, мощности)
3. Добавления (с изображением)
4. Изменения
5. Удаления
6. Просмотра деталей
7. Выгрузки в CSV (при этом вставляется ссылка на картинку - строкой)
8. Загрузки из CSV (при этом для существующих объектов происходит обновление,
для несуществующих - добавление, в качестве картинки для добавляемых прописывается заглушка)
*/

public class AutoService {
    private final DataSource ds;

    private static final String[] HEADERS = {"id", "name", "year", "color", "power", "image"};
    private static final String DEFAULT_IMG_NAME = "default.jpg";

    private static final String findAllQuery = "SELECT id, name, year, color, power, image FROM autos";
    private static final String insertAutoQuery = "INSERT INTO autos\n" +
            "(id, name, year, color, power, image)\n" +
            "VALUES (?, ?, ?, ?, ?, ?);";
    private static final String updateByIdQuery = "UPDATE autos\n" +
            "SET name = ?, year = ?, color = ?, power = ?, image = ?\n" +
            "WHERE id = ?;";
    private static final String existsByIdQuery = "SELECT * FROM autos WHERE id = ?";
    private static final String findByIdQuery = "SELECT id, name, year, color, power, image FROM autos WHERE id = ?";
    private static final String findByNameQuery = "SELECT id, name, year, color, power, image FROM autos WHERE name = ?";
    private static final String findByYearQuery = "SELECT id, name, year, color, power, image FROM autos WHERE year = ?";
    private static final String findByColorQuery = "SELECT id, name, year, color, power, image FROM autos WHERE color = ?";
    private static final String findByPowerQuery = "SELECT id, name, year, color, power, image FROM autos WHERE power = ?";
    private static final String deleteAllQuery = "DELETE FROM autos;";
    private static final String deleteByIdQuery = "DELETE FROM autos\n" +
            "WHERE id = ?;";


    public AutoService() throws NamingException {
        var context = new InitialContext();
        ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
    }

    // JdbcTemplate
    public List<Auto> getAll() {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.createStatement()) {
                try (var rs = stmt.executeQuery(findAllQuery)) {
                    var list = new ArrayList<Auto>();

                    while (rs.next()) {
                        list.add(getAutoFromRS(rs));
                    }

                    return list;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void generateCsv(String filename) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.createStatement()) {
                try (var rs = stmt.executeQuery(findAllQuery)) {
                    File file = new File(filename);
                    FileWriter out = new FileWriter(file);
                    CSVPrinter csvPrinter = CSVFormat.DEFAULT
                            .withDelimiter(';')
                            .withHeader(rs)
                            .print(out);
                    csvPrinter.printRecords(rs);
                    csvPrinter.flush();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void create(String name, Integer year, String color, Integer power, Part file) throws IOException {
        var fileName = file != null
                ? UUID.randomUUID().toString()
                : null;
        if (file != null)
            file.write(fileName);

        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(insertAutoQuery)) {
                setParametersForAutoCreation(stmt,
                        new Auto(
                                null, name, year, color, power, fileName)
                );
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void createFromCsv(Part file) throws IOException {
        Reader in = new InputStreamReader(file.getInputStream());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(in);

        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(insertAutoQuery)) {
                for (CSVRecord record : records) {
                    String image = record.get("image");
                    String fileName = null;
                    if (Files.exists(Paths.get(image))) {
                        fileName = UUID.randomUUID().toString();
                        Files.copy(Paths.get(image), Paths.get(Utils.UPLOAD_DIR).resolve(fileName));
                    }
                    Auto auto = new Auto(record.get("id"),
                            record.get("name"),
                            Integer.valueOf(record.get("year")),
                            record.get("color"),
                            Integer.valueOf(record.get("power")),
                            fileName);
                    if (existsById(record.get("id"))) {
                        updateById(auto);
                    } else {
                        setParametersForAutoCreation(stmt, auto);
                        stmt.execute();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void updateById(Auto auto) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(updateByIdQuery)) {

                stmt.setString(1, auto.getName());
                stmt.setInt(2, auto.getYear());
                stmt.setString(3, auto.getColor());
                stmt.setInt(4, auto.getPower());
                stmt.setString(5, auto.getImage() != null ? auto.getImage() : DEFAULT_IMG_NAME);
                stmt.setString(6, auto.getId());
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean existsById(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(existsByIdQuery)) {

                stmt.setString(1, id);
                try (var rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Auto getById(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(findByIdQuery)) {

                stmt.setString(1, id);
                try (var rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("404");
                    }

                    return getAutoFromRS(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Auto> findByName(String name) {
        return findByStringParameter(name, findByNameQuery);
    }

    public List<Auto> findByYear(int year) {
        return findByIntParameter(year, findByYearQuery);
    }

    public List<Auto> findByColor(String color) {
        return findByStringParameter(color, findByColorQuery);

    }

    public List<Auto> findByPower(int power) {
        return findByIntParameter(power, findByPowerQuery);
    }

    private List<Auto> findByStringParameter(String value, String query) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, value);
                try (var rs = stmt.executeQuery()) {
                    var list = new ArrayList<Auto>();

                    while (rs.next()) {
                        list.add(getAutoFromRS(rs));
                    }
                    return list;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<Auto> findByIntParameter(int value, String query) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, value);
                try (var rs = stmt.executeQuery()) {
                    var list = new ArrayList<Auto>();

                    while (rs.next()) {
                        list.add(getAutoFromRS(rs));
                    }
                    return list;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteById(String id) {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(deleteByIdQuery)) {

                stmt.setString(1, id);
                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        try (var conn = ds.getConnection()) {
            try (var stmt = conn.prepareStatement(
                    deleteAllQuery)) {

                stmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Auto getAutoFromRS(ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var name = rs.getString("name");
        var year = rs.getInt("year");
        var color = rs.getString("color");
        var power = rs.getInt("power");
        var image = rs.getString("image");
        return new Auto(id, name, year, color, power, image);
    }

    private void setParametersForAutoCreation(PreparedStatement stmt, Auto auto) throws SQLException {
        stmt.setString(1, UUID.randomUUID().toString());
        stmt.setString(2, auto.getName());
        stmt.setInt(3, auto.getYear());
        stmt.setString(4, auto.getColor());
        stmt.setInt(5, auto.getPower());
        stmt.setString(6, auto.getImage() != null ? auto.getImage() : DEFAULT_IMG_NAME);
    }

}
