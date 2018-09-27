package news.analytics.test;

public class FuzzBuzz {
    public static void main(String[] args) {
        for(int i = 1; i <= 100; i++) {
            System.out.println();

            if(i%3 == 0 || i%5 == 0) {
                if(i%3 == 0) {
                    System.out.print("Fizz");
                }
                if(i%5 == 0) {
                    System.out.print("Buzz");
                }
                continue;
            }
            System.out.print(i);
        }
    }
}
