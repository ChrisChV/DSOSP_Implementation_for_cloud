package cloud.proyecto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.lang.Math;

//TODO Dar vuelta a la ETC_matrix Vm/T;

public class DSOS{
    
    private static Vector<Vector<Double>> ETC_matrix; //Fila: Vm //Columna: Task
    private static Vector<Integer> best = null;

    public static void main(String[] args){
        Vector<Integer> taskLengs = generateRandomTask(100, 10, 50);
        //Vector<Integer> vmCost = generateRandomTask(20, 10, 50);
        Vector<Integer> vmCost = new Vector<Integer>();
        for(int i = 0; i < 10; i++){
            vmCost.add(1000 * (i+1));
            vmCost.add(1000 * (i+1));
        }
        int numOrg = 3;
        int iterations = 3;
        execute(taskLengs, vmCost, numOrg, iterations);
        printBest();
    }

    public static void execute(Vector<Integer> taskLengs, Vector<Integer> vmCost, int numOrg, int iterations){
        Vector<Vector<Integer>> organisms = getRandomOrganisms(numOrg, vmCost.size(), taskLengs.size()); //Fila: Vm //Columna: Task
        int iter = 0;
        best = null;
        Double bestFitness = (double) -1;
        Double tempFitness = (double) 0;
        generateETC_matrix(taskLengs, vmCost);
        int randonNumber = 0;
        while(iter != iterations){
            iter++;
            System.out.print("Iteracion:");
            System.out.println(iter);
            for(int i = 0; i < numOrg; i++){
                for(int j = 0; j < numOrg; j++){
                    tempFitness = getFitness(organisms.get(j));
                    if(bestFitness == -1 || bestFitness > tempFitness){
                        bestFitness = tempFitness;
                        best = organisms.get(i);
                    }
                }
                randonNumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp1 = getMutationBenefit(organisms.get(i), organisms.get(randonNumber), vmCost.size());
                Vector<Integer> temp2 = getMutationBenefit(organisms.get(randonNumber), organisms.get(i), vmCost.size());
                if(getFitness(organisms.get(i)) > getFitness(temp1)){
                    organisms.set(i, temp1);
                }
                if(getFitness(organisms.get(randonNumber)) > getFitness(temp2)){
                    organisms.set(randonNumber, temp2);
                }
                randonNumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp3 = comensalims(organisms.get(randonNumber), vmCost.size());
                if(getFitness(organisms.get(i)) > getFitness(temp3)){
                    organisms.set(i, temp3);
                }
                randonNumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp4 = getParasito(organisms.get(i), vmCost.size());
                if(getFitness(organisms.get(randonNumber)) > getFitness(temp4)){
                    organisms.set(randonNumber,temp4);
                }
            }
        }
    }

    private static void generateETC_matrix(Vector<Integer> taskLengs, Vector<Integer> vmCost){
        ETC_matrix = new Vector<Vector<Double>>();
        for(int i = 0; i < vmCost.size(); i++){
            ETC_matrix.add(new Vector<Double>());
        }
        for(int i = 0; i < vmCost.size(); i++){
            for(int j = 0; j < taskLengs.size(); j++){
                ETC_matrix.get(i).add((double) taskLengs.get(j) / (double) vmCost.get(i));
            }
        }
    }

    private static Double getFitness(Vector<Integer> organism){
        Double res = (double) -1;
        Double tempRes =  (double) 0;
        for(int i = 0; i < organism.size(); i++){
            //System.out.println(organism.get(i));
            tempRes = ETC_matrix.get(organism.get(i)).get(i);
            if(res == -1 || res < tempRes){
                res = tempRes;
            }
        }
        return res;
    }

    private static Vector<Vector<Integer>> getRandomOrganisms(int numOrg, int numVM ,int numTasks){
        Vector<Vector<Integer>> res = new Vector<Vector<Integer>>();
        for(int i = 0; i < numOrg; i++){
            res.add(new Vector<Integer>());
            for(int j = 0; j < numTasks; j++){
                    res.get(i).add(getRandomNumber(0, numVM));
            }
        }
        return res;
    }

    //Si queremos obtener el mutation benefit de un organismo X, este tiene que ir en el parametro organismI ;
    private static Vector<Integer> getMutationBenefit(Vector<Integer> organismI, Vector<Integer> organismJ, int tamVM){
        Double randomNumber = getRandomDoubleNumber(0, 1);
        Double randomNumberF = getRandomDoubleNumber(1, 2);
        return modOrganism( ceilOrganism(sumOrganismsInt_( organismI , multOrganism( minusOrganismInt_(best,  multOrganism( divOrganism(sumOrganismsInt(organismI, organismJ), (double)2 ) , randomNumberF)) , randomNumber))), tamVM);
    }


    private static Vector<Integer> comensalims(Vector<Integer> a, int tamVM){
        Double randomNumber = getRandomDoubleNumber(0, 1);
        return modOrganism( ceilOrganism( multOrganism(minusOrganismInt(best, a), randomNumber)) , tamVM);
    }

    private static Vector<Integer> getParasito(Vector<Integer> a, int tamVM){
        Double randomNUmber = getRandomDoubleNumber(0, 1);
        return modOrganism(ceilOrganism(multOrganismInt(a, randomNUmber)), tamVM);
    }

    public static int getRandomNumber(int min, int max){
        max--;
        int x = (int) (Math.random()*((max-min)+1))+min;
        return x;
    }

    public static Double getRandomDoubleNumber(int min, int max){
        return Math.random()*((max-min)+1) + min;
    }

    public static int getRandomNumberDiff(int min, int max, int diff){
        max--;
        int x = 0;
        while(true){
            x = (int) (Math.random()*((max-min)+1)) + min;
            if(x != diff) return x;
        }
    }

    public static Vector<Double> sumOrganismsInt(Vector<Integer> a, Vector<Integer> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) a.get(i) + b.get(i));
        }
        return res;
    }

    public static Vector<Double> sumOrganismsInt_(Vector<Integer> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) a.get(i) + b.get(i));
        }
        return res;
    }

    public static Vector<Double> sumOrganismsDou(Vector<Double> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) + b.get(i));
        }
        return res;
    }

    public static Vector<Double> minusOrganismInt(Vector<Integer> a, Vector<Integer> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) Math.abs(a.get(i) - b.get(i)));
        }
        return res;
    } 

    public static Vector<Double> minusOrganismInt_(Vector<Integer> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) Math.abs(a.get(i) - b.get(i)));
        }
        return res;
    } 

    public static Vector<Double> minusOrganism(Vector<Double> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(Math.abs(a.get(i) - b.get(i)));
        }
        return res;
    } 
    
    public static Vector<Double> multOrganismInt(Vector<Integer> a, Double b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) * b);
        }
        return res;
    }

    public static Vector<Double> multOrganism(Vector<Double> a, Double b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) * b);
        }
        return res;
    }

    public static Vector<Double> divOrganism(Vector<Double> a, Double b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) / b);
        }
        return res;
    }

    public static Vector<Integer> ceilOrganism(Vector<Double> a){
        Vector<Integer> res = new Vector<Integer>(a.size());
        for(Double d : a){
            res.add( (int) Math.ceil(d));
        }
        return res;
    }

    public static Vector<Integer> modOrganism(Vector<Integer> a, int m){
        Vector<Integer> res = new Vector<Integer>(a.size());
        for(Integer i : a){
            res.add(i % m);
        }
        return res;
    }

    public static Vector<Integer> getBest(){
        return best;
    }

    public static Double printBest(){
        for(int i = 0; i < best.size(); i++){
            System.out.print(i);
            System.out.print(" ");
            System.out.println(best.get(i));
        }
        System.out.print("Fitness: ");
        Double fit = getFitness(best);
        System.out.println(fit);
        return fit;
    }

    public static Vector<Integer> generateRandomTask(int numTasks, int minRand, int maxRand){
        Vector<Integer> res = new Vector<Integer>();
        for(int i = 0; i < numTasks; i++){
            res.add(getRandomNumber(minRand, maxRand));
        }
        return res;
    }
}