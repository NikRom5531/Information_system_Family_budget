package com.example.kursproject;

import javafx.stage.Stage;

import java.io.IOException;

// TODO GET OUT OF HERE QUICKLY
public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Информационная система «Семейный бюджет»");
        ControlStages controlStages = new ControlStages(stage);
        ControlStages.changeScene(URLs.URL_MAIN);
    }

    public static void main(String[] args) {
        launch();
    }
}

/*
    Название                | Name                      | Controller                        | table
    --------------------------------------------------------------------------------------------------------------------
    доходы                  | income                    | IncomeController                  | incomes
    расходы                 | expenses                  | ExpensesController                | expenses
    члены семьи             | family members            | FamilyMembersController           | family_members
    источники доходов       | sources of income         | SourcesIncomeController           | income_sources
    категории доходов       | income categories         | CategoriesIncomeController        | income_categories
    подкатегории доходов    | income subcategories      | SubcategoriesIncomeController     | income_subcategories
    категории расходов      | expenditure categories    | CategoriesExpenditureController   | expense_categories
    --------------------------------------------------------------------------------------------------------------------

    onGoToExpensesWindowButtonClick                 // "Открыть таблицу расходов"
    onGoToFamilyMembersWindowButtonClick            // "Открыть таблицу членов семьи"
    onGoToSourcesIncomeWindowButtonClick            // "Открыть таблицу источников дохода"
    onGoToCategoriesIncomeWindowButtonClick         // "Открыть таблицу категорий доходов"
    onGoToSubcategoriesIncomeWindowButtonClick      // "Открыть таблицу подкатегорий дохода"
    onGoToCategoriesExpenditureWindowButtonClick    // "Открыть таблицу категорий расходов"
*/
/*
    14. Проектирование информационной системы в  СУБД MS Access
    15. Разработка логического и физического уровня модели данных в ERwin
    16. Язык SQL. Использование языка описания данных DDL
    17. Язык SQL. Использование языка описания данных DML
    18. Язык SQL. Команда SELECT – выборка данных
    19. Разработка таблиц удаленной базы данных с использованием клиент-серверной технологии
    20. Разработка бизнес-логики на стороне SQL-сервера
    21. Визуальное представление набора данных БД в разрабатываемой информационной системе
    22. Фильтрация, поиск и сортировка данных таблиц БД в разрабатываемой информационной системе
    23. Программирование действий с набором записей из нескольких связанных по внешнему ключу таблиц базы данных
    24. Построение SQL запросов к базе данных в разрабатываемой информационной системе
    25. Составление отчетов по данным базы в разрабатываемой информационной системе
 */