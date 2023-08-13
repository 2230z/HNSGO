package algorithm;

import java.util.Random;

import src.Evaluation;
import src.NodesList;

// 爬山算法，寻找次优解
public class Climbing {

    private double p = 0.1;
    private int maxCount = 1000000;  //最大迭代次数 
    private NodesList nodesList = new NodesList();
    private Random random = new Random();
    private int N = nodesList.getNodesSize();
    private int[][] result = new int[N][N];   // 解
    private Evaluation evaluation = new Evaluation();

    public Climbing() {
        this.initResult();
    }

    // 生成初始解
    private void initResult(){
        for(int i=0; i<this.N; i++){
            this.result[0][i] = 1;
        }
    }

    // 手动深拷贝
    private void copyResult(int[][] preResult, int[][] result) {
        for(int i=0; i<result.length; i++){
            for(int j=0; j<result[i].length; j++)
                result[i][j] = preResult[i][j];
        }
    }

    private int[][] createRandomResult(){
        
        int[][] localResult = new int[this.N][this.N];
        localResult = this.result;
        localResult = this.updateResultMatrix();
        // if(!Tools.judgeResult(localResult)) throw new Exception();
        return localResult;
    }

    private int[][] updateResultMatrix(){
        int[][] localResult = new int[this.N][this.N];
        this.copyResult(this.result, localResult);
        int maxRange = this.N;
        int y;
        int x;
        do{
            y = this.random.nextInt(maxRange);
            x = this.random.nextInt(maxRange);
        }while(localResult[x][y] == 1);
        for(int i=0; i<maxRange; i++)   localResult[i][y] = 0;
        localResult[x][y] = 1;
        return localResult;
    }

    private void run() {
        int label = 1;
        int i=0;
        double min = this.evaluation.calculateDMvalue(result, this.p);
        double cur;
        while(i<this.maxCount){
            int[][] localResult = this.createRandomResult();
            cur = this.evaluation.calculateDMvalue(localResult, this.p);
            if(cur < min){
                label = this.evaluation.calculateK(localResult);
                min = cur;
                this.copyResult(localResult, this.result);
                System.out.println(label+"        "+cur);
            }
            i++;
        }
        System.out.println("最后结果为："+label+"组,最小值为"+min);
    }

    public static void main(String[] args) {
        Climbing climbing = new Climbing();
        climbing.run();
    }

}
