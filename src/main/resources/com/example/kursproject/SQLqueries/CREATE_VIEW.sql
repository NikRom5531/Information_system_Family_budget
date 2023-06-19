CREATE VIEW FamilyMembersView AS
SELECT ID, First_Name, Last_Name, Additional_Info
FROM Family_Members;

CREATE VIEW IncomeSubcategoriesView AS
SELECT ID,
       Name
FROM Income_Subcategories;

CREATE VIEW IncomeCategoriesView AS
SELECT Income_Categories.ID,
       Income_Categories.Name,
       Income_Subcategories.Name AS Subcategory_Name
FROM Income_Categories
         INNER JOIN Income_Subcategories ON Income_Categories.Subcategory_ID = Income_Subcategories.ID;

CREATE VIEW IncomeSourcesView AS
SELECT Income_Sources.ID,
       Income_Sources.Name,
       Income_Categories.Name AS Category_Name,
       Income_Subcategories.Name AS Subcategory_Name,
       Description
FROM Income_Sources
         INNER JOIN Income_Categories ON Income_Sources.Income_Category_ID = Income_Categories.ID
         INNER JOIN Income_Subcategories ON Income_Categories.Subcategory_ID = Income_Subcategories.ID;

CREATE VIEW IncomeView AS
SELECT Income.ID,
       Income.Date,
       Family_Members.First_Name,
       Family_Members.Last_Name,
       Income_Sources.Name AS Source_Name,
       Income_Categories.Name AS Category_Name,
       Income_Subcategories.Name AS Subcategory_Name,
       Income.Amount,
       Income.Description,
       Income.Status,
       Income.Comment
FROM Income
         INNER JOIN Family_Members ON Income.Family_Member_ID = Family_Members.ID
         INNER JOIN Income_Sources ON Income.Income_Source_ID = Income_Sources.ID
         INNER JOIN Income_Categories ON Income_Sources.Income_Category_ID = Income_Categories.ID
         INNER JOIN Income_Subcategories ON Income_Categories.Subcategory_ID = Income_Subcategories.ID;

CREATE VIEW ExpenseCategoriesView AS
SELECT ID,
       Name
FROM Expense_Categories;

CREATE VIEW ExpensesView AS
SELECT Expenses.ID,
       Expenses.Date,
       Family_Members.First_Name,
       Family_Members.Last_Name,
       Expense_Categories.Name AS Category_Name,
       Expenses.Amount,
       Expenses.Description,
       Expenses.Status,
       Expenses.Comment
FROM Expenses
         INNER JOIN Family_Members ON Expenses.Family_Member_ID = Family_Members.ID
         INNER JOIN Expense_Categories ON Expenses.Expense_Category_ID = Expense_Categories.ID;