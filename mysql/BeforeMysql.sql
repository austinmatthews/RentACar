USE db2;

-- Queries ----


-- 1
SELECT name FROM Student WHERE Student.id = 787457;

-- 2
SELECT name FROM Student WHERE Student.id BETWEEN 25633 AND 902846;

-- 3
SELECT s.name FROM Student s JOIN Transcript t ON t.studId = s.id WHERE t.crsCode = 'crsCode484738';


-- 4
SELECT s.name FROM Student s JOIN Transcript tr ON tr.studId = s.id JOIN Teaching t ON t.crsCode = tr.crsCode AND t.semester = tr.semester JOIN Professor p ON p.id = t.profId WHERE p.name = 'name244706';

-- 5
SELECT name FROM Student s JOIN Transcript t ON s.id = t.studId JOIN Course c ON c.crsCode = t.crsCode WHERE c.deptId = 'deptId210330'
NOT IN
(SELECT name FROM Student s JOIN Transcript t ON s.id = t.studId JOIN Course c ON c.crsCode = t.crsCode WHERE c.deptId = 'deptId328787');


-- 6
SELECT student.name  FROM (SELECT crsCode FROM Course WHERE Course.deptId = 'deptId595927') AS t1 JOIN Transcript ON t1.crsCode = Transcript.crsCode JOIN Student ON transcript.studId = student.id;






