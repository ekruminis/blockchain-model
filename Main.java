import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        Game g = new Game();

        System.out.print("mempool size = ");
        int n = scan.nextInt();

        System.out.print("how many rounds = ");
        int r = scan.nextInt();

        g.play(r, n);

    }
}