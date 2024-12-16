CREATE DATABASE IF NOT EXISTS FinalProject;
USE FinalProject;

DROP TABLE Grade;
DROP TABLE Assignment;
DROP TABLE Enroll;
DROP TABLE Category;
DROP TABLE Student;
DROP TABLE Class;

CREATE TABLE Class( 
	classID INT AUTO_INCREMENT PRIMARY KEY,
	description TEXT,
	sectionNumber INT,
	term VARCHAR(10),
	courseNumber VARCHAR(10)
);

CREATE TABLE Student( 
	studentID INT AUTO_INCREMENT PRIMARY KEY,
	email VARCHAR(150),
	username VARCHAR(50),
	firstname VARCHAR (20),
	lastname VARCHAR(20)
);

CREATE TABLE Category( 
	classID INT NOT NULL,
	categoryType VARCHAR(20) NOT NULL,
	weight INT NOT NULL,
	PRIMARY KEY (classID, categoryType),
	FOREIGN KEY (classID) REFERENCES Class(classID)
);

CREATE TABLE Enroll ( 
	studentID INT,
	classID INT,
	PRIMARY KEY (studentID, classID),
	FOREIGN KEY (studentID) REFERENCES Student (studentID),
	FOREIGN KEY (classID) REFERENCES Class (classID)
);

CREATE TABLE Assignment ( 
	assignmentID INT PRIMARY KEY,
	classID INT,
	assignmentType VARCHAR(20),
	name VARCHAR(100),
	description TEXT,
	pointValue INT,
	FOREIGN KEY (classID) REFERENCES Class(classID), 
	UNIQUE (classID, name)
);

CREATE TABLE Grade( 
	grade INT,
	studentID INT NOT NULL,
	assignmentID INT NOT NULL,
	PRIMARY KEY (studentID, assignmentID),
	FOREIGN KEY (studentID) REFERENCES Student (studentID),
	FOREIGN KEY (assignmentID) REFERENCES Assignment (assignmentID)
);

