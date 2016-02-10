import java.util.*;

public class Testtree{

	public static void main(String[] args){

		BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
		Random r = new Random();

		for(int i = 0; i < 20; i++){
			int num = r.nextInt(100);
			System.out.println(num);
			bpt.put(num, num);
			bpt.print(bpt.getRoot(), 0);
		}

		System.out.println(bpt.get(53));
		System.out.println(bpt.get(14));
		System.out.println(bpt.get(65));

		/* for(int i = 0; i < 30; i++){
			if(i % 2 == 0) bpt.put(i, i);
			else{
				int num = -i;
				bpt.put(num, num);
			}
			bpt.print(bpt.getRoot(), 0);
		}

		System.out.println(bpt.get(-11));
		System.out.println(bpt.get(-17));
		System.out.println(bpt.get(-23)); */

	}

}