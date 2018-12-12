package cloud.proyecto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import cloud.proyecto._DSOS;
import cloud.proyecto._DSOS2;

public class DSOS2{

    private static Vector<Vector<Integer>> workFlow;


    public static void main(String[] args){
        //for(int i = 0; i < 15; i++){
            String file = "/home/xnpio/Documentos/Xnpio/UNSACS/Cloud/Proyecto/src/cloud/proyecto/test";
            Vector<Integer> taskLengs = new Vector<Integer>();
            Vector<Integer> vmCost = new Vector<Integer>();
            for(int j = 0; j < 10; j++){
                vmCost.add(1000 * (j+1));
                vmCost.add(1000 * (j+1));
            }
            int numOrg = 50;
            int numberOfResources = vmCost.size();
            Double groupsPenalty = Double.valueOf(1);
            int iterations = 500;
            execute(taskLengs, vmCost, file, numOrg, numberOfResources, groupsPenalty, iterations);
        //}
    }


    public static void execute(Vector<Integer> taskLengs, Vector<Integer> vmCost, String file, int numOrg, int numberOfResources, double groupsPenalty, int iterations){
        _DSOS dsos = new _DSOS();
        _DSOS2 dsos2 = new _DSOS2();
        loadWorkFlow(file);
        taskLengs = generateRandomTask(workFlow.size(), 1, 10);
        double startTime = System.nanoTime();
        dsos2.execute(workFlow, workFlow.size(), numOrg, groupsPenalty, iterations);
        double endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000;

        dsos2.printBest();

        System.out.print("Time: ");
        System.out.println(duration);


        Vector<Integer> groupsVec = dsos2.getBest();
        Vector<Vector<Integer>> groups = new Vector<Vector<Integer>>();
        for(int i = 0; i < workFlow.size(); i++){
            groups.add(new Vector<Integer>());
        }
        for(int i = 0; i < groupsVec.size(); i++){
            groups.get(groupsVec.get(i)).add(i);
        }

        Vector<Integer> tempTaskLengs;

        Double totalFitness = Double.valueOf(0);
        Double totalMakespan = Double.valueOf(0);
        
        for(int i = 0; i < groups.size(); i++){
            if(groups.get(i).size() == 0) continue;
            System.out.println("---- Grupo " + Integer.toString(i) + " ----");
            tempTaskLengs = new Vector<Integer>();
            for(Integer j : groups.get(i)){
                tempTaskLengs.add(taskLengs.get(j));
            }
            dsos.execute(tempTaskLengs, vmCost, numOrg, iterations);
            totalMakespan += dsos.printBest();
            totalFitness += dsos.getFitBest();
        }
        
        /*
        System.out.print("Total Fit: ");
        System.out.println(totalFitness);
        */
        
        System.out.print("Total Makespan: ");
        System.out.println(totalMakespan);
        /*
        System.out.print("Time: ");
        System.out.println(duration);
        */
    }

    public static void loadWorkFlow(String file){
        try{
            BufferedReader br = null;
            FileReader fr = null;
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String sCurrentLine = br.readLine();
            String[] parts;
            String src;
            String dest;
            int numOfNodes = Integer.parseInt(sCurrentLine);
            Integer i_src = null;
            int i_dest = 0;
            workFlow = new Vector<Vector<Integer>>();
            for(int i = 0; i < numOfNodes; i++){
                workFlow.add(new Vector<Integer>());
            }
            while ((sCurrentLine = br.readLine()) != null) {
                parts = sCurrentLine.split(" ");
                src = parts[0];
                dest = parts[1];
                i_src = Integer.valueOf(src);
                i_dest = Integer.parseInt(dest);
                workFlow.get(i_dest).add(i_src);
            }
            br.close();
        }
        catch(FileNotFoundException e){

        }
        catch(IOException e){

        }
    }

    public static void printWorflow(){
        int actual = 0;
        for(Vector<Integer> v : workFlow){
            System.out.print(actual);
            System.out.print(" : ");
            for(Integer i : v){
                System.out.print(i);
                System.out.print(" ");
            }
            System.out.println("");
            actual += 1;
        }
    }

    public static Vector<Integer> generateRandomTask(int numTasks, int minRand, int maxRand){
        Vector<Integer> res = new Vector<Integer>();
        for(int i = 0; i < numTasks; i++){
            res.add(getRandomNumber(minRand, maxRand));
        }
        return res;
    }

    public static int getRandomNumber(int min, int max){
        max--;
        int x = (int) (Math.random()*((max-min)+1))+min;
        return x;
    }


}