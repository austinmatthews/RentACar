
/****************************************************************************************
 * @file  Table.java
 *
 * @author   Team
 */

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.Boolean.*;
import static java.lang.System.out;

/****************************************************************************************
 * This class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus and join.  The insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
implements Serializable
{
	/** Relative path for storage directory
	 */
	private static final String DIR = "store" + File.separator;

	/** Filename extension for database files
	 */
	private static final String EXT = ".dbf";

	/** Counter for naming temporary tables.
	 */
	private static int count = 0;

	/** Table name.
	 */
	private final String name;

	/** Array of attribute names.
	 */
	private final String [] attribute;

	/** Array of attribute domains: a domain may be
	 *  integer types: Long, Integer, Short, Byte
	 *  real types: Double, Float
	 *  string types: Character, String
	 */
	private final Class [] domain;

	/** Collection of tuples (data storage).
	 */
	private final List <Comparable []> tuples;

	/** Primary key. 
	 */
	private final String [] key;

	/** Index into tuples (maps key to tuple number).
	 */
	private final Map <KeyType, Comparable []> index;

	//----------------------------------------------------------------------------------
	// Constructors
	//----------------------------------------------------------------------------------

	/************************************************************************************
	 * Construct an empty table from the meta-data specifications.
	 *
	 * @param _name       the name of the relation
	 * @param _attribute  the string containing attributes names
	 * @param _domain     the string containing attribute domains (data types)
	 * @param _key        the primary key
	 */  
	public Table (String _name, String [] _attribute, Class [] _domain, String [] _key)
	{
		name      = _name;
		attribute = _attribute;
		domain    = _domain;
		key       = _key;
		tuples    = new ArrayList <> ();
//		index     = new LinHashMap <> (KeyType.class, Comparable[].class, 4);       // also try BPTreeMap, LinHashMap or ExtHashMap
		index     = new BpTreeMap<>(KeyType.class, Comparable[].class);

	} // constructor

	/************************************************************************************
	 * Construct a table from the meta-data specifications and data in _tuples list.
	 *
	 * @param _name       the name of the relation
	 * @param _attribute  the string containing attributes names
	 * @param _domain     the string containing attribute domains (data types)
	 * @param _key        the primary key
	 * @param _tuple      the list of tuples containing the data
	 */  
	public Table (String _name, String [] _attribute, Class [] _domain, String [] _key,
			List <Comparable []> _tuples)
	{
		name      = _name;
		attribute = _attribute;
		domain    = _domain;
		key       = _key;
		tuples    = _tuples;
		index     = new TreeMap <> ();       // also try BPTreeMap, LinHashMap or ExtHashMap
	} // constructor

	/************************************************************************************
	 * Construct an empty table from the raw string specifications.
	 *
	 * @param name        the name of the relation
	 * @param attributes  the string containing attributes names
	 * @param domains     the string containing attribute domains (data types)
	 */
	public Table (String name, String attributes, String domains, String _key)
	{
		this (name, attributes.split (" "), findClass (domains.split (" ")), _key.split(" "));

		out.println ("DDL> create table " + name + " (" + attributes + ")");
	} // constructor

	//----------------------------------------------------------------------------------
	// Public Methods
	//----------------------------------------------------------------------------------

	/************************************************************************************
	 * Return the list of tuples
	 *
	 * @return  the list of tuples
	 */
	public List getTuples () {
		return this.tuples;
	}
	
	
	/************************************************************************************
	 * Project the tuples onto a lower dimension by keeping only the given attributes.
	 * Check whether the original key is included in the projection.
	 *
	 * #usage movie.project ("title year studioNo")
	 *
	 * @param attributes  the attributes to project onto
	 * @return  a table of projected tuples
	 */
	public Table project (String attributes)
	{
		out.println ("RA> " + name + ".project (" + attributes + ")");
		String [] attrs     = attributes.split (" ");
		Class []  colDomain = extractDom (match (attrs), domain);
		String [] newKey    = (Arrays.asList (attrs).containsAll (Arrays.asList (key))) ? key : attrs;

		//Get int array with column numbers of the categories to keep
		int [] colPos = match(attrs);
		List <Comparable []> rows = new ArrayList <> ();
		
		//Loop through each row in the table
		for (int i = 0; i < this.tuples.size(); i++){
			//Create an empty temporary array the length of the amount of attributes we want to keep
			Comparable[] temp = new Comparable[attrs.length];
			//Get the attribute in the tuple in position corresponding to position j of colPos and store in position j of temp
			for(int j = 0; j < colPos.length; j++)
				temp[j] = this.tuples.get(i)[colPos[j]];
			//Add temp to our rows
			rows.add(temp);
		}
		
		return new Table (name + count++, attrs, colDomain, newKey, rows);
	} // project

	/************************************************************************************
	 * Select the tuples satisfying the given predicate (Boolean function).
	 *
	 * #usage movie.select (t -> t[movie.col("year")].equals (1977))
	 *
	 * @param predicate  the check condition for tuples
	 * @return  a table with tuples satisfying the predicate
	 */
	public Table select (Predicate <Comparable []> predicate)
	{
		out.println ("RA> " + name + ".select (" + predicate + ")");

		return new Table (name + count++, attribute, domain, key, tuples.stream ().filter (t -> predicate.test (t))
				.collect (Collectors.toList ()));
	} // select

	/************************************************************************************
	 * Select the tuples satisfying the given key predicate (key = value).  Use an index
	 * (Map) to retrieve the tuple with the given key value.
	 *
	 * @param keyVal  the given key value
	 * @return  a table with the tuple satisfying the key predicate
	 */
	public Table select (KeyType keyVal)
	{
		out.println ("RA> " + name + ".select (" + keyVal + ")");

		//An array list used to instantiate a new table to be returned at the completion of Select method.
		List <Comparable []> rows = new ArrayList <> ();
		
		
		//Check to see if the keyVal is in the index mapping.
		if (this.index.get(keyVal) != null)
		{
			//If keyVal is in the index mapping, we want to add that tuple to the current table.
			rows.add(this.index.get(keyVal));
		}
		
		//If the keyVal was not in the index mapping, then the returned table will be empty.
		//The columns of the new table will match the columns of this table.
		return new Table (name + count++, attribute, domain, key, rows);
	} // select

	/************************************************************************************
	 * Union this table and table2.  Check that the two tables are compatible.
	 *
	 * #usage movie.union (show)
	 *
	 * @param table2  the rhs table in the union operation
	 * @return  a table representing the union
	 */
	public Table union (Table table2){
		out.println ("RA> " + name + ".union (" + table2.name + ")");

		if (! compatible (table2)) return null;

		List <Comparable []> rows = new ArrayList <> ();
		//
		for (int i = 0; i < this.tuples.size(); i++){ //Add entries from table two to our rows
			rows.add(this.tuples.get(i));
		}
		for (int j = 0; j < table2.tuples.size(); j++){  //Add entries from table two to our rows
			rows.add(table2.tuples.get(j));
		}
		//
		return new Table (name + count++, attribute, domain, key, rows);
	} // union

	/************************************************************************************
	 * Take the difference of this table and table2.  Check that the two tables are
	 * compatible.
	 *
	 * #usage movie.minus (show)
	 *
	 * @param table2  The rhs table in the minus operation
	 * @return  a table representing the difference
	 */
	public Table minus (Table table2)
	{
		out.println ("RA> " + name + ".minus (" + table2.name + ")");
		if (! compatible (table2)) return null;

		List <Comparable []> rows = new ArrayList <> ();

		// 
		for (int i = 0; i < this.tuples.size(); i++){ //Select row in table one to search for in table two

			Comparable [] table_one_row = this.tuples.get(i);
			boolean isMatch = false;

			for (int j = 0; j < table2.tuples.size(); j++){  //Loop through each row in table two to see if we have a match from our table one row

				Comparable [] table_two_row = table2.tuples.get(j);
				if (table_one_row == table_two_row){
					isMatch = true;
				} 
			}

			if(!isMatch){ //It's not in table 2! That means we want to keep it :)
				rows.add(table_one_row);
			}
		}
		//
		return new Table (name + count++, attribute, domain, key, rows);
	} // minus

	/************************************************************************************
	 * Join this table and table2 by performing an "equi-join".  Tuples from both tables
	 * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
	 * names by append "2" to the end of any duplicate attribute name.
	 *
	 * #usage movie.join ("studioNo", "name", studio)
	 *
	 * @param attribute1  the attributes of this table to be compared (Foreign Key)
	 * @param attribute2  the attributes of table2 to be compared (Primary Key)
	 * @param table2      the rhs table in the join operation
	 * @return  a table with tuples satisfying the equality predicate
	 */
	@SuppressWarnings("unchecked")
	public Table join (String attributes1, String attributes2, Table table2)
	{
		// if table2 is null, then return table1
		if(table2 == null){
			return this;
		}

		out.println ("RA> " + name + ".join (" + attributes1 + ", " + attributes2 + ", "
				+ table2.name + ")");

		List <Comparable []> rows = new ArrayList <> ();

		try{
			String [] t_attrs = attributes1.split (" ");
			String [] u_attrs = attributes2.split (" ");

			if(t_attrs.length == u_attrs.length){
			
				Comparable[] temp = null;

			    //iterate over both tables to check if the tuple value at matching attribute position are equal
				for(int i = 0; i < table2.tuples.size(); i++){
					for(int j = 0; j < tuples.size(); j++){

						//find column positions of each attribute
						Comparable[] cols1 = extract(tuples.get(j), t_attrs);
						Comparable[] cols2 = table2.extract(table2.tuples.get(i), u_attrs);
						boolean truth = true;

						for(int k = 0; k < cols1.length; k++){
							if(cols1[k].compareTo(cols2[k]) != 0) truth = false;
						}
						if(truth){
							rows.add(ArrayUtil.concat(tuples.get(j), table2.tuples.get(i)));
						}

					}
				}
			}

			//concatenate 2 to end of attribute name if they are duplicates

			//find way to merge the 2 nested for loops

			for(int x = 0; x < table2.attribute.length; x++){
				for(int y = 0; y < attribute.length; y++){

					if(table2.attribute[x].equals(attribute[y]))
						table2.attribute[x] += 2;

				}
			}

			return new Table (name + count++, ArrayUtil.concat (attribute, table2.attribute),
					ArrayUtil.concat (domain, table2.domain), key, rows);

		} catch(ArrayIndexOutOfBoundsException e){
			// there is an ArrayIndexOutOfBoundsException thrown if one/both of the attributes provided
			// are not found in their respective tables
			System.out.println("\nCheck Query");
			System.out.println("One Or More Specified Attributes Are Incorrect\n");
			System.out.println("Table 1 Attribute: " + attributes1);
			System.out.println("Table 2 Attribute: " + attributes2);
			return new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
					ArrayUtil.concat(domain, table2.domain), key, rows);
		}

	} // join

	/************************************************************************************
	 * Join this table and table2 by performing an "natural join".  Tuples from both tables
	 * are compared requiring common attributes to be equal.  The duplicate column is also
	 * eliminated.
	 *
	 * #usage movieStar.join (starsIn)
	 *
	 * @param table2  the rhs table in the join operation
	 * @return  a table with tuples satisfying the equality predicate
	 */
	@SuppressWarnings("unchecked")
	public Table join (Table table2)
	{

		// return this if table2 is null
        if(table2 == null){
            return this;
        }

        out.println ("RA> " + name + ".join (" + table2.name + ")");
        
		List <Comparable []> rows = new ArrayList <> ();

		//keep track of attr that match or mismatch
		ArrayList<String> matching = new ArrayList<String>();
		ArrayList<String> mismatch = new ArrayList<String>();

		boolean match = false;

		//iterate over both tables to find matching and mismatching attributes
		for(int i = 0; i < table2.attribute.length; i++){
			match = false;
			for(int j = 0; j < attribute.length; j++){

				if (table2.attribute[i].equals(attribute[j])){
					matching.add(table2.attribute[i]);
					match = true;

				}

			}

			if(match == false){

				mismatch.add(table2.attribute[i]);

			}

		} //this could be removed 

		// List of rows that have been added from each table
		ArrayList<Integer> added1 = new ArrayList<Integer>();
		ArrayList<Integer> added2 = new ArrayList<Integer>();
		Comparable[] temp = new Comparable[mismatch.size()];

		// Iterate over each table
		for(int j = 0; j < tuples.size(); j++){
			for(int k = 0; k < table2.tuples.size(); k++){

				boolean tupleMatch = false;
				// Change to Loop through all of ArrayList -> Matching
				for(int m = 0; m < matching.size(); m++){
					int result = tuples.get(j)[col(matching.get(m))].compareTo(table2.tuples.get(k)[table2.col(matching.get(m))]);

					if(result == 0){
						tupleMatch = true;
					} else {
						tupleMatch = false;
						break;
					}                   
				}

				// if all the values match the other table at the corresponding matching attributes
				// and both rows have not already been added
				if(tupleMatch == true){ //change to set {}

					if(!(added1.contains(j)) && !(added2.contains(k))){

						added1.add(j);
						added2.add(k);

						//create a new tuple from table2
						for(int z = 0; z < mismatch.size(); z++){

							temp[z] = table2.tuples.get(k)[table2.col(mismatch.get(z))];

						}

						//add the tuple
						rows.add(ArrayUtil.concat(tuples.get(j), temp));

					}

				}

			}
		}

		int[] colPos = new int[mismatch.size()];
		for(int index = 0; index < mismatch.size(); index++){
			colPos[index] = table2.col(mismatch.get(index));
		} // get the column positions for extractDomain method

		String[] mm = new String[mismatch.size()];
		mismatch.toArray(mm); //get mismatched attributes

		return new Table (name + count++, ArrayUtil.concat (attribute, mm),
				ArrayUtil.concat (domain, table2.extractDom(colPos, table2.domain)), key, rows);
	} // join

	/**
	* @param attr1 - attributes from table 1
	* @param attr2 - attributes from table 2
	* @param table2 - table to be join with this
	* @return new table containing the index-join
	*/
	public Table indexJoin(String attr1, String attr2, Table table2){

		if(table2 == null){
			return this;
		}

		String [] t_attrs = attr1.split (" ");
		String [] u_attrs = attr2.split (" ");

		List<Comparable []> rows = new ArrayList<Comparable []>();

		if(t_attrs.length == u_attrs.length){

			for(Comparable [] t : tuples){

				Comparable[] match = table2.index.get(new KeyType(extract(t, t_attrs)));
				if(match != null){

					rows.add(ArrayUtil.concat(t, match));

				}

			}

			for(int x = 0; x < table2.attribute.length; x++){
				for(int y = 0; y < attribute.length; y++){

					if(table2.attribute[x].equals(attribute[y]))
						table2.attribute[x] += 2;

				}
			}

			return new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
							ArrayUtil.concat(domain, table2.domain), key, rows);
		} else {
			return null;
		}

	}

	/************************************************************************************
	 * Return the column position for the given attribute name.
	 *
	 * @param attr  the given attribute name
	 * @return  a column position
	 */
	public int col (String attr)
	{
		for (int i = 0; i < attribute.length; i++) {
			if (attr.equals (attribute [i])) return i;
		} // for

		return -1;  // not found
	} // col

	/************************************************************************************
	 * Insert a tuple to the table.
	 *
	 * #usage movie.insert ("'Star_Wars'", 1977, 124, "T", "Fox", 12345)
	 *
	 * @param tup  the array of attribute values forming the tuple
	 * @return  whether insertion was successful
	 */
	public boolean insert (Comparable [] tup)
	{
		out.println ("DML> insert into " + name + " values ( " + Arrays.toString (tup) + " )");

		if (typeCheck (tup)) {
			tuples.add (tup);
			Comparable [] keyVal = new Comparable [key.length];
			int []        cols   = match (key);
			for (int j = 0; j < keyVal.length; j++) keyVal [j] = tup [cols [j]];
			index.put (new KeyType (keyVal), tup);
			return true;
		} else {
			return false;
		} // if
	} // insert

	/************************************************************************************
	 * Get the name of the table.
	 *
	 * @return  the table's name
	 */
	public String getName ()
	{
		return name;
	} // getName

	/************************************************************************************
	 * Print this table.
	 */
	public void print ()
	{
		out.println ("\n Table " + name);
		out.print ("|-");
		for (int i = 0; i < attribute.length; i++) out.print ("---------------");
		out.println ("-|");
		out.print ("| ");
		for (String a : attribute) out.printf ("%15s", a);
		out.println (" |");
		out.print ("|-");
		for (int i = 0; i < attribute.length; i++) out.print ("---------------");
		out.println ("-|");
		for (Comparable [] tup : tuples) {
			out.print ("| ");
			for (Comparable attr : tup) out.printf ("%15s", attr);
			out.println (" |");
		} // for
		out.print ("|-");
		for (int i = 0; i < attribute.length; i++) out.print ("---------------");
		out.println ("-|");
	} // print

	/************************************************************************************
	 * Print this table's index (Map).
	 */
	public void printIndex ()
	{
		out.println ("\n Index for " + name);
		out.println ("-------------------");
		for (Map.Entry <KeyType, Comparable []> e : index.entrySet ()) {
			out.println (e.getKey () + " -> " + Arrays.toString (e.getValue ()));
		} // for
		out.println ("-------------------");
	} // printIndex

	/************************************************************************************
	 * Load the table with the given name into memory. 
	 *
	 * @param name  the name of the table to load
	 */
	public static Table load (String name)
	{
		Table tab = null;
		try {
			ObjectInputStream ois = new ObjectInputStream (new FileInputStream (DIR + name + EXT));
			tab = (Table) ois.readObject ();
			ois.close ();
		} catch (IOException ex) {
			out.println ("load: IO Exception");
			ex.printStackTrace ();
		} catch (ClassNotFoundException ex) {
			out.println ("load: Class Not Found Exception");
			ex.printStackTrace ();
		} // try
		return tab;
	} // load

	/************************************************************************************
	 * Save this table in a file.
	 */
	public void save ()
	{
		try {
			ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream (DIR + name + EXT));
			oos.writeObject (this);
			oos.close ();
		} catch (IOException ex) {
			out.println ("save: IO Exception");
			ex.printStackTrace ();
		} // try
	} // save

	//----------------------------------------------------------------------------------
	// Private Methods
	//----------------------------------------------------------------------------------

	/************************************************************************************
	 * Determine whether the two tables (this and table2) are compatible, i.e., have
	 * the same number of attributes each with the same corresponding domain.
	 *
	 * @param table2  the rhs table
	 * @return  whether the two tables are compatible
	 */
	private boolean compatible (Table table2)
	{
		if (domain.length != table2.domain.length) {
			out.println ("compatible ERROR: table have different arity");
			return false;
		} // if
		for (int j = 0; j < domain.length; j++) {
			if (domain [j] != table2.domain [j]) {
				out.println ("compatible ERROR: tables disagree on domain " + j);
				return false;
			} // if
		} // for
		return true;
	} // compatible

	/************************************************************************************
	 * Match the column and attribute names to determine the domains.
	 *
	 * @param column  the array of column names
	 * @return  an array of column index positions
	 */
	private int [] match (String [] column)
	{
		int [] colPos = new int [column.length];

		for (int j = 0; j < column.length; j++) {
			boolean matched = false;
			for (int k = 0; k < attribute.length; k++) {
				if (column [j].equals (attribute [k])) {
					matched = true;
					colPos [j] = k;
				} // for
			} // for
			if ( ! matched) {
				out.println ("match: domain not found for " + column [j]);
			} // if
		} // for

		return colPos;
	} // match

	/************************************************************************************
	 * Extract the attributes specified by the column array from tuple t.
	 *
	 * @param t       the tuple to extract from
	 * @param column  the array of column names
	 * @return  a smaller tuple extracted from tuple t 
	 */
	private Comparable [] extract (Comparable [] t, String [] column)
	{
		Comparable [] tup = new Comparable [column.length];
		int [] colPos = match (column);
		for (int j = 0; j < column.length; j++) tup [j] = t [colPos [j]];
		return tup;
	} // extract

	/************************************************************************************
	 * A public method to be used to test typeCheck.
	 *
	 * @param t  the tuple as a list of attribute values
	 * @return  whether the tuple has the right size and values that comply
	 *          with the given domains
	 */
	public boolean testTypeCheck (Comparable [] t)
	{ 
		boolean toReturn = this.typeCheck(t);
		return toReturn;
	}
	
	/************************************************************************************
	 * Check the size of the tuple (number of elements in list) as well as the type of
	 * each value to ensure it is from the right domain. 
	 *
	 * @param t  the tuple as a list of attribute values
	 * @return  whether the tuple has the right size and values that comply
	 *          with the given domains
	 */
	private boolean typeCheck (Comparable [] t)
	{ 
		boolean isChecked = false;

		//Check to see if the length of the given tuple matches the length of the tuples in this table.
		if (t.length == this.attribute.length)
		{
			//Cycle through the types of the given tuple and the types in this table.
			for (int i = 0; i < t.length; i++)
			{
				//Check to see if the classes (types) of the columns in the tuples match.
				if (t[i].getClass().equals(this.domain[i]))
				{
					isChecked = true;
				}
				//If they do not match at any point, the else block below is executed.
				else
				{
					//Set isChecked to false since tuples do not match and set i such that the for loop is terminated leaving the value of isChecked as false.
					isChecked = false;
					i = t.length + 1;
				}
			}
		}   	

		return isChecked;
	} // typeCheck

	/************************************************************************************
	 * Find the classes in the "java.lang" package with given names.
	 *
	 * @param className  the array of class name (e.g., {"Integer", "String"})
	 * @return  an array of Java classes
	 */
	private static Class [] findClass (String [] className)
	{
		Class [] classArray = new Class [className.length];

		for (int i = 0; i < className.length; i++) {
			try {
				classArray [i] = Class.forName ("java.lang." + className [i]);
			} catch (ClassNotFoundException ex) {
				out.println ("findClass: " + ex);
			} // try
		} // for

		return classArray;
	} // findClass

	/************************************************************************************
	 * Extract the corresponding domains.
	 *
	 * @param colPos the column positions to extract.
	 * @param group  where to extract from
	 * @return  the extracted domains
	 */
	private Class [] extractDom (int [] colPos, Class [] group)
	{
		Class [] obj = new Class [colPos.length];

		for (int j = 0; j < colPos.length; j++) {
			obj [j] = group [colPos [j]];
		} // for

		return obj;
	} // extractDom

} // Table class
