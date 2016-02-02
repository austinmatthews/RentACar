import static java.lang.System.out;
import java.util.*;

public class LinHashInsertTest{

	public static void main(String[] args){

		LinHashMap<Integer, Integer> map = new LinHashMap<>(Integer.class, Integer.class);
		
		Random r = new Random();

		int fours = 0;
		for(int i = 0; i < 20; i++){
			map.put(fours, fours*fours);
			fours+=4;
		}


		//map.print();
		map.entrySet();

		LinHashMap<Integer, Integer> map2 = new LinHashMap<>(Integer.class, Integer.class);

		//for(int j = 0; j < 50; j++) map2.put(j, j*j);
		//map2.print();

	}

}