package com.example.kursproject;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.awt.Desktop;

public class ReportGenerator<T> {
    private List<T> data;

    public ReportGenerator(List<T> data) {
        this.data = data;
    }

    public static <T> void createReport(String nameReport, String nameTable, Class<T> tClass, Map<String, String> columnNames, List<String> onlyColumns, String parametrs) {
        // Создание экземпляра ReportGenerator с данными таблицы FamilyMembersView
        List<T> Data = CommandsSQL.readDataFromTable(nameTable, tClass);//new ArrayList<>();
        // Заполнение данных таблицы FamilyMembersView
        ReportGenerator<T> reportGenerator = new ReportGenerator<>(Data);
        reportGenerator.generateReport(nameReport, "report_" + nameTable + ".html", columnNames, onlyColumns, parametrs); // Создание отчета в формате HTML
    }

    public static <T> void selectPath(String nameReport, ObservableList<T> Data, Map<String, String> columnNames, List<String> onlyColumns, String parametrs) { //String nameTable, Class<T> tClass,
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report"); // Устанавливаем заголовок диалогового окна
        // Устанавливаем фильтр для выбора только HTML файлов
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML Files (*.html)", "*.html");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("report.html");
        File selectedFile = fileChooser.showSaveDialog(null);
        General.setIconWindow(fileChooser);
        if (selectedFile != null) {
            String outputPath = selectedFile.getAbsolutePath();
//            List<T> Data = CommandsSQL.readDataFromTable(nameTable, tClass);//new ArrayList<>();
            ReportGenerator<T> reportGenerator = new ReportGenerator<>(Data);
            // Выполните сохранение отчета в выбранное место (outputPath)
            reportGenerator.generateReport(nameReport, outputPath, columnNames, onlyColumns, parametrs);
        }
    }

    public void generateReport(String nameReport, String filePath, Map<String, String> columnNames, List<String> onlyColumns, String parametrs) {
        if (filePath.endsWith(".html")) {
            generateHtmlReport(nameReport, filePath, columnNames, onlyColumns, parametrs);
        } else {
            throw new IllegalArgumentException("Неверный формат файла.");
        }
    }

    private void generateHtmlReport(String nameReport, String filePath, Map<String, String> columnNames, List<String> onlyColumns, String parametrs) {
        // Generate the HTML report based on the data
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n<html lang=\"ru\">\n");
        htmlBuilder.append("\t<head>\n\t\t<meta charset=\"UTF-8\" />\n");
        htmlBuilder.append("\t\t<title>").append("Отчёт").append("</title>\n");
        htmlBuilder.append("""
                        <style type="text/css">
                        .table {
                            width: 100%;
                            border: none;
                            margin-bottom: 20px;
                            font-family: "Lucida Sans Unicode", "Lucida Grande", Sans-Serif;
                        }
                        .table thead th {
                            font-weight: bold;
                            text-align: left;
                            border: none;
                            padding: 10px 15px;
                            background: #d8d8d8;
                            font-size: 14px;
                        }
                        .table thead tr th:first-child {
                            border-radius: 8px 0 0 8px;
                        }
                        .table thead tr th:last-child {
                            border-radius: 0 8px 8px 0;
                        }
                        .table tbody td {
                            text-align: left;
                            border: none;
                            padding: 10px 15px;
                            font-size: 14px;
                            vertical-align: top;
                        }
                        .table tbody tr:nth-child(even){
                            background: #f3f3f3;
                        }
                        .table tbody tr td:first-child {
                            border-radius: 8px 0 0 8px;
                        }
                        .table tbody tr td:last-child {
                            border-radius: 0 8px 8px 0;
                        }
                        </style>
                """);
        htmlBuilder.append("\t</head>\n");
        htmlBuilder.append("\t<body>\n\t\t<h1 style=\"display: flex; justify-content: center; align-items: center;\">");
        htmlBuilder.append(nameReport).append("</h1>\n");
        htmlBuilder.append("\t\t<table class=\"table\">\n");
        if (!parametrs.trim().isEmpty()) {
            htmlBuilder.append("\t\t<a style=\"display: flex;\">").append("Параметр(ы) отбора: ").append(parametrs)
                    .append("</a>\n");
        }
        htmlBuilder.append("\t\t<a style=\"display: flex;\">").append("Кол-во результатов: ").append(data.size())
                .append("</a>\n");
        // Generate table headers
        htmlBuilder.append("\t\t\t<thead>\n\t\t\t\t<tr>\n");
        for (java.lang.reflect.Field field : data.get(0).getClass().getDeclaredFields()) {
            String columnName = field.getName();
            if (onlyColumns.contains(columnName)) {
                htmlBuilder.append("\t\t\t\t\t<th>");
                htmlBuilder.append(columnNames.getOrDefault(columnName, columnName)).append("</th>\n");
            }
        }
        htmlBuilder.append("\t\t\t\t</tr>\n\t\t\t</thead>\n\t\t\t<tbody>\n");

        // Generate table rows
        for (T item : data) {
            htmlBuilder.append("\t\t\t\t<tr>\n");
            for (java.lang.reflect.Field field : item.getClass().getDeclaredFields()) {
                String columnName = field.getName();
                if (onlyColumns.contains(columnName)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(item);
                        htmlBuilder.append("\t\t\t\t\t<td>").append(value).append("</td>\n");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            htmlBuilder.append("\t\t\t\t</tr>\n");
        }
        htmlBuilder.append("\t\t\t</tbody>\n\t\t</table>\n");

        // Получаем дату и время составления отчёта
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        htmlBuilder.append("\t\t<a style=\"display: flex;\">").append("Отчет составлен: ").append(formattedDateTime).append("</a>\n");

        htmlBuilder.append("\t</body>\n");
        htmlBuilder.append("</html>");

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(htmlBuilder.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(filePath);
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file); // Открываем файл с помощью системного приложения по умолчанию
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println("Файл не найден: " + filePath);
    }
}

