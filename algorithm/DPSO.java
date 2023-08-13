package algorithm;

import java.util.Random;

import chart.ProgressBar;
import src.Evaluation;
import src.Tools;

// 粒子群算法
public class DPSO {
    private final Random random = new Random();
    private ProgressBar bar = ProgressBar.build();
    private final Evaluation evaluation = new Evaluation();
    private final int N = 10000;
    
    // 一个可行解
    // private int[] res = new int[N];
    private final int IterationTimes = 100;   // 迭代次数
    private final int Size = 40;   // 粒子群数量
    private int[][] SwarmLocations = new int[Size][N];  // 粒子群当前位置
    private int[][] SwarmSpeed = new int[Size][N];   // 粒子群运动速度
    private int[][] pBest = new int[Size][N];  // 局部最优解
    private int[] gBest = new int[N];  // 全局最优解
    private double[] fitness = new double[Size];   // 粒子群适应值
    private final double W = 0.6;  // 惯性因子
    private final double C1 = 0.2;  // 学习因子
    private final double C2 = 0.2;  // 学习因子
    private final int[] SpeedRange = {-5,5};  //粒子移动速度取值范围  [0] 最小值； [1] 最大值
    private final int[] Range = {1, 1000};

    private double P = 0.1 ;  // 权重

    private void SetP(double p){
        this.P = p;
    }

    // 随机生成粒子群
    private void RandomSwarms() {
        for(int i=0; i<Size; i++){
            for(int j=0; j<N; j++){
                SwarmLocations[i][j] = random.nextInt(Range[1]-Range[0]+1)+Range[0];   // [1,N]
                SwarmSpeed[i][j] = random.nextInt(SpeedRange[1]-SpeedRange[0]+1)-SpeedRange[1];   // [-5,5]
            }
        }
    }

    // 可行解结构转化，以便求解适应值
    private int[][] toCalculation(int[] swarm) {
        int[][] res = new int[N][N];
        for(int i=0; i<N; i++) {
            res[swarm[i]-1][i] = 1;
            // for(int j=0; j<N; j++) {
            //     if(j == swarm[i]-1) res[i][j] = 1;
            //     else res[i][j] = 0;
            // }
        }
        return res;
    }

    // 计算当前粒子群适应值
    private void calculateFitness() {
        int i = 0;
        for(int[] swarm : SwarmLocations)
            fitness[i++] = evaluation.calculateDMvalue(this.toCalculation(swarm), P);
    }

    private int bestSolution() {
        int index = -1;
        double min = 999999d;
        for(int i=0; i<Size; i++){
            if(fitness[i] < min){
                min = fitness[i];
                index = i;
            }
        }
        return index;
    }

    // 初始化局部，全局最优解
    private void initBestSolution() {
        for(int i=0; i<Size; i++){
            for(int j=0; j<N; j++)
                pBest[i][j] = SwarmLocations[i][j];
        }
        this.loadGlobalBest();
    }

    // 初始化
    private void Initialize(){
        System.out.println("开始初始化随机数据");
        this.RandomSwarms();
        this.calculateFitness();
        this.initBestSolution();
        System.out.println("初始化数据准备完毕");
    }

    // 更新粒子的速度和位置
    private void updateSwarms() {
        for(int i=0; i<Size; i++){
            for(int j=0; j<N; j++){

                SwarmSpeed[i][j] = (int)(W*SwarmSpeed[i][j] + C1*(pBest[i][j]-SwarmLocations[i][j]) + C2*(gBest[j]-SwarmLocations[i][j]));

                // 速度超出边界
                if(SwarmSpeed[i][j] < SpeedRange[0])    SwarmSpeed[i][j] = SpeedRange[0];
                if(SwarmSpeed[i][j] > SpeedRange[1])    SwarmSpeed[i][j] = SpeedRange[1];


                SwarmLocations[i][j] += SwarmSpeed[i][j];
                    
                // 超出边界
                if(SwarmLocations[i][j] < Range[0])    SwarmLocations[i][j] = Range[0];
                if(SwarmLocations[i][j] > Range[1])    SwarmLocations[i][j] = Range[1];
            }
        }
    }


    // 更新局部最优解
    private void loadLocalBest() {
        double val = 0d;
        int i = 0;
        for(int[] best : pBest) {
            val = evaluation.calculateDMvalue(this.toCalculation(best), P);
            if(val < fitness[i]){
                for(int j=0; j<N; j++)
                    pBest[i][j] = SwarmLocations[i][j];
            }
        }
    }

    // 更新全局最优解
    private void loadGlobalBest() {
        int index = this.bestSolution();
        for(int j=0; j<N; j++)
            gBest[j] = SwarmLocations[index][j];
    }

    // 粒子群搜索过程
    private void Search() {
        bar.init();
        int process = 0;
        int i = 1;
        int pre = 0;
        while( i <= IterationTimes) {
            // System.out.println("第"+i+"次迭代");
            this.updateSwarms();
            this.calculateFitness();
            this.loadLocalBest();
            this.loadGlobalBest();
            process = (int)((i*1.0) / IterationTimes * 100);
            if(process > pre){
                bar.process(process);
                pre = process;
            }
            i++;
        }
    }


    // 运行得到的近似最优解
    private void showResult() {
        System.out.println("*******************");
        System.out.println("最小值:"+evaluation.calculateDMvalue(this.toCalculation(gBest), P));
        System.out.println("最佳分组:"+evaluation.calculateK(this.toCalculation(gBest)));
    }
    

    public void run() {
        long startTime = System.currentTimeMillis();
        this.Initialize();
        this.Search();
        this.showResult();
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时："+Tools.formatDuration(endTime-startTime)); 

    }

    public void runWithP(){
        for(double p=0.1; p<1; p+=0.1){
            p = Tools.twoDecimals(p);
            this.SetP(p);
            System.out.println("P="+p);
            this.run();
        }
    }

    public static void main(String[] args) {
        DPSO dpso = new DPSO();
        dpso.run();
        // dpso.runWithBar();
    }
}
