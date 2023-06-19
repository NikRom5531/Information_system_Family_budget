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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandsSQL {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";

    public static <T> void fillTable(TableView<T> tableView, String nameTable, Class<T> tClass) {
        tableView.setItems(readDataFromTable(nameTable, tClass));
    }

    public static void executeUpdateQuery(String query, Object[] values) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Выполнение SQL-запроса
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < values.length; i++) {
                    statement.setObject(i + 1, values[i]);
                }
                statement.executeUpdate();
            }
            System.out.println("Данные успешно записаны в базу данных.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                        try { // TODO проблемы с таблицами VIEW
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
                            // Обработка ошибок или пропуск неподходящих столбцов
                            e.printStackTrace();
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
    public static ObservableList<Map<String, Object>> readDataFromTable_V(String tableName) { //TODO возможно не нужна
        ObservableList<Map<String, Object>> results = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM " + tableName + " ORDER BY id";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(selectQuery)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> item = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        if (value instanceof BigDecimal) {
                            value = ((BigDecimal) value).doubleValue();
                        } else if (value instanceof String strValue) {
                            if (strValue.equals("true")) {
                                value = true;
                            } else if (strValue.equals("false")) {
                                value = false;
                            }
                        }
                        item.put(columnName, value);
                    }
                    results.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorMessageSQL(e);
        }
        return results;
    }
    public static <T> void insertDataIntoTable(String tableName, T data) {
        System.out.println(data.toString());
        System.out.println();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertQuery = generateInsertQuery(tableName, data);
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                setPreparedStatementParameters(statement, data);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
        }
    }

    public static <T> void updateDataInTable(String tableName, T data, String condition) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = generateUpdateQuery(tableName, data, condition);
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                setPreparedStatementParameters(statement, data);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
        }
    }

    public static void deleteDataFromTable(String tableName, String condition) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = generateDeleteQuery(tableName, condition);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(deleteQuery);
            }
        } catch (SQLException e) {
            ErrorMessageSQL(e);
            e.printStackTrace();
        }
    }

    private static <T> String generateInsertQuery(String tableName, T data) { // не использовать
        // Генерация INSERT-запроса на основе данных объекта и имени таблицы
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(tableName).append(" (");
        Field[] fields = data.getClass().getDeclaredFields();
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

    private static <T> String generateUpdateQuery(String tableName, T data, String condition) { // не использовать
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(tableName).append(" SET ");
        Field[] fields = data.getClass().getDeclaredFields();
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

    private static <T> void setPreparedStatementParameters(PreparedStatement statement, T data) { // не использовать
        Field[] fields = data.getClass().getDeclaredFields();
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
                else if (value instanceof java.util.Date) {
                    statement.setDate(parameterIndex, new java.sql.Date(((java.util.Date) value).getTime()));
                }
                // Добавьте обработку других типов данных, если необходимо
            } catch (SQLException e) {
                ErrorMessageSQL(e);
            }
        }
    }

    public static int getFreeID(String tableName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Integer> integers = new ArrayList<>();
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
        for (int i = 0; i < integers.size(); i++) if (!integers.contains(i)) return i;
        return integers.size();
    }

    public static <T> void retrieveSortedData(String tableName, String sortColumn, Class<T> dataType) {
        List<T> sortedData = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = connection.createStatement();
            String query = "SELECT * FROM " + tableName + " ORDER BY " + sortColumn;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                T data = createObjectFromResultSet(resultSet, dataType);
                sortedData.add(data);
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
        generateUpdateQuery(tableName, sortedData, sortColumn);
//        return sortedData;
    }

    private static <T> T createObjectFromResultSet(ResultSet resultSet, Class<T> dataType) throws SQLException {
        try {
            T data = dataType.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(columnName);
                setFieldValue(data, columnName, value);
            }
            return data;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> void setFieldValue(T object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void ErrorMessageSQL(SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(e.getMessage());
        VBox dialogPaneContent = new VBox();
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.showAndWait();
    }

    public static boolean checkID(int getID, String tableName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Integer> integers = new ArrayList<>();
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
        return integers.contains(getID);
    }

    public static double calculateTotalBalance() {
        double totalFunds = 0.0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(Amount) AS TotalFunds FROM (SELECT Amount FROM Income WHERE Status = 'true' UNION ALL SELECT Amount FROM Expenses WHERE Status = 'true') AS AllFunds")) {
            if (rs.next()) totalFunds = rs.getDouble("TotalFunds");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalFunds;
    }
}
