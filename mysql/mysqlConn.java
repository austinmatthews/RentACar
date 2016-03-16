import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.*;

public class mysqlConn{

	public static void main(String[] args){

		Connection conn = null;
		PreparedStatement studInsert;
		PreparedStatement profInsert;
		PreparedStatement CourseInsert;
		PreparedStatement TeachInsert;
		PreparedStatement TransInsert;
		Comparable[][][] result;

		try{

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CSCI4370", "root", "");
			System.out.println("Database successfully connected to!");


			studInsert = conn.prepareStatement("INSERT INTO Student(id, name, addr, status) VALUES(?,?,?,?)");
			profInsert = conn.prepareStatement("INSERT INTO Professor(id, name, deptid) VALUES(?,?,?)");
			CourseInsert = conn.prepareStatement("INSERT INTO Course(crsCode, deptid, crsName, descr) VALUES(?,?,?,?)");
			TeachInsert = conn.prepareStatement("INSERT INTO Teaching(crsCode, semester, profid) VALUES(?,?,?)");
			TransInsert = conn.prepareStatement("INSERT INTO Transcript(studid, crsCode, semester, grade) VALUES(?,?,?,?)");

			TupleGenerator gen = new TupleGeneratorImpl();

			gen.addRelSchema ("Student",
					"id name address status",
					"Integer String String String",
					"id",
					null);

			gen.addRelSchema ("Professor",
					"id name deptId",
					"Integer String String",
					"id",
					null);

			gen.addRelSchema ("Course",
					"crsCode deptId crsName descr",
					"String String String String",
					"crsCode",
					null);

			gen.addRelSchema ("Teaching",
					"crsCode semester profId",
					"String String Integer",
					"crsCode semester",
					new String [][] {{ "profId", "Professor", "id" },
				{ "crsCode", "Course", "crsCode" }});

			gen.addRelSchema ("Transcript",
					"studId crsCode semester grade",
					"Integer String String String",
					"studId crsCode semester",
					new String [][] {{ "studId", "Student", "id"},
				{ "crsCode", "Course", "crsCode" },
				{ "crsCode semester", "Teaching", "crsCode semester" }});


			int[] numTups = new int[] {50, 50, 50, 50, 50};

			System.out.println("Start Generating");
			result = gen.generate(numTups);
			System.out.println("Done Generating");


			for (int i = 0; i < result[0].length; i++) {
				studInsert.setInt(1, (int) result[0][i][0]);
				studInsert.setString(2, result[0][i][1].toString());
				studInsert.setString(3, result[0][i][2].toString());
				studInsert.setString(4, result[0][i][3].toString());
				studInsert.execute();
			}
			System.out.println("Student Done");

			for (int i = 0; i < result[1].length; i++) {
				profInsert.setInt(1, (int) result[1][i][0]);
				profInsert.setString(2, result[1][i][1].toString());
				profInsert.setString(3, result[1][i][2].toString());
				profInsert.execute();
			}
			System.out.println("Professor Done");

			for (int i = 0; i < result[2].length; i++) {
				CourseInsert.setString(1, result[2][i][0].toString());
				CourseInsert.setString(2, result[2][i][1].toString());
				CourseInsert.setString(3, result[2][i][2].toString());
				CourseInsert.setString(4, result[2][i][3].toString());
				CourseInsert.execute();
			}
			System.out.println("Course Done");

			for (int i = 0; i < result[3].length; i++) {
				TeachInsert.setString(1, result[3][i][0].toString());
				TeachInsert.setString(2, result[3][i][1].toString());
				TeachInsert.setInt(3, (int) result[3][i][2]);
				TeachInsert.execute();
			}
			System.out.println("Teacher Done");

			for (int i = 0; i < result[4].length; i++) {
				TransInsert.setInt(1, (int) result[4][i][0]);
				TransInsert.setString(2, result[4][i][1].toString());
				TransInsert.setString(3, result[4][i][2].toString());
				TransInsert.setString(4, result[4][i][3].toString());
				TransInsert.execute();
			}
			System.out.println("Transfer Done");
			conn.close();


		} catch (Exception e){

			e.printStackTrace();

		}

	}

}




