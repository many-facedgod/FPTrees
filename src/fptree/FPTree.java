package fptree;
import java.io.*;
import java.util.*;

public class FPTree {

    	public static void main(String args[])
	{
		ArrayList<ArrayList<Integer>> trn = new ArrayList<>();
		int items = 34;
		String file = "opfile.txt", file1 = "Items.txt";
		String item_names[] = new String[items];
		try{
			Scanner sc = new Scanner(new File(file));
			int i = 0, temp;
			System.out.println("Reading file...");
			while(sc.hasNextInt())
			{
				if(i%17 == 0)
					trn.add(new ArrayList<>());
				temp = sc.nextInt();
                                trn.get(i/17).add((i%17)*2 + temp);
				i++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(file1))) {
    			String line;
			int i=0;
    			while ((line = br.readLine()) != null) 
			{
       				item_names[i++] = line;
    			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}

		Scanner scan = new Scanner(System.in);
		System.out.print("Enter minsup value: ");
		double minsup = scan.nextDouble();
                Tree fp=new Tree(trn, items);
                System.out.println("Tree generated:");
                System.out.println("*******************************************\n");
		ArrayList<ArrayList<Itemset>> frequentISL = fp.frequent((int)Math.ceil(minsup*435));
		System.out.println("The frequent itemsets generated are as follows:");
		for(ArrayList<Itemset> array: frequentISL)
		{
			for(Itemset it: array)
			{
				System.out.print("[ ");
				for(Integer in: it.itemset)
					System.out.print(item_names[in]+" ");
				System.out.println("] "+ it.getCount());
			}
		}
	}
    
}
