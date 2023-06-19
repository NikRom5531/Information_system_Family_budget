CREATE TABLE Family_Members
(
    ID              INT PRIMARY KEY,
    First_Name      VARCHAR(255) NOT NULL,
    Last_Name       VARCHAR(255) NOT NULL,
    Additional_Info VARCHAR(255)
);

CREATE TABLE Income_Subcategories
(
    ID   INT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL
);

CREATE TABLE Income_Categories
(
    ID             INT PRIMARY KEY,
    Name           VARCHAR(255) NOT NULL,
    Subcategory_ID INT          NOT NULL,
    FOREIGN KEY (Subcategory_ID) REFERENCES Income_Subcategories (ID)
);

CREATE TABLE Income_Sources
(
    ID                 INT PRIMARY KEY,
    Name               VARCHAR(255) NOT NULL,
    Income_Category_ID INT          NOT NULL,
    Description        VARCHAR(255),
    FOREIGN KEY (Income_Category_ID) REFERENCES Income_Categories (ID)
);

CREATE TABLE Income
(
    ID               INT PRIMARY KEY,
    Date             DATE           NOT NULL,
    Family_Member_ID INT            NOT NULL,
    Income_Source_ID INT            NOT NULL,
    Amount           DECIMAL(10, 2) NOT NULL,
    Description      VARCHAR(255)   NOT NULL,
    Status           VARCHAR(255)   NOT NULL,
    Comment          VARCHAR(255),
    FOREIGN KEY (Family_Member_ID) REFERENCES Family_Members (ID),
    FOREIGN KEY (Income_Source_ID) REFERENCES Income_Sources (ID)
);

CREATE TABLE Expense_Categories
(
    ID   INT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL
);

CREATE TABLE Expenses
(
    ID                  INT PRIMARY KEY,
    Date                DATE           NOT NULL,
    Family_Member_ID    INT            NOT NULL,
    Expense_Category_ID INT            NOT NULL,
    Amount              DECIMAL(10, 2) NOT NULL,
    Description         VARCHAR(255)   NOT NULL,
    Status              VARCHAR(255)   NOT NULL,
    Comment             VARCHAR(255),
    FOREIGN KEY (Family_Member_ID) REFERENCES Family_Members (ID),
    FOREIGN KEY (Expense_Category_ID) REFERENCES Expense_Categories (ID)
);
