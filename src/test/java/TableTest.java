import org.junit.*;
import org.junit.Test; 
import static org.junit.Assert.*;


public class TableTest {

	@Test
	public void testSelect() {

		//Create a table
		Table movie = new Table ("movie", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");	

		//Prepare Comparable arrays to put into table.
		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
		Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
		Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
		Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };

		//Fill table with tuples.
		movie.insert (film0);
		movie.insert (film1);
		movie.insert (film2);
		movie.insert (film3);

		//Select a single Tuple based on a KeyType
		Table t_iselect2 = movie.select (new KeyType ("Rambo"));

		//Assert that the number of tuples in the table is 1.
		assertEquals(1, t_iselect2.getTuples().size());
	}

	@Test
	public void testTypeCheck() {
		//Create a table
		Table movie = new Table ("movie", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");	

		//Prepare Comparable arrays to put into table.
		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
		Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
		Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
		Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };

		//Create a tuple for which TypeCheck will return false.
		Comparable [] badTuple = { "Rambo", 1978, 100, "action", "Universal", 32355, "ABCD" };

		//Assert that film0 returns true and that the badTuple returns false when it runs through the typeCheck method.
		System.out.println("\n\n" + movie.testTypeCheck(film0));
		System.out.println("\n\n" + movie.testTypeCheck(badTuple));

		assertTrue(movie.testTypeCheck(film0));
		assertFalse(movie.testTypeCheck(badTuple));
	}

	@Test
	public void testUnion() {
		//Create tables
		Table movie = new Table ("movie", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table cinema = new Table ("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		//Prepare Comparable arrays to put into table.
		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
		Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
		Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
		Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
		Comparable [] film4 = { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };

		//Fill table with tuples.
		//Note that film2 and film3 are in both tables.
		movie.insert (film0);
		movie.insert (film1);
		movie.insert (film2);
		movie.insert (film3);
		cinema.insert (film2);
		cinema.insert (film3);
		cinema.insert (film4);

		//Call the union method on the two tables.
		Table resultTable = movie.union (cinema);

		//Assert that the number of tuples in the unioned table is 7.
		assertEquals(7, resultTable.getTuples().size());
	}

	@Test
	public void testMinus() {

		//Create tables
		Table movie = new Table ("movie", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		Table cinema = new Table ("cinema", "title year length genre studioName producerNo",
				"String Integer Integer String String Integer", "title year");

		//Prepare Comparable arrays to put into table.
		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
		Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
		Comparable [] film2 = { "Rocky", 1985, 200, "action", "Universal", 12125 };
		Comparable [] film3 = { "Rambo", 1978, 100, "action", "Universal", 32355 };
		Comparable [] film4 = { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };

		//Fill table with tuples.
		//Note that film2 and film3 are in both tables.
		movie.insert (film0);
		movie.insert (film1);
		movie.insert (film2);
		movie.insert (film3);
		cinema.insert (film2);
		cinema.insert (film3);
		cinema.insert (film4);

		//Call the minus method on the two tables.
		Table resultTable = movie.minus (cinema);


		//Assert that the number of tuples in the result table is 2.
		assertEquals(2, resultTable.getTuples().size());
	}
}
