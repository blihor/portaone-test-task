package com.portaone;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> numbers = new ArrayList<>();
        Graph graph = new Graph();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("test_task/src/main/resources/numbers.in"));
            String line;

            while ((line = reader.readLine()) != null) {
                numbers.add(line);
            }
        } catch (IOException e) {
        }

        graph.addAll(numbers);
        graph.breakCycles();
        graph.findLongestPath();
    }
}