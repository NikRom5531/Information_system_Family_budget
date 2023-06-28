package com.example.kursproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class General {
    public static Object selectedObject;

    public enum Mode {
        TABLE,
        VIEW
    }

    public static <T> void setSelectedObject(T t) {
        selectedObject = t;
    }

    public static <T> void clearTFA(T component) {
        setColorTFA(component, "A9A9A9");
        if (component instanceof TextField) ((TextField) component).setText("");
        else if (component instanceof TextArea) ((TextArea) component).setText("");
    }

    public static <T> void setColorTFA(T component, String ColorHexRGB) {
        String style = "-fx-border-color: #" + ColorHexRGB + "; -fx-border-width: 1.5; -fx-border-radius: 2.5;";
        if (component instanceof TextField) ((TextField) component).setStyle(style);
        else if (component instanceof TextArea) ((TextArea) component).setStyle(style);
    }

    public static <T> ObservableList<T> filterTableData(ObservableList<T> data, String searchText, TableView<T> tableView) {
        if (searchText == null || searchText.isEmpty()) return data;
        String lowerSearchText = searchText.toLowerCase();
        ObservableList<T> filteredData = FXCollections.observableArrayList();
        for (T item : data) {
            for (TableColumn<T, ?> column : tableView.getColumns()) {
                if (String.valueOf(column.getCellObservableValue(item).getValue()).toLowerCase().contains(lowerSearchText)) {
                    filteredData.add(item);
                    break;
                }
            }
        }
        return filteredData;
    }

    public static void successfully(String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Уведомление");
        alert.setHeaderText("Успешно " + headerText + "!");
        VBox dialogPaneContent = new VBox();
        alert.getDialogPane().setContent(dialogPaneContent);
        setIconWindow(alert);
        alert.showAndWait();
    }

    public static boolean getConfirmation(String AddInfoHeader) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Подтверждение действия");
        confirmationDialog.setHeaderText("Вы уверены, что хотите выполнить это действие?\n" + AddInfoHeader);
        ButtonType buttonTypeYes = new ButtonType("Да");
        ButtonType buttonTypeNo = new ButtonType("Нет");
        setIconWindow(confirmationDialog);
        confirmationDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        AtomicBoolean result = new AtomicBoolean(false);
        confirmationDialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeYes) result.set(true);
        });
        return result.get();
    }

    public static <T> T createInputDialog(String title, List<String> labels, List<Boolean> condition, T instance) {
        Dialog<T> dialog = new Dialog<>();
        setIconWindow(dialog);
        dialog.setTitle(title); // Установка кнопок "OK" и "Отмена"
        ButtonType okButton = new ButtonType("Подтвердить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton); // Создание меток и текстовых полей для ввода данных
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        List<Object> list_T = new ArrayList<>();
        Field[] fields = instance.getClass().getDeclaredFields();
        for (int i = 0; i < labels.size(); i++) {
            grid.add(new Label(labels.get(i)), 0, i);
            Field field = fields[i + 1];
            field.setAccessible(true);
            String type = field.getType().getTypeName();
            switch (type) {
                case "boolean" -> { // TODO надо проверить ComboBox!!
                    ComboBox<String> comboBox = new ComboBox<>();
                    comboBox.setItems(FXCollections.observableArrayList("Истина", "Ложь")); //
                    comboBox.setPromptText("Выберите...");
                    try {
                        if (field.get(instance) != null) {
                            boolean bool = (boolean) field.get(instance);
                            if (bool) comboBox.getSelectionModel().select("Истина");
                            else comboBox.getSelectionModel().select("Ложь");
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    list_T.add(comboBox);
                }
                case "java.util.Date" -> { // TODO надо проверить DatePicker!!
                    DatePicker datePicker = new DatePicker();
                    datePicker.setPromptText("Выберите дату");
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        datePicker.setValue(LocalDate.parse(format.format((Date) field.get(instance))));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    list_T.add(new DatePicker(datePicker.getValue()));
                }
                default -> {
                    TextField textField;
                    try {
                        if (field.get(instance) != null) textField = new TextField(field.get(instance).toString());
                        else textField = new TextField();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    list_T.add(textField);
//                    }
                }
            }
            grid.add((Node) list_T.get(i), 1, i);
        }
        dialog.getDialogPane().setContent(grid);// Установка обработчика для кнопки "OK"
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == cancelButton) return null;
            if (dialogButton == okButton) {
                try {
                    for (int i = 0; i < labels.size(); i++) {
                        Field field = fields[i + 1];
                        field.setAccessible(true);
                        if (condition.get(i) && list_T.get(i) == null)
                            return null; // Возвращаем null, чтобы показать, что данные не прошли валидацию
                        String type = field.getType().getTypeName();
                        switch (type) {
                            case "java.lang.String", "int", "double" -> {
                                String value = null;
                                if (list_T.get(i) instanceof TextField)
                                    value = ((TextField) list_T.get(i)).getText().trim();
                                if (value != null) {
                                    if (condition.get(i) && value.isEmpty()) return null;
                                    switch (type) {
                                        case "java.lang.String" -> field.set(instance, value); // String
                                        case "int" -> field.set(instance, Integer.parseInt(value)); // Integer
                                        case "double" -> field.set(instance, Double.parseDouble(value)); // Double
                                        default -> {
                                        }
                                    }
                                }
                            }
                            case "boolean" -> { // boolean
                                if (list_T.get(i) instanceof ComboBox) {
                                    if (((ComboBox<?>) list_T.get(i)).getSelectionModel().isSelected(0))
                                        field.set(instance, true);
                                    else field.set(instance, false);
                                }
                            }
                            case "java.util.Date" -> { // Date
                                if (list_T.get(i) instanceof DatePicker) {
                                    try { // Используйте соответствующий метод для преобразования строки в Date
                                        LocalDate localDate = ((DatePicker) list_T.get(i)).getValue();
                                        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                                        field.set(instance, Date.from(instant));
                                    } catch (Exception e) { // Обработка ошибки при преобразовании строки в Date
                                        e.printStackTrace();
                                    }
                                }
                            } // Добавьте необходимую логику для других типов данных
                            default -> System.out.println("Не установленный тип данных!"); // Другой тип данных поля
                        }
                    }
                    return instance;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }); // Отображение диалогового окна и ожидание результата
        Optional<T> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static void ErrorWindow(String headerText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Уведомление об ошибке");
        alert.setHeaderText(headerText);
        VBox dialogPaneContent = new VBox();
        alert.getDialogPane().setContent(dialogPaneContent);
        setIconWindow(alert);
        alert.showAndWait();
    }

    public static <T> void setIconWindow(T iconWindow) {
        Stage stage = null;
        if (iconWindow instanceof Alert) stage = (Stage) ((Alert) iconWindow).getDialogPane().getScene().getWindow();
        else if (iconWindow instanceof Dialog)
            stage = (Stage) ((Dialog<?>) iconWindow).getDialogPane().getScene().getWindow();
        if (stage != null)
            stage.getIcons().add(new Image(Objects.requireNonNull(General.class.getResourceAsStream("images/AppIcon.png"))));
    }
}