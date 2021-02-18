package news.analytics.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Java8Concepts {
    private static ArrayList<Integer> al = new ArrayList<>();
    static {
        al.add(5);
        al.add(5);
        al.add(7);
        al.add(7);
        al.add(3);
        al.add(4);
        al.add(1);
        al.add(8);
        al.add(9);
        al.add(10);
    }

    public static void main(String[] args) {
//        testLambda();
        testStream();
    }

    private static void testStream() {
        List<Integer> collected = al.stream().map(x -> 2*x).collect(Collectors.toList());
        System.out.println(collected);

        collected = al.stream().map(x -> 2*x).sorted().collect(Collectors.toList());
        System.out.println(collected);

        collected = al.stream().map(x -> 3*x).distinct().collect(Collectors.toList());
        System.out.println(collected);

        collected = al.stream().map(x -> 4*x).filter(x -> x%3 == 0).collect(Collectors.toList());
        System.out.println(collected);
    }

    private static void testLambda() {
        FunctionalInterface functionalInterface = (int x, int y) -> {
            System.out.println(x + y);
            return x + y;
        };

        functionalInterface.add(10, 20);


        Collections.sort(al,
                (a,b) -> {
            if(a<b) return 1;
            else if(b<a) return -1;
            else return 0;
        });
        System.out.println(al);
    }
}
interface FunctionalInterface {
    public int add (int x, int y);
}
