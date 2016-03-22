-- 2
SELECT name FROM Student WHERE Student.id BETWEEN 50 AND 1000;

-- 4
SELECT Student.name FROM Student, Transcript, Teaching, Professor
WHERE Transcript.studId = Student.id
AND   Teaching.crsCode = Transcript.crsCode
AND   Teaching.semester = Transcript.semester
AND   Professor.id = Teaching.profId
AND   Professor.name = 'name274422';

-- 6
SELECT s.name FROM Student s JOIN ( (SELECT crsCode FROM Course WHERE Course.deptId = 'deptId853934') AS t1 
JOIN Transcript ON t1.crsCode = Transcript.crsCode) AS t2
ON t2.studId = s.id;