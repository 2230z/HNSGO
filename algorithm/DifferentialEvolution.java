package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chart.ProgressBar;
import src.Evaluation;
import src.Tools;

/*
 * 差分进化算法
 */
public class DifferentialEvolution {
    private final Evaluation evaluation = new Evaluation();
    private final Random random = new Random();
    private final int iterationTimes = 100;
    private final int N = 10000;    // 解码后数组规模
    private int initLength = 40; // 初始种群染色体数量
    private List<List<Integer>> population = new ArrayList<>();   // 种群<染色体>
    private List<Double> fitness = new ArrayList<>();   // 种群适应值
    private int BestIndex = -1;
    private int Choice = 1;
    private double RATE = 0.1;  // 变异率
    private final double F = 0.5;
    private double P = 0.3;

    private void setP(double P) {
        this.P = P;
    }

    private void setChoice(int choice){
        this.Choice = choice;
    }

    private void setInitLength(int length) {
        this.initLength = length;
    }

    // 随机产生初始种群
    private void initPopulation() {
        for(int i=0; i<initLength; i++)
            population.add(this.randomInitResult());
    }

    // 产生随机初始解
    private List<Integer> randomInitResult() {
        List<Integer> list = new ArrayList<>();
        for(int i=0; i<N; i++)  list.add(random.nextInt(N)+1);
        return list;
    }

    // 解码，供求解目标函数值
    private int[][] ArrayToDouble(List<Integer> res) {
        int[][] result = new int[N][N];
        for(int i=0; i<N; i++)  result[res.get(i)-1][i] = 1;
        return result;
    }

    // 获取种群染色体数量
    private int getPopulationLength(){
        return this.population.size();
    }

    // 计算染色体适应值
    private double calculateAdaptionValue(List<Integer> list) {
        return evaluation.calculateDMvalue(this.ArrayToDouble(list), P);
    }

    // 计算整个种群中所有染色体的适应值
    private void calculatePopulationValue() {
        fitness.clear();
        for(int i=0; i<this.getPopulationLength(); i++)
            fitness.add(this.calculateAdaptionValue(population.get(i)));
    }

    // 随机选择参与选择
    private int[] randomChoose(int n){
        int[] res = new int[n];
        int index = -1;
        boolean tag = false;
        for(int i=0; i<n;){
            tag = false;
            index = random.nextInt(N);
            for(int j=0; j<i; j++){
                if(index == res[j]){
                    tag = true;
                    break;
                }
            }
            if(!tag) i++;
        }
        return res;
    }

    /*
     * 产生变异体
     */
    // 方式一
    private List<Integer> createMidSolution1(){
        int[] parents = this.randomChoose(3);
        List<Integer> mid = new ArrayList<>();
        int val = -1;
        for(int i=0; i<N; i++){
            val = population.get(parents[0]).get(i) + (int)(F * (population.get(parents[1]).get(i)-population.get(parents[2]).get(i)));
            if(val > N || val < 1) val = random.nextInt(N)+1;
            mid.add(val);
        }
        return mid;
    }

    // 方式二
    private List<Integer> createMidSolution2(){
        int[] parents = this.randomChoose(5);
        List<Integer> mid = new ArrayList<>();
        int val = -1;
        for(int i=0; i<N; i++){
            val = population.get(parents[0]).get(i) + (int)(F * (population.get(parents[1]).get(i)-population.get(parents[2]).get(i))) + (int)(F * (population.get(parents[3]).get(i)-population.get(parents[4]).get(i)));
            if(val > N || val < 1) val = random.nextInt(N)+1;
            mid.add(val);
        }
        return mid;
    }

    // 方式三
    private List<Integer> createMidSolution3(){
        int[] parents = this.randomChoose(2);
        List<Integer> mid = new ArrayList<>();
        int val = -1;
        for(int i=0; i<N; i++){
            val = population.get(BestIndex).get(i) + (int)(F * (population.get(parents[0]).get(i)-population.get(parents[1]).get(i)));
            if(val > N || val < 1) val = random.nextInt(N)+1;
            mid.add(val);
        }
        return mid;
    }

    // 方式四
    private List<Integer> createMidSolution4(){
        int[] parents = this.randomChoose(4);
        List<Integer> mid = new ArrayList<>();
        int val = -1;
        for(int i=0; i<N; i++){
            val = population.get(BestIndex).get(i) + (int)(F * (population.get(parents[0]).get(i)-population.get(parents[1]).get(i))) + (int)(F * (population.get(parents[2]).get(i)-population.get(parents[3]).get(i)));
            if(val > N || val < 1) val = random.nextInt(N)+1;
            mid.add(val);
        }
        return mid;
    }

    // 方式五
    // O 目标基因
    private List<Integer> createMidSolution5(List<Integer> O){
        int[] parents = this.randomChoose(2);
        List<Integer> mid = new ArrayList<>();
        int val = -1;
        for(int i=0; i<N; i++){
            val = O.get(i) + (int)(F * (population.get(BestIndex).get(i)-O.get(i))) + (int)(F * (population.get(parents[0]).get(i)-population.get(parents[1]).get(i)));
            if(val > N || val < 1) val = random.nextInt(N)+1;
            mid.add(val);
        }
        return mid;
    }



    // 选择基因进化并更新
    //index 为L在种群中的索引， L 为原染色体， R 为变异染色体
    private void selectGenetic(int index, List<Integer> L, List<Integer> R){
        List<Integer> res = new ArrayList<>();
        for(int i=0; i<N; i++){
            if(random.nextDouble() < RATE || random.nextInt(N)==i) res.add(R.get(i));
            else   res.add(L.get(i));
        }
        double val = evaluation.calculateDMvalue(this.ArrayToDouble(res), P);
        if( val < fitness.get(index)){
            if(val < fitness.get(BestIndex))    BestIndex = index;
            fitness.set(index,val);
            population.set(index,res);
        }
    }


    // 一次迭代过程
    private void OnceIterator(int choice){
        switch(choice){
            case 1:
                for(int i=0; i<initLength; i++){
                    List<Integer> mid = this.createMidSolution1();
                    this.selectGenetic(i, population.get(i), mid);
                }
                break;
            case 2:
                for(int i=0; i<initLength; i++){
                    List<Integer> mid = this.createMidSolution2();
                    this.selectGenetic(i, population.get(i), mid);
                }
                break;
            case 3:
                for(int i=0; i<initLength; i++){
                    List<Integer> mid = this.createMidSolution3();
                    this.selectGenetic(i, population.get(i), mid);
                }
                break;
            case 4:
                for(int i=0; i<initLength; i++){
                    List<Integer> mid = this.createMidSolution4();
                    this.selectGenetic(i, population.get(i), mid);
                }
                break;
            case 5:
                for(int i=0; i<initLength; i++){
                    List<Integer> mid = this.createMidSolution5(population.get(i));
                    this.selectGenetic(i, population.get(i), mid);
                }
                break;
        }
        
    }

    // 迭代结束，寻找当前最优解
    private void findBest() {
        double min = 99999999d;
        for(int i=0; i<fitness.size(); i++){
            if(fitness.get(i) < min) {
                min = fitness.get(i);
                BestIndex = i;
            }
        }
    }

    private void showResult(){
        int groups = evaluation.calculateK(this.ArrayToDouble(population.get(BestIndex)));
        System.out.println("最小值为"+fitness.get(BestIndex)+", 最小分组数量为 "+groups);
    }

    // 默认运行模式（无参数设置，无进度条显示）
    public void run() {
        long startTime = System.currentTimeMillis();
        this.initPopulation();   // 初始化种群
        this.calculatePopulationValue();  // 计算适应值
        this.findBest();
        int i=1;
        do{
            this.OnceIterator(Choice);
            i += 1;
        }while(i <= iterationTimes);
        this.showResult();
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时："+Tools.formatDuration(endTime-startTime));
    }

    // 带进度条
    public void runWithBar() {
        long startTime = System.currentTimeMillis();
        ProgressBar bar = ProgressBar.build();
        bar.init();
        int process = 0;
        int pre = 0;
        this.initPopulation();   // 初始化种群
        this.calculatePopulationValue();  // 计算适应值
        this.findBest();
        int i=1;
        do{
            this.OnceIterator(Choice);
            process = (int)((i*1.0) / iterationTimes * 100);
            if(process > pre){
                bar.process(process);
                pre = process;
            }
            i += 1;
        }while(i <= iterationTimes);
        this.showResult();
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时："+Tools.formatDuration(endTime-startTime));
    }


    // 测试不同权重下的进化结果
    public void runWithP() {
        for(double p=0.1; p<=0.9; p+=0.1){
            this.setP(Tools.twoDecimals(p));
            // System.out.println(P);
            System.out.println("P="+p);
            this.runWithBar();
        }
    }

    // 测试不同初始种群数量对最终结果的影响
    public void runWithLength() {
        for(int len=10; len<=100; len+=10) {
            this.setInitLength(len);
            System.out.println("initLength="+len);
            this.runWithBar();
        }
    }

    // 不同变异策略的影响
    public void runWithChoice(){
        for(int i=1; i<=5; i++){
            System.out.println("策略"+i);
            this.setChoice(i);
            this.runWithBar();
            System.out.println(); 
        }
    }
    
    public static void main(String[] args) {
        DifferentialEvolution DE = new DifferentialEvolution();
        DE.runWithChoice();
    }
}
