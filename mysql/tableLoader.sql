USE CSCI4370;

-- Tables ----


DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Professor;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS Teaching;
DROP TABLE IF EXISTS Transcript;

CREATE TABLE Student(

	id INT,
	name VARCHAR(100),
	addr VARCHAR(100),
	status VARCHAR(100)

);

CREATE TABLE Professor(

	id INT,
	name VARCHAR(100),
	deptId VARCHAR(100)	

);

CREATE TABLE Course(

	crsCode VARCHAR(100),
	deptId  VARCHAR(100),
	crsName VARCHAR(100),
	descr   VARCHAR(100)

);

CREATE TABLE Teaching(

	crsCode VARCHAR(100),
	semester VARCHAR(100),
	profId  INT

);

CREATE TABLE Transcript(

	studId INT,
	crsCode VARCHAR(100),
	semester VARCHAR(100),
	grade VARCHAR(100)

);









