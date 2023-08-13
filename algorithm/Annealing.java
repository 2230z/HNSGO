package algorithm;

import java.util.Random;

import chart.ProgressBar;
import src.Evaluation;
import src.NodesList;
import src.Tools;

// 模拟退火算法
public class Annealing {
    private ProgressBar bar = ProgressBar.build();
    private double p = 0.3;
    private final double T0 = 100;  // 初始温度
    private final double Te = 1;  // 温度衰减目标
    private final double Trate = 1;  // 温度衰减因子
    private final int Nums = 200;  // 同一温度查找次数
    private final double receiveP = 0.5;  //接受概率
    private final double receiveDes = 0.05;  // 接受概率衰减因子
    private double E0;
    private Evaluation evaluation = new Evaluation();
    private Random random = new Random();
    private NodesList nodesList = new NodesList();
    private final int N = nodesList.getNodesSize();  // 节点数量
    private int[][] result = new int[N][N];

    private void initResult(){
        for(int i=0; i<this.N; i++){
            this.result[0][i] = 1;
        }
        this.E0 = this.calculateTemperature(this.result);
    }

    private double calculateTemperature(int[][] result){
        return this.evaluation.calculateDMvalue(result, this.p);
    }

    private int[][] updateResultMatrix(){
        int[][] localResult = new int[N][N];
        localResult = this.result;
        int maxRange = N;
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

    public void run() {
        long startTime = System.currentTimeMillis();
        double T = T0;
        double receive = receiveP;
        int[][] local;
        double val;
        while(T > Te){
            for(int i=0; i<Nums; i++){
                local = this.updateResultMatrix();
                val = this.calculateTemperature(local);
                if(val < E0 || random.nextDouble() < receive){
                    result = local;
                    E0 = val;
                }
            }
            T -= Trate;
            receive -= receiveDes;
        }
        System.out.println();
        System.out.println("最小值"+E0);
        System.out.println("最佳分组数量"+evaluation.calculateK(result));
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时："+Tools.formatDuration(endTime-startTime)); 
    }

    public void runWithBar() {
        long startTime = System.currentTimeMillis();
        bar.init();
        this.initResult();
        int process = 0;
        int pre = 0;
        double T = T0;
        double receive = receiveP;
        int[][] local;
        double val;
        while(T >= Te){
            for(int i=0; i<Nums; i++){
                local = this.updateResultMatrix();
                val = this.calculateTemperature(local);
                if(val < E0 || random.nextDouble() < receive){
                    result = local;
                    E0 = val;
                }
            }
            process = (int)((T0-T) / (T0-Te) * 100);
            if(process > pre){
                bar.process(process);
                pre = process;
            }
            T -= Trate; 
            receive -= receiveDes;
        }
        System.out.println();
        System.out.println("最小值"+E0);
        System.out.println("最佳分组数量"+evaluation.calculateK(result));
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时："+Tools.formatDuration(endTime-startTime)); 
    }

    public static void main(String[] args) {
        Annealing annealing = new Annealing();
        annealing.runWithBar();
    }
}