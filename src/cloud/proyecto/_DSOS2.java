package cloud.proyecto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.lang.Math;

//TODO Dar vuelta a la ETC_matrix Vm/T;

public class _DSOS2{
    
    private Vector<Vector<Double>> ETC_matrix; //Fila: Vm //Columna: Task
    private Vector<Integer> best = null;
    private Double groupsPenalty;
    private Vector<Vector<Integer>> workFlow;

    /*
    public static void main(String[] args){
        Vector<Integer> taskLengs = generateRandomTask(100, 10, 50);
        Vector<Integer> vmCost = generateRandomTask(20, 10, 50);
        int numOrg = 3;
        int iterations = 3;
        execute(taskLengs, vmCost, numOrg, iterations);
        printBest();
    }
    */

    public _DSOS2(){
        
    }

    public void execute(Vector<Vector<Integer>> workFlow, int numTasks, int numOrg, double groupsPenalty, int iterations){
        Vector<Vector<Integer>> organisms = getRandomOrganisms(numOrg, numTasks); //Fila: Vm //Columna: Task
        this.groupsPenalty = groupsPenalty;
        this.workFlow = workFlow;
        int iter = 0;
        best = null;
        Double bestFitness = (double) -1;
        Double tempFitness = (double) 0;
        int randonNumber = 0;
        Double i_fitness = Double.valueOf(0);
        Double temp_fitness;
        while(iter != iterations){
            iter++;
            //System.out.print("Iteracion:");
            //System.out.println(iter);
            for(int i = 0; i < numOrg; i++){
                for(int j = 0; j < numOrg; j++){
                    tempFitness = getFitness(organisms.get(j));
                    if(bestFitness == -1 || bestFitness > tempFitness){
                        bestFitness = tempFitness;
                        best = organisms.get(j);
                    }
                }
                randonNumber = getRandomNumberDiff(0, numOrg, i);
                i_fitness = getFitness(organisms.get(i));
                Vector<Integer> temp1 = getMutationBenefit(organisms.get(i), organisms.get(randonNumber), numTasks);
                Vector<Integer> temp2 = getMutationBenefit(organisms.get(randonNumber), organisms.get(i), numTasks);
                temp_fitness = getFitness(temp1);
                if(verifyGroups(temp1) && i_fitness > temp_fitness){
                    if(verifySameGroup(temp1) == false){
                        temp1 = generateRandomTask(numTasks, 0, numTasks);
                        temp_fitness = getFitness(temp1);
                    }
                    organisms.set(i, temp1);
                    i_fitness = temp_fitness;
                }
                if(verifyGroups(temp2) && getFitness(organisms.get(randonNumber)) > getFitness(temp2)){
                    if(verifySameGroup(temp2) == false){
                        temp2 = generateRandomTask(numTasks, 0, numTasks);
                    }
                    organisms.set(randonNumber, temp2);
                }
                randonNumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp3 = comensalims(organisms.get(randonNumber), numTasks);
                temp_fitness = getFitness(temp3);
                if(verifyGroups(temp3) && i_fitness > temp_fitness){
                    if(verifySameGroup(temp3) == false){
                        temp3 = generateRandomTask(numTasks, 0, numTasks);
                        temp_fitness = getFitness(temp3);
                    }
                    organisms.set(i, temp3);
                    i_fitness = temp_fitness;
                }
                randonNumber = getRandomNumberDiff(0, numOrg, i);
                Vector<Integer> temp4 = getParasito(organisms.get(i), numTasks);
                if(verifyGroups(temp4) && getFitness(organisms.get(randonNumber)) > getFitness(temp4)){
                    if(verifySameGroup(temp4) == false){
                        temp4 = generateRandomTask(numTasks, 0, numTasks);
                        temp_fitness = getFitness(temp4);
                    }
                    organisms.set(randonNumber,temp4);
                }
            }
        }
    }

    private Boolean verifySameGroup(Vector<Integer> groups){
        for(Integer i : groups){
            if(i != 0) return true;
        }
        return false;
    }

    private Double getFitness(Vector<Integer> groups){
        Double res = Double.valueOf(1);
        Double errors = Double.valueOf(0);
        Vector<Vector<Integer>> g_groups;
        Vector<Boolean> flags; 
        g_groups = new Vector<Vector<Integer>>();
        flags = new Vector<Boolean>();
        for(int i = 0; i < groups.size(); i++){
            g_groups.add(new Vector<Integer>());
        }
        for(int i = 0; i < groups.size(); i++){
            g_groups.get(groups.get(i)).add(i);
        }
        for(int i = 0; i < groups.size(); i++){
            flags.add(false);
        }

        Vector<Integer> tempGroup;
        int numGroups = 0;
        for(int i = 0; i < g_groups.size(); i++){
            tempGroup = g_groups.get(i);
            if(tempGroup.size() != 0) numGroups += 1;
            for(Integer task : tempGroup){
                for(Integer depends : workFlow.get(task)){
                    if(flags.get(depends) == false){
                        errors += 1;
                        res += groupsPenalty * errors;
                    } 
                }
            }
            for(Integer task : tempGroup){
                flags.set(task, true);
            }
        }
        return res + (5 * numGroups);
    }

    private Boolean verifyGroups(Vector<Integer> groups){
        Vector<Boolean> flags;
        flags = new Vector<Boolean>();
        for(int i = 0; i < groups.size(); i++){
            flags.add(false);
        }
        for(Integer i : groups){
            flags.set(i, true);
        }
        Boolean flag = false;
        for(Boolean f : flags){
            if(flag == false){
                if(f == true) continue;
                else flag = true;
            }
            else{
                if(f == true) return false;
                else continue;
            }
        }
        return true;
    }

    private Vector<Integer> getValidGroup(Vector<Integer> group, Vector<Integer> flags){
        Boolean state = false;
        int ant = 0;
        for(int i = 0; i < flags.size(); i++){
            if(state == false){
                if(flags.get(i) == 0){
                    state = true;
                    ant = i;
                } 
            }
            else{
                if(flags.get(i) != 0){
                    group.set(i, ant);
                    flags.set(ant, 1);
                    flags.set(i, flags.get(i) - 1);
                    return getValidGroup(group, flags);
                }
            }
        }
        return group;
    }

    private Vector<Vector<Integer>> getRandomOrganisms(int numOrg, int numTasks){
        Vector<Vector<Integer>> res = new Vector<Vector<Integer>>();
        Vector<Integer> flags = new Vector<Integer>();
        Integer randomNumber = 0;
        for(int i = 0; i < numOrg; i++){
            flags = new Vector<Integer>();
            res.add(new Vector<Integer>());
            for(int j = 0; j < numTasks; j++){
                flags.add(0);
            }
            
            for(int j = 0; j < numTasks; j++){
                randomNumber = getRandomNumber(0, numTasks);
                res.get(i).add(randomNumber);
                flags.set(randomNumber, flags.get(randomNumber) + 1);
            }
            //if(verifyGroups(res.get(i)) == false){
            //    res.set(i, getValidGroup(res.get(i),flags));
            //}
        }
        return res;
    }

    private Vector<Integer> getRandomOrg(int numTasks){
        Vector<Integer> res = new Vector<Integer>();
        for(int i = 0; i < numTasks; i++){
            res.add(getRandomNumber(0, numTasks));
        }
        return res;
    }

    //Si queremos obtener el mutation benefit de un organismo X, este tiene que ir en el parametro organismI ;
    private Vector<Integer> getMutationBenefit(Vector<Integer> organismI, Vector<Integer> organismJ, int tamVM){
        Double randomNumber = getRandomDoubleNumber(0, 1);
        Double randomNumberF = getRandomDoubleNumber(1, 2);
        return modOrganism( ceilOrganism(sumOrganismsInt_( organismI , multOrganism( minusOrganismInt_(best,  multOrganism( divOrganism(sumOrganismsInt(organismI, organismJ), (double)2 ) , randomNumberF)) , randomNumber))), tamVM);
    }


    private Vector<Integer> comensalims(Vector<Integer> a, int tamVM){
        Double randomNumber = getRandomDoubleNumber(0, 1);
        return modOrganism( ceilOrganism( multOrganism(minusOrganismInt(best, a), randomNumber)) , tamVM);
    }

    private Vector<Integer> getParasito(Vector<Integer> a, int tamVM){
        Double randomNUmber = getRandomDoubleNumber(0, 1);
        return modOrganism(ceilOrganism(multOrganismInt(a, randomNUmber)), tamVM);
    }

    public int getRandomNumber(int min, int max){
        max--;
        int x = (int) (Math.random()*((max-min)+1))+min;
        return x;
    }

    public Double getRandomDoubleNumber(int min, int max){
        return Math.random()*((max-min)+1) + min;
    }

    public int getRandomNumberDiff(int min, int max, int diff){
        max--;
        int x = 0;
        while(true){
            x = (int) (Math.random()*((max-min)+1)) + min;
            if(x != diff) return x;
        }
    }

    public Vector<Double> sumOrganismsInt(Vector<Integer> a, Vector<Integer> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) a.get(i) + b.get(i));
        }
        return res;
    }

    public Vector<Double> sumOrganismsInt_(Vector<Integer> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) a.get(i) + b.get(i));
        }
        return res;
    }

    public Vector<Double> sumOrganismsDou(Vector<Double> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(a.get(i) + b.get(i));
        }
        return res;
    }

    public Vector<Double> minusOrganismInt(Vector<Integer> a, Vector<Integer> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) Math.abs(a.get(i) - b.get(i)));
        }
        return res;
    } 

    public Vector<Double> minusOrganismInt_(Vector<Integer> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add( (double) Math.abs(a.get(i) - b.get(i)));
        }
        return res;
    } 

    public Vector<Double> minusOrganism(Vector<Double> a, Vector<Double> b){
        Vector<Double> res = new Vector<Double>(a.size());
        for(int i = 0; i < a.size(); i++){
            res.add(Math.abs(a.get(i) - b.get(i)));
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
            res.add( (int) Math.ceil(d));
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

    public void printBest(){
        for(int i = 0; i < best.size(); i++){
            System.out.print(i);
            System.out.print(" ");
            System.out.println(best.get(i));
        }
        System.out.print("Fitness: ");
        System.out.println(getFitness(best));
    }

    public Vector<Integer> generateRandomTask(int numTasks, int minRand, int maxRand){
        Vector<Integer> res = new Vector<Integer>();
        for(int i = 0; i < numTasks; i++){
            res.add(getRandomNumber(minRand, maxRand));
        }
        return res;
    
    }

}