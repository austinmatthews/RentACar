import static java.lang.System.out;
import java.util.*;

public class LinHashInsertTest{

	public static void main(String[] args){

		LinHashMap<Integer, Integer> map = new LinHashMap<>(Integer.class, Integer.class, 4);
		
		Random r = new Random();

		// RANDOM NUMBERS

		for(int i = 0; i < 100; i++){
			int rand = r.nextInt(100);
			map.put(rand, rand);
			//map.print();
		}

		out.println("RANDOM NUMBERS");
		map.print();
		map.entrySet();
		out.println();

		out.println(map.get(r.nextInt(100)));
		out.println(map.get(r.nextInt(100)));
		out.println(map.get(r.nextInt(100)));
		out.println(map.get(47));

		// SEQUENTIAL NUMBERS

		LinHashMap<Integer, Integer> map2 = new LinHashMap<>(Integer.class, Integer.class, 4);

		for(int j = 0; j < 100; j++){
			map2.put(j, j*j);
		}

		out.println("SEQUENTIAL NUMBERS");
		out.println();

		map2.print();
		map2.entrySet();
		out.println();

		out.println(map2.get(r.nextInt(100)));
		out.println(map2.get(r.nextInt(100)));
		out.println(map2.get(r.nextInt(100)));


	}

}