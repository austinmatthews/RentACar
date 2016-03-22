-- 2
SELECT name FROM Student WHERE Student.id BETWEEN v2 AND v3

-- 4
SELECT s.name FROM Student s JOIN Trancript tr ON tr.studId = s.id JOIN Teaching t ON t.crsCode = tr.crsCode
AND t.semester = tr.semester JOIN Professor p ON p.id = t.profId WHERE p.name = 'name274422';

-- 6
SELECT s.name FROM Student s JOIN ( (SELECT crsCode FROM Course WHERE Course.deptId = 'deptId853934') AS t1 
JOIN Transcript ON t1.crsCode = Transcript.crsCode) AS t2
ON t2.studid = s.id;

/*
Brandon General Tuning
Most of the query speed ups occured by adding indexes to certain columns. 
I added indices on the keys for each table.
I also added indices on certain columns that were used frequently by many of the queries. 
I did not however put an index on every column that was used. Adding indices slows down UPDATE and INSERT speeds. 
So it is bad pratice to add indices to every column. Generally speaking, you can add indices to 
frequently used columns. My graphs shows several trials for the original queries and then the sped up queries. 

2 Tuning 
For this query I sped it up by adding an index on the column Student.id. The table was already set up properly 
so there was no need to change it. Also PostgreSQL rewrites the query for you. It automatically 
uses Hash Joins and rewrites the query to perform more efficiently. There is little to no difference with 
rewriting the queries because PostgreSQL's built in optimizer does it automatically. 

4 Tuning
For this query I sped it up by adding an index on the columns: Transcript.studId, Professor.name, Professor.profId, 
Teaching.profId, Teaching.(crsCode, semester), Transcript.(crsCode, semester, studId). 
Again PSQL uses Hash Joins automatically. Also PSQL rewrites the query to be more efficient, meaning 
it performs the operations in the right
order.

6 Tuning
For this query I sped it up by adding an index on the columns used in query 4. 
Hash Joins are used automatically so no need to force the database to use it. 
Also the optimizer performs the most efficient order of operations. */