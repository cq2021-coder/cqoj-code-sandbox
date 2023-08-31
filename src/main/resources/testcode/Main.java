import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(2000);
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println("结果为：" + (a + b));
    }
}
