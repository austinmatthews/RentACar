-- 2
SELECT name FROM Student WHERE Student.id BETWEEN v2 AND v3

-- 4
SELECT s.name FROM Student s JOIN Trancript tr ON tr.studId = s.id JOIN Teaching t ON t.crsCode = tr.crsCode
AND t.semester = tr.semester JOIN Professor p ON p.id = t.profId WHERE p.name = 'name274422';

-- 6
SELECT s.name FROM Student s JOIN ( (SELECT crsCode FROM Course WHERE Course.deptId = 'deptId853934') AS t1 
JOIN Transcript ON t1.crsCode = Transcript.crsCode) AS t2
ON t2.studid = s.id;