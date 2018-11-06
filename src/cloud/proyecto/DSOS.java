package cloud.proyecto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class DSOS{
    
    private Vector<Vector<Double>> ETC_matrix; //Fila: Vm //Columna: Task

    public void execute(Vector<Integer> taskLengs, Vector<Integer> vmCost, int numOrg, int iterations){
        Vector<Vector<Integer>> organisms; //Fila: Vm //Columna: Task
        int iter = 0;
        Vector<Integer> best = null;
        generateETC_matrix(taskLengs, vmCost);
        while(iter != iterations){
            iter++;
               
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
}