package com.portaone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Graph {
    private Map<String, List<String>> graph;
    private Map<String, Integer> inDegree;
    
    public Graph() {
        graph = new HashMap<>();
        inDegree = new HashMap<>();
    }

    public void addAll(List<String> numbers) {
        for (String number : numbers) {
            add(number);
        }
    }

    public void add(String number) {
        // Add new vertex if absent
        graph.putIfAbsent(number, new ArrayList<>());
        inDegree.putIfAbsent(number, 0);

        // Add a new edge to all existing vertecies
        // Check for source and destination equality to avoid self-loops
        List<String> sourceVertecies = graph.keySet()
                                    .stream()
                                    .filter(n -> isConnected(n, number) && !number.equals(n))
                                    .collect(Collectors.toList());
        
        sourceVertecies.forEach(v -> {
            graph.get(v).add(number);
            inDegree.compute(number, (key, value) -> value += 1);
        });

        // Add all edges to a new vertex
        // Check for source and destination equality to avoid self-loops
        List<String> destinationVertecies = graph.keySet()
                                                .stream()
                                                .filter(n -> isConnected(number, n) && !number.equals(n))
                                                .collect(Collectors.toList());

        destinationVertecies.forEach(v -> {
            graph.get(number).add(v);
            inDegree.compute(v, (key, value) -> value += 1);
        });
    }

    public void print() {
        graph.forEach((key, value) -> {
            System.out.println(key + " => " + value);
        });
    }

    public void findLongestPath() {
        int maxPathLength = 0;
        String sourceVertex = "";

        for (String vertex : graph.keySet()) {
            int currentLength = findLongestPath(vertex);
            if (maxPathLength < currentLength) {
                maxPathLength = currentLength;
                sourceVertex = vertex;
            }
        }

        constructPath(sourceVertex, maxPathLength);
    }

    private void constructPath(String sourceVertex, Integer distance) {
        Set<String> visited = new HashSet<>();
        List<String> path = new ArrayList<>();

        constructPath(sourceVertex, visited, path, 0, distance);
    }

    private void constructPath(String sourceVertex, Set<String> visited,
                                List<String> path, Integer currentDistance, Integer expectedDistance) {
        
        path.add(path.size(), sourceVertex);
        visited.add(sourceVertex);

        if (expectedDistance == currentDistance) {
            printPath(path);
            //System.out.println(path);
        } else if (currentDistance < expectedDistance) {
            for (String neighbor : graph.get(sourceVertex)) {
                if (!visited.contains(neighbor)) {
                    constructPath(neighbor, visited, path, currentDistance + 1, expectedDistance);

                }
            }
        }

        path.remove(path.size() - 1);
        visited.remove(sourceVertex);
    }

    private boolean isConnected(String u, String v) {
        return u.substring(u.length() - 2).equals(v.substring(0, 2)) ? true : false;
    }

    public void breakCycles() {
        Queue<String> queue = new LinkedList<>();
        List<String> topologicalOrder = new ArrayList<>();

        for (String vertex : graph.keySet()) {
            if (inDegree.get(vertex) == 0) {
                queue.add(vertex);
            }
        }

        while (!queue.isEmpty()) {
            String vertex = queue.poll();
            topologicalOrder.add(vertex);

            for (String neighbor: graph.get(vertex)) {
                inDegree.compute(neighbor, (key, value) -> value -= 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (topologicalOrder.size() < graph.size()) {
            boolean isRemoved = false;

            for (String vertex : graph.keySet()) {
                if (inDegree.get(vertex) > 0) {
                    for (String neighbor : graph.get(vertex)) {
                        if (inDegree.get(neighbor) > 0) {
                            graph.get(vertex).remove(neighbor);
                            inDegree.compute(neighbor, (key,value) -> value -= 1);
                            isRemoved = true;
                            break;
                        }
                    }
                }

                if(isRemoved) {
                    break;
                }
            }

            breakCycles();
        }
    }

    private void topologicalSortUtil(String node, Set<String> visited, Stack<String> stack) {
        visited.add(node);

        for (String neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                topologicalSortUtil(neighbor, visited, stack);
            }
        }

        stack.push(node);
    }

    public int findLongestPath(String source) {
        Stack<String> topoStack = new Stack<>();
        Set<String> visited = new HashSet<>();

        for (String vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                topologicalSortUtil(vertex, visited, topoStack);
            }
        }

        Map<String, Integer> distance = new HashMap<>();
        for (String vertex : graph.keySet()) {
            distance.put(vertex, Integer.MIN_VALUE);
        }
        distance.compute(source, (key, value) -> value = 0);

        while (!topoStack.isEmpty()) {
            String current = topoStack.pop();

            if (distance.get(current) != Integer.MIN_VALUE) {
                for (String neighbor : graph.get(current)) {
                    if (distance.get(current) + 1 > distance.get(neighbor)) {
                        distance.compute(neighbor, (key, value) -> value = distance.get(current) + 1);
                    }
                }
            }
        }

        return distance.values().stream().max(Integer::compare).orElseThrow();
    }

    private void printPath(List<String> path) {
        System.out.print(path.get(0));

        for (int i = 1; i < path.size(); ++i) {
            System.out.print(path.get(i).substring(2));
        }

        System.out.println();
    }

}
