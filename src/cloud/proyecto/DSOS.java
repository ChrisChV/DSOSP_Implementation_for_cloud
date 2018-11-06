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
    
    private Vector<Vector<Double>> ETC_matrix; //Fila: Vm //Columna: Task
    private Vector<Integer> best = null;

    public void execute(Vector<Integer> taskLengs, Vector<Integer> vmCost, int numOrg, int iterations){
        Vector<Vector<Integer>> organisms = getRandomOrganisms(numOrg, vmCost.size(), taskLengs.size()); //Fila: Vm //Columna: Task
        int iter = 0;
        best = null;
        Double bestFitness = -1;
        Double tempFitness = 0;
        int randonMumber = 0;
        generateETC_matrix(taskLengs, vmCost);
        while(iter != iterations){
            iter++;
            for(int i = 0; i < numOrg; i++){
                for(int j = 0; j < numOrg; j++){
                    tempFitness = getFitness(organisms.get(j));
                    if(bestFitness == -1 || bestFitness > tempFitness){
                        bestFitness = tempFitness;
                        best = organisms.get(i);
                    }
                }
                randonMumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp1 = getMutationBenefit(organisms.get(i), organisms.get(randomNumber), taskLengs.size());
                Vector<Integer> temp2 = getMutationBenefit(organisms.get(randonMumber), organisms.get(i), taskLengs.size());
                if(getFitness(organisms.get(i)) > getFitness(temp1)){
                    organisms.set(i, temp1);
                }
                if(getFitness(organisms.get(randomNumber)) > getFitness(temp2)){
                    organisms.set(randonMumber, temp2);
                }
                randonMumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp3 = comensalims(organisms.get(randomNumber), taskLengs.size());
                if(getFitness(organisms.get(i)) > getFitness(temp3)){
                    organisms.set(i, temp3);
                }
                randonMumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp4 = getParasito(organisms.get(i), taskLengs.size());
                if(getFitness(organisms.get(j)) > getFitness(temp4)){
                    organisms.set(j,temp4);
                }
            }
        }
    }

    private void generateETC_matrix(Vector<integer> taskLengs, Vector<Integer> vmCost){
        ETC_matrix = new Vector<Vector<Double>>(vmCost.size());
        for(int i = 0; i < ETC_matrix.size(); i++){
            ETC_matrix.set(i, new Vector<Double>(taskLengs.size()));
        }
        for(int i = 0; i < vmCost.size(); i++){
            for(int j = 0; j < taskLengs.size(); j++){
                ETC_matrix.get(i).set(j, taskLengs.get(j) / vmCost.get(i));
            }
        }
    }

    private Double getFitness(Vector<Integer> organism){
        Double res = -1;
        Double tempRes = 0;
        for(int i = 0; i < organism.size(); i++){
            tempRes = ETC_matrix.get(i).get(organism.get(i));
            if(res == -1 || res < tempRes){
                res = tempRes;
            }
        }
        return res;
    }

    private Vector<Vector<Integer>> getRandomOrganisms(int numOrg, int numVM ,int numTasks){
        Vector<Vector<Integer>> res = new Vector<Vector<Integer>>(numOrg);
        for(int i = 0; i < res.size(); i++){
            for(int j = 0; j < numTasks; j++){
                res.get(i).add(getRandomNumber(0, numVM));
            }
        }
        return res;
    }

    //Si queremos obtener el mutation benefit de un organismo X, este tiene que ir en el parametro organismI ;
    private Vector<Integer> getMutationBenefit(Vector<Integer> organismI, Vector<Integer> organismJ, int tamTask){
        Vector<Double> res = 0;
        Double randomNumber = getRandomNumber(0, 1);
        Double randomNumberF = getRandomNumber(1, 2);
        return modOrganism( ceilOrganism(sumOrganismsDou( organismI , multOrganism( minusOrganism(best,  multOrganism( divOrganism(sumOrganismsInt(organismI, organismJ), Double(2)) , randomNumber2)) , randomNumber))), tamTask);
    }


    private Vector<Integer> comensalims(Vector<Integer> a, int tamTask){
        Double randomNumber = getRandomNumber(0, 1);
        return modOrganism( ceilOrganism( multOrganism(minusOrganismInt(best, a), randomNumber)) ,m );
    }

    private Vector<Integer> getParasito(Vector<Integer> a, int tamTask){
        Double randomNUmber = getRandomNumber(0, 1);
        return modOrganism(ceilOrganism(multOrganismInt(a, randomNUmber)), m);
    }

    public static int getRandomNumber(int min, int max){
        int x = (Math.random()*((max-min)+1))+min;
        return x;
    }

    public int getRandomNumberDiff(int min, int max, int diff){
        int x = 0;
        while(true){
            x = (Math.random()*((max-min)+1))+min;
            if(x != diff) return x;
        }
    }

    public Vector<Double> sumOrganismsInt(Vector<Integer> a, Vector<Integer> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            for(int j = 0; j < b.size(); j++){
                res.add(Double(a.get(i) + b.get(i)));
            }
        }
        return res;
    }

    public Vector<Double> sumOrganismsDou(Vector<Double> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            for(int j = 0; j < b.size(); j++){
                res.add(a.get(i) + b.get(i));
            }
        }
        return res;
    }

    public Vector<Double> minusOrganismInt(Vector<Integer> a, Vector<Integer> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            for(int j = 0; j < b.size(); j++){
                res.add(Double(Math.abs(a.get(i) - b.get(i))));
            }
        }
        return res;
    } 

    public Vector<Double> minusOrganism(Vector<Double> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            for(int j = 0; j < b.size(); j++){
                res.add(Math.abs(a.get(i) - b.get(i)));
            }
        }
        return res;
    } 
    
    public Vector<Double> multOrganismInt(Vector<Integer> a, Double b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) * b);
        }
        return res;
    }

    public Vector<Double> multOrganism(Vector<Double> a, Double b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) * b);
        }
        return res;
    }

    public Vector<Double> divOrganism(Vector<Double> a, Double b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) / b);
        }
        return res;
    }

    public Vector<Integer> ceilOrganism(Vector<Double> a){
        Vector<Integer> res = new Vector<Integer>(a.size());
        for(Double d : a){
            res.add(Integer(Math.ceil(d)));
        }
        return res;
    }

    public Vector<Integer> modOrganism(Vector<Integer> a, int m){
        Vector<Integer> res = new Vector<Integer>(a.size());
        for(Integer i : a){
            res.add(i % m);
        }
        return res;
    }

    public Vector<Integer> getBest(){
        return best;
    }

}