/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptree;

import java.util.List;

/**
 *
 * @author Tanmaya
 */
public class FPTree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Tree x=new Tree(5);
        int a[]={0,1};
        int b[]={1,2};
        int c[]={0,2};
        int d[]={0,1};
        int e[]={0,1};
        int f[]={2};
        int g[]={2};
        int h[]={1,2};
        int q[]={0,2};
        int r[]={0,1};
        int s[]={0,1,2,3,4};
        int t[]={3};
        int u[]={2,4};
        int v[]={1,3,4};
        x.add(a);
        x.add(b);
        x.add(c);
        x.add(d);
        x.add(e);
        x.add(f);
        x.add(g);
        x.add(h);
        x.add(q);
        x.add(r);
        x.add(s);
        x.add(t);
        x.add(u);
        x.add(v);
        List<int[]> J=x.frequent(3);
        for (int fq[]: J)
        {
            for(int k: fq)
            {
                System.out.print(k+",");
            }
            System.out.println();
        }
    }
    
}
