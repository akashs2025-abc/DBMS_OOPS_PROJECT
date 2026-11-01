package main;

public class Main {

    public static void main(String[] args) {
        System.err.println("Console mode is disabled because it uses unscoped DAO operations. "
                + "Use the authenticated web application for the shared multi-user database.");
    }
}
