import static java.lang.System.out;

class JoinTest{

	public static void main(String args[]){

		Table movie = new Table("movie", "title year length genre",
			"String Integer Integer String", "title");

		Table cinema = new Table ("cinema", "title year length studioName",
			"String Integer Integer String", "title");

		Comparable [] film0 = { "Star_Wars", 1977, 124, "sciFi"};
		Comparable [] film1 = { "Star_Wars_2", 1980, 124, "sciFi"};
		Comparable [] film2 = { "Rocky", 1985, 200, "action"};
		Comparable [] film3 = { "Rambo", 1978, 100, "action"};

		movie.insert(film0);
		movie.insert(film1);
		movie.insert(film2);
		movie.insert(film3);

		movie.print ();

		cinema.insert(film0);
		cinema.insert(film1);
		cinema.insert(film2);
		cinema.insert(film3);

		cinema.print ();

		out.println ();
		Table t_join = movie.join (cinema);
		t_join.print ();

		Table nul = null;

		Table null_join = movie.join (nul);
		null_join.print ();

        out.println ();
        Table natural_join = movie.join ("genre", "studioName", cinema);
        natural_join.print ();

        out.println ();
        Table wrong_join = movie.join("somethingElse", "studioName", cinema);
        wrong_join.print ();
        out.println("This should be an empty table^^\n");

        out.println ();
        Table wrong_join2 = movie.join("genre", "wrongAttr", cinema);
        wrong_join2.print ();
        out.println("This should be an empty table^^\n");


	}

}