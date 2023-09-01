/*
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        Thread.sleep(4000);
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println(a + b);
    }
}
*/
import java.util.*;
import java.io.*;
public class Main
{
    public static void main(String args[])throws Exception
    {
        DataInputStream z=new DataInputStream(System.in);
        int t=Integer.valueOf(z.readLine());
        while(t>=1 && t<=100)
        {
            int n;
            n=Integer.valueOf(z.readLine());
            int a[]=new int[n];
            StringTokenizer ab=new StringTokenizer(z.readLine());
            for(int i=0;i<n;i++)
            {
                a[i]=Integer.valueOf(ab.nextToken());
            }
            int diff=a[1]-a[0],d=0;
            boolean b=true;
            for(int i=0;i<n-1;i++)
            {
                d=a[i+1]-a[i];
                if(d>=0)
                {
                    if(d<diff)
                        diff=d;
                }
                else
                {
                    b=false;
                    break;
                }
            }
            if(b==true)
            {
                System.out.println(diff/2 + 1);
            }
            else
                System.out.println(0);
            t--;
        }
    }
}
