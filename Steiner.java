// The programme may possibly run some more instances after 145 but our laptops crashed so we could not continue

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.*;
import java.io.*;


public class Steiner{ 
  public int nodes, edges, terminals;
  public int[][] adjMatrix;    //define the adjacency matrix
  public ArrayList<Integer> terminalNodes;  //define the arraylist for the terminal nodes
  public ArrayList<ArrayList<Integer>> paths;   //define the path used in Dijkstra, each element in the path has the path to get there in it.
  
  public static void main(String[] args) {
    try{ 
      String number = args[0];    
      Steiner g = new Steiner("instance" + number + ".gr");  
      BufferedWriter writer = new BufferedWriter(new FileWriter("instance" + number + ".sol", true));
      
      
      int[][] adjacency = new int[g.nodes][g.nodes];  
      int[][] position = new int[g.nodes][g.nodes];
      
      
      int place = 0;
      for(int index= 0; index<g.nodes; index++){
        if (g.terminalNodes.contains(index)){
          ArrayList<ArrayList<Integer>> data = g.dijkstra(index);  //use Dijkstra to find the metric complete graph
          for(ArrayList<Integer> path : data){
            int dest = path.get(path.size()-1);
            int distance = path.get(0);
            
            adjacency[index][dest] = distance;
            position[index][dest] = place;
            
            
            g.paths.add(path);
            place++;
          }
        }
      }
      
      //now we have the metric complete graph (metricGraph) and we have to find the minimum tree in this graph
      int[][] steinerTree = g.prims(adjacency,position); int value = 0;
      
      
      //  System.out.println(); System.out.println("Edges in optimal solution:");
      for (int i = 0; i < g.nodes; i++) {
        for (int j = 0; j < g.nodes; j++) {
          if (steinerTree[i][j]!=0){
            //    System.out.println((i + 1) + "\t " + (j + 1) + "\t  " + steinerTree[i][j]);
            value += steinerTree[i][j];
          }
        }
      }
      System.out.println("VALUE \t " + value);
      writer.write("VALUE \t " + value);
      writer.newLine();
      for (int i = 0; i < g.nodes; i++) {
        for (int j = 0; j < g.nodes; j++) {
          if (steinerTree[i][j]!=0){
            writer.write((i + 1) + "\t " + (j + 1));
            writer.newLine();
          }
        }
      }
      writer.close();
      
      
      /*  double endTime = System.currentTimeMillis();
       System.out.println("time: " + (endTime - startTime)); */
    }
    
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  
  //methof for Dijkstra
  public ArrayList<ArrayList<Integer>> dijkstra(int startNode){
    ArrayList<ArrayList<Integer>> data = new ArrayList<ArrayList<Integer>>();
    int n = nodes;
    int[] shortest = new int[n]; 
    
    boolean[] added = new boolean[n]; 
    
    for (int nodeIndex = 0; nodeIndex < n; nodeIndex++) { 
      shortest[nodeIndex] = Integer.MAX_VALUE; 
      added[nodeIndex] = false; 
    } 
    shortest[startNode] = 0; 
    int[] previous = new int[n]; 
    previous[startNode] = -1; 
    
    // Find shortest path for all vertices 
    for (int i = 1; i < n; i++) {
      int closestNode = -1; 
      int distance = Integer.MAX_VALUE; 
      for (int nodeIndex = 0; nodeIndex < n; nodeIndex++) { 
        if (!added[nodeIndex] && shortest[nodeIndex] < distance){ 
          closestNode = nodeIndex; 
          distance = shortest[nodeIndex]; 
        } 
      } 
      
      added[closestNode] = true; 
      
      
      for (int nodeIndex = 0; nodeIndex < n; nodeIndex++){ 
        int edgeDistance = adjMatrix[closestNode][nodeIndex]; 
        
        if (edgeDistance > 0 && ((distance + edgeDistance) < shortest[nodeIndex])){ 
          previous[nodeIndex] = closestNode; 
          shortest[nodeIndex] = distance + edgeDistance; 
        } 
      } 
    } 
    
    for (int nodeIndex:terminalNodes){
      if (nodeIndex > startNode){ 
        ArrayList<Integer> currentNode = new ArrayList<Integer>();
        currentNode.add(shortest[nodeIndex]);
        getPath(nodeIndex, previous, currentNode);
        
        data.add(currentNode);
      }
    }
    return data;
  }

 //method to find the path (backtracking
  public static void getPath(int currentNode, int[] previous, ArrayList<Integer> path) { 
    if (currentNode == -1) return;
    
    getPath(previous[currentNode], previous, path); 
    path.add(currentNode); 
  }
  
  //function that implements kruskal's algorithm.
  //kruskal works perfectly!
  

 //method for Prim's algorithm
  public int[][] prims(int[][] adjacency, int[][] position) {
    
    int[][] steinerTree = new int[nodes][nodes];
    
    for(int i=0; i<nodes; i++) {
      for  (int j=0; j<nodes; j++) {
        steinerTree[i][j] =0;
      }
    }
    
    Boolean set[] = new Boolean [nodes];
    set[terminalNodes.get(0)]=true;
    for(int i=0; i< nodes; i++) {
      if(i != terminalNodes.get(0)) {
        set[i] = false;
      }
    }
    
    int value =0;
    for(int count=0; count<(terminals-1); count++) {
      int k=0; int f=0;
      int min = Integer.MAX_VALUE;
      for(int i=0; i<nodes; i++) {
        for(int j=i+1; j<nodes; j++) {
          if(set[i]==true) {
            if(set[j]==false) {
              if(adjacency[i][j]!=0) {
                if(adjacency[i][j]<min) {
                  min = adjacency[i][j]; 
                  k=i; f=j;                  
                }
              }
            }
          }
        }
      }
      
      int first = k ; int second = f; 
      ArrayList<Integer> path = paths.get(position[first][second]);
      for(int i=1; i<path.size()-1; i++){
        steinerTree[path.get(i)][path.get(i+1)] = adjMatrix[path.get(i)][path.get(i+1)] ;
      }
      set[k]=true; set[f]=true;
      value = value + min;
    }
 //   System.out.println("MST value = " + value); //this is the value of MST not the value of steiner!
    return steinerTree;
  }
  
  
  //method to read in the Steiner tree
  public Steiner(String filename) throws java.io.FileNotFoundException{
    
    File file = new File(filename);
    Scanner input = new Scanner (file);
    
    input.next(); input.next(); input.next(); 
    this.nodes = input.nextInt();
    input.next(); //text that is unnecesarry
    this.edges = input.nextInt();
    
    this.adjMatrix= new int[nodes][nodes];
    for(int i=0; i<nodes; i++) {
      for(int j=0; j<nodes; j++) {
        adjMatrix[i][j] = 0;
      }
    }
    
    for(int i =0; i<edges; i++){
      String e = input.next();
      int first = input.nextInt()-1; 
      int second = input.nextInt()-1;
      int value = input.nextInt();
      
      this.adjMatrix[first][second] = value;
      this.adjMatrix[second][first] = value;
    }
    
    
    input.next(); input.next(); input.next(); input.next();
    this.terminals = input.nextInt();
    this.terminalNodes = new ArrayList<Integer>();
    for(int i=0; i<terminals; i++){
      String t = input.next();
      this.terminalNodes.add(input.nextInt()-1); 
    }
    input.close(); 
    paths = new ArrayList<ArrayList<Integer>>();
  }
}