package com.example.kursproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class CommandsSQL {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";

    public static <T> void fillTable(TableView<T> tableView, String nameTable, Class<T> tClass) {
        tableView.setItems(readDataFromTable(nameTable, tClass));
    }

    public static <T> ObservableList<T> getColumnValues(String tableName, String columnName, Class<T> valueType, boolean sorted) {
        ObservableList<T> values = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT DISTINCT " + columnName + " FROM " + tableName;
            if (sorted) selectQuery += " ORDER BY " + columnName;
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {
                while (resultSet.next()) {
                    T value = resultSet.getObject(columnName, valueType);
                    values.add(value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }

    public static <T> ObservableList<T> filterData(String tableName, String filterQuery, Class<T> objectType) {
        ObservableList<T> filteredData = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM " + tableName + " WHERE " + filterQuery;
            try (PreparedStatement statement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    T item = objectType.getDeclaredConstructor().newInstance();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnFieldName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        try {
                            java.lang.reflect.Field field = objectType.getDeclaredField(columnFieldName);
                            field.setAccessible(true);
                            if (field.getType().getTypeName().equals("double"))
                                value = ((BigDecimal) value).doubleValue();
                            else if (field.getType().getTypeName().equals("boolean")) {
                                if (value.equals("true")) value = true;
                                else if (value.equals("false")) value = false;
                            }
                            field.set(item, value);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();// Обработка ошибок или пропуск неподходящих столбцов
                        }
                    }
                    filteredData.add(item);
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return filteredData;
    }

    public static <T> T retrieveObjectById(String tableName, int id, Class<T> objectType) {
        T object = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                object = objectType.getDeclaredConstructor().newInstance();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    setFieldValue(object, columnName, columnValue);
                }
            } else System.out.println("Объект с id=" + id + " не найден.");
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
            assert e instanceof SQLException;
            ErrorMessageSQL((SQLException) e);
        }
        return object;
    }

    private static <T> void setFieldValue(T object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        if (field.getType().getTypeName().equals("double")) value = ((BigDecimal) value).doubleValue();
        else if (field.getType().getTypeName().equals("boolean")) {
            if (value.equals("true")) value = true;
            else if (value.equals("false")) value = false;
        }
        field.set(object, value);
    }

    public static <T> ObservableList<T> readDataFromTable(String tableName, Class<T> objectType) {
        ObservableList<T> results = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM " + tableName + " ORDER BY id";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(selectQuery)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    T item = objectType.getDeclaredConstructor().newInstance();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        try {
                            java.lang.reflect.Field field = objectType.getDeclaredField(columnName);
                            field.setAccessible(true);
                            if (field.getType().getTypeName().equals("double"))
                                value = ((BigDecimal) value).doubleValue();
                            else if (field.getType().getTypeName().equals("boolean")) {
                                if (value.equals("true")) value = true;
                                else if (value.equals("false")) value = false;
                            }
                            field.set(item, value);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace(); // Обработка ошибок или пропуск неподходящих столбцов
                        }
                    }
                    results.add(item);
                }
            }
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
            assert e instanceof SQLException;
            ErrorMessageSQL((SQLException) e);
        }
        return results;
    }

    public static <T> boolean insertDataIntoTable(String tableName, T data, List<String> fieldNames) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertQuery = generateInsertQuery(tableName, data, fieldNames);
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                setPreparedStatementParameters(statement, data, fieldNames);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static <T> boolean updateDataInTable(String tableName, T data, List<String> fieldNames, String condition) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = generateUpdateQuery(tableName, data, fieldNames, condition);
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                setPreparedStatementParameters(statement, data, fieldNames);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean deleteDataFromTable(String tableName, String condition) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = generateDeleteQuery(tableName, condition);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(deleteQuery);
            }
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static <T> String generateInsertQuery(String tableName, T data, List<String> fieldNames) { // не использовать
        StringBuilder queryBuilder = new StringBuilder(); // Генерация INSERT-запроса на основе данных объекта и имени таблицы
        queryBuilder.append("INSERT INTO ").append(tableName).append(" (");
        Field[] fields = getRequestFields(data.getClass().getDeclaredFields(), fieldNames);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            queryBuilder.append(field.getName());
            if (i < fields.length - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(") VALUES (");
        for (int i = 0; i < fields.length; i++) {
            queryBuilder.append("?");
            if (i < fields.length - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(")");
        return queryBuilder.toString();
    }

    private static <T> String generateUpdateQuery(String tableName, T data, List<String> fieldNames, String condition) { // не использовать
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(tableName).append(" SET ");
        Field[] fields = getRequestFields(data.getClass().getDeclaredFields(), fieldNames);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            queryBuilder.append(field.getName()).append(" = ?");
            if (i < fields.length - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(" WHERE ").append(condition);
        return queryBuilder.toString();
    }

    private static String generateDeleteQuery(String tableName, String condition) { // не использовать
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(tableName);
        if (condition != null && !condition.isEmpty()) queryBuilder.append(" WHERE ").append(condition);
        return queryBuilder.toString();
    }

    private static Field[] getRequestFields(Field[] fields, List<String> fieldNames) { //Parameter[] parameters
        Field[] results = new Field[fieldNames.size()];
        int i = 0;
        for (Field field : fields) for (String parameter : fieldNames) if (field.getName().equals(parameter)) results[i++] = field;
        return results;
    }

    private static <T> void setPreparedStatementParameters(PreparedStatement statement, T data, List<String> fieldNames) { // не использовать
        Field[] fields = getRequestFields(data.getClass().getDeclaredFields(), fieldNames);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
            try {
                int parameterIndex = i + 1;
                if (value instanceof Integer) statement.setInt(parameterIndex, (Integer) value);
                else if (value instanceof String) statement.setString(parameterIndex, (String) value);
                else if (value instanceof Double) statement.setDouble(parameterIndex, (Double) value);
                else if (value instanceof Boolean) statement.setBoolean(parameterIndex, (Boolean) value);
                else if (value instanceof java.util.Date) statement.setDate(parameterIndex, new java.sql.Date(((java.util.Date) value).getTime()));
                // Можно добавить обработку других типов данных, если необходимо
            } catch (SQLException e) {
                ErrorMessageSQL(e);
            }
        }
    }

    public static int getFreeID(String tableName) {
        List<Integer> integers = getIntegers(tableName);
        for (int i = 1; i < integers.size(); i++) if (!integers.contains(i)) return i;
        return integers.size() + 1;
    }

    public static boolean checkID(int getID, String tableName) {
        List<Integer> integers = getIntegers(tableName);
        return integers.contains(getID);
    }

    private static List<Integer> getIntegers(String tableName) {
        List<Integer> integers = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                integers.add(id);
            }
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                ErrorMessageSQL(e);
                e.printStackTrace();
            }
        }
        return integers;
    }

    public static void ErrorMessageSQL(SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        General.setIconWindow(alert);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(e.getMessage());
        VBox dialogPaneContent = new VBox();
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.showAndWait();
    }

    public static double calculateTotalBalance() {
        double totalFunds = 0.0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(amount) AS TotalFunds FROM (SELECT amount FROM income WHERE status = 'true' UNION ALL SELECT amount FROM expenses WHERE status = 'true') AS AllFunds")) {
            if (rs.next()) totalFunds = rs.getDouble("TotalFunds");
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
        }
        return totalFunds;
    }
}