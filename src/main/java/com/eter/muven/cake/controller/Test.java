package com.eter.muven.cake.controller;

import java.util.Scanner;

public class Test {
    public static void main(String[] args){
        System.out.println("test");
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("\n");

        System.out.print("input integer number:");
        int i = scan.nextInt();
        System.out.print("input double number:");
        double d = scan.nextDouble();
        System.out.print("input string:");
        String s = scan.next();

        /*while (scan.hasNext()){
            String x = scan.next();
            s+= ' '+x;
        }*/

        System.out.println("String: "+s);
        System.out.println("Double: "+d);
        System.out.println("int: "+i);

    }
}
