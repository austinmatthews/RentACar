 
/*****************************************************************************************
 * @file  TestTupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 */

import static java.lang.System.out;
import java.io.*;
import java.util.*;

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
        
        int tups [] = new int [] { 100000, 1000, 2000, 50000, 1000 };

        
        Table [] tabless = { Student, Professor, Course, Teaching, Transcript };
    
        /*Comparable [][][] resultTest = test.generate (tups);
        
        for (int i = 0; i < resultTest.length; i++) {

            for (int j = 0; j < resultTest [i].length; j++) {
            	tabless[i].insert(resultTest[i][j]);
            } // for
        } // for */

        testJoin();

        
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
    
    

    public static void testJoin(){

        TupleGenerator tester  = new TupleGeneratorImpl ();
        TupleGenerator tester2 = new TupleGeneratorImpl ();

        tester.addRelSchema ("Student",
                           "id name address status",
                           "Integer String String String",
                           "id",
                           null);

        tester2.addRelSchema ("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester",
                           null);

        Table Student = new Table ("Student",
            "id name address status",
            "Integer String String String",
            "id");

        Table Transcript = new Table("Transcript",
           "studId crsCode semester grade",
           "Integer String String String",
           "studId crsCode semester");
        int tups [] = new int [] {100000};
        int tup2 [] = new int [] {100000};

        Comparable [][][] result  = tester.generate(tups);
        Comparable [][][] result2 = tester2.generate(tup2);
        Table [] tables = {Student, Transcript};

        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[i].length; j++){
                tables[0].insert(result[i][j]);
            }
        }
        for(int i = 0; i < result2.length; i++){
            for(int j = 0; j < result2[i].length; j++){
                tables[1].insert(result2[i][j]);
            }
        }

        try{
            PrintWriter writer = new PrintWriter(new FileWriter(new File("a.txt"), true));
           
            long start = System.nanoTime();
            Table t_join = Transcript.indexJoin("studId", "id", Student);
            long end = System.nanoTime();
            long diff = (end - start) / 1000000;
            
            System.out.println("Time difference: " + diff);

            writer.println(diff);
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

} // TestTupleGenerator

