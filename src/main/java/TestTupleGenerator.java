 
/*****************************************************************************************
 * @file  TestTupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 */

import static java.lang.System.out;
import java.util.Date;
import java.util.Scanner;

/**
 * This class tests the TupleGenerator on the Student Registration Database defined in the
 * Kifer, Bernstein and Lewis 2006 database textbook (see figure 3.6).  The primary keys
 * (see figure 3.6) and foreign keys (see example 3.2.2) are as given in the textbook.
 */
public class TestTupleGenerator
{
    /**
     * The main method is the driver for TestGenerator.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        TupleGenerator test = new TupleGeneratorImpl ();

        test.addRelSchema ("Student",
                           "id name address status",
                           "Integer String String String",
                           "id",
                           null);
        
        test.addRelSchema ("Professor",
                           "id name deptId",
                           "Integer String String",
                           "id",
                           null);
        
        test.addRelSchema ("Course",
                           "crsCode deptId crsName descr",
                           "String String String String",
                           "crsCode",
                           null);
        
        test.addRelSchema ("Teaching",
                           "crsCode semester profId",
                           "String String Integer",
                           "crcCode semester",
                           new String [][] {{ "profId", "Professor", "id" },
                                            { "crsCode", "Course", "crsCode" }});
        
        test.addRelSchema ("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester",
                           new String [][] {{ "studId", "Student", "id"},
                                            { "crsCode", "Course", "crsCode" },
                                            { "crsCode semester", "Teaching", "crsCode semester" }});

        String [] tables = { "Student", "Professor", "Course", "Teaching", "Transcript" };
        
        Table Student = new Table ("Student",
                			"id name address status",
                			"Integer String String String",
                			"id");
        
        Table Professor = new Table("Professor",
                           "id name deptId",
                           "Integer String String",
                           "id");
        
        Table Course = new Table("Course",
                           "crsCode deptId crsName descr",
                           "String String String String",
                           "crsCode");
        
        Table Teaching = new Table("Teaching",
                           "crsCode semester profId",
                           "String String Integer",
                           "crsCode semester" );
        
        Table Transcript = new Table("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester");
        
        int tups [] = new int [] { 100000, 100, 100, 100, 100 };
        
        Table [] tabless = { Student, Professor, Course, Teaching, Transcript };
    
        Comparable [][][] resultTest = test.generate (tups);
        
        for (int i = 0; i < resultTest.length; i++) {

            for (int j = 0; j < resultTest [i].length; j++) {
            	tabless[i].insert(resultTest[i][j]);
            } // for
        } // for
        
        
        System.out.println("B+Tree Select");
        Scanner in = new Scanner(System.in);
        for (int k=0; k<5 ; k++){
        	Integer key = in.nextInt();
        	Date date = new Date();
        	long startTime = System.nanoTime();
        
        	Table t_iselect = Student.select (new KeyType (key));
        
        	long endTime = System.nanoTime();
        	System.out.println((endTime-startTime)/1000 + "micro sec");
        }
        ///
        
        
        
    } // main
    
    

} // TestTupleGenerator

