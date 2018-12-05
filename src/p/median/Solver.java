/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p.median;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ján
 */
public class Solver {
    //two dimensional matrix of the shortest distances between vertexes
    private double[][] distanceMatrix;
    // map containing all the vertex names
    private HashMap<Integer, String> vertexNames;
    // number of medians in population elements
    private int numberOfMedians;
    // parametrically set of population size
    private int sizeOfPopulation;
    // array containing all population elements
    private int[][] population;
    // array containing solution for all population elements
    private double[] solutionArray;
    // array containg indexes of sorted solutionArray from the highest to the smallest
    private int[] sortedIndexes;
    
    public Solver(int sizeOfPopulation,int numberOfMedians) {
        this.sizeOfPopulation = sizeOfPopulation;
        this.numberOfMedians = numberOfMedians;
        population = new int[sizeOfPopulation][];
        solutionArray = new double[this.sizeOfPopulation];
        sortedIndexes = new int[this.sizeOfPopulation];
        vertexNames = new HashMap<>();
        loadFile();
    }
    
    private void loadFile() {
        try {
            File f = new File("maticaVzdialenosti.txt");
            Scanner sc = new Scanner(f);
            int row = sc.nextInt();
            int column = sc.nextInt();
            distanceMatrix = new double[row][column];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    distanceMatrix[i][j] = sc.nextDouble();
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File f1 = new File("nazvyCentier.txt");
        try {
            Scanner s = new Scanner(f1);
            String p = s.nextLine();
            int count=  Integer.parseInt(p);
            for (int i = 0; i < count; i++) {
                String b = s.nextLine();
                String array[] = b.split(". ");
                vertexNames.put(Integer.parseInt(array[0]) - 1, array[1]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    // method which create element of population
    public int[] createElementOfPopulation() {
        int maxLength = distanceMatrix.length-1;
        int[] tempArray = new int[distanceMatrix.length];
        int[] element = new int[distanceMatrix.length];
        for (int i = 0; i < tempArray.length; i++) {
            tempArray[i] = i;
        }
        Random rnd = new Random();
        for (int i = 0; i < this.numberOfMedians; i++) {
            int index = rnd.nextInt(maxLength);
            element[tempArray[index]] = 1;
            tempArray[index] = tempArray[maxLength];
            maxLength--;
            
        }
        return element;
    }
    // creating whole population,number of elements depends of parameter
    public void createPopulation() {
        int sizeOfActualPopulation = 0;
        while(sizeOfActualPopulation < sizeOfPopulation) {
            int[] potentionalElement = createElementOfPopulation();
            double solution = calculateSolution(potentionalElement);
            if(!checkPopulation(potentionalElement,solution,sizeOfActualPopulation)) {
                solutionArray[sizeOfActualPopulation] = calculateSolution(potentionalElement);
                population[sizeOfActualPopulation] = potentionalElement;
                sizeOfActualPopulation++;
            }
            
        }
    }
    //checks if population already contains same element
    // false if doesnt
    public boolean checkPopulation(int[] element,double solution,int actualPopulationSize) {
        //if there isnt any population element
        if(actualPopulationSize ==0) {
            return false;
        }
        //if exists same solution
        boolean solutionFound = false;
        for (int i = 0; i < solutionArray.length; i++) {
            if(solutionArray[i] == solution) {
                solutionFound = true;
                break;
            }
        }
        if(!solutionFound) {
            return false;
        }
        //of its exactly the same element
        boolean elementFound = false;
        for (int i = 0; i < actualPopulationSize; i++) {
            if(compareElements(element, population[i])) {
                elementFound = true;
                break;
            }
        }
        if(elementFound) {
            return true;
        }
        return false;
    }
    public boolean compareElements(int[] element1,int[] element2) {
        for (int i = 0; i < element1.length; i++) {
            if(element1[i] != element2[i]) {
                return false;
            }
        }
        return true;
    }
    

    //finds the closest distance of non median to any median of element
    private double calculateBestSolutionForNonMedian(int index,int[] element) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < element.length; i++) {
            if(index != i) {
            min = Math.min(min, distanceMatrix[i][index]);
            }
        }
        return min;
    }
    // calculate whole fitness value
    public double calculateSolution(int[] elementOfPopulation) {
        double currentSolution = 0;
        for (int i = 0; i < distanceMatrix.length; i++) {
            if (elementOfPopulation[i] == 1) {
                continue;
            }
            currentSolution += calculateBestSolutionForNonMedian(i,elementOfPopulation);
        }
        return currentSolution;
    }
    // sorts to sortedIndexes fitness values of all population elements
    public void sortBySolutions() {
      double[] docA = solutionArray.clone();
      int indexx = 0;
      while(true) {
      double naj = -1;
      int index = -1;
          for (int i = 0; i < docA.length; i++) {
              if(docA[i] > naj) {
                  naj = docA[i];
                  index = i;
              }
          }
          if(naj == -1) {
              break;
          }
          sortedIndexes[indexx] = index;
          docA[index] = -2;
          indexx++;
      }
       
    }
    public int[] changeMedians(int indexRemove,int indexAdd,int[] element) {
        element[indexRemove] = 0;
        element[indexAdd] = 1;
        return element;
    }
    
    public String printElement(int[] element) {
        String reprez = "";
        for (int i = 0; i < element.length; i++) {
            if(element[i] ==1) {
                reprez += String.valueOf(i) + " ";
            }
        }
        return reprez;
    }
    
    public void printPopulation() {
        createPopulation();
        for (int i = 0; i < sizeOfPopulation; i++) {
            System.out.println("Clen #" + i + " < " + printElement(population[i]) + ">" + "   " + solutionArray[i]);
        }
        sortBySolutions();
        for (int i = 0; i < sortedIndexes.length; i++) {
            System.out.println(sortedIndexes[i]+ " ");
        }
    }
    
  
    
}
