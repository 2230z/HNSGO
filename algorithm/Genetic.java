package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chart.ProgressBar;
import src.Evaluation;
import src.Tools;

/*
 * 遗传算法
 */
public class Genetic {
    private final Evaluation evaluation = new Evaluation();
    private final Random random = new Random();
    private final int iterationTimes = 500;
    private final int N = 10000;    // 解码后数组规模
    private int initLength = 40; // 初始种群染色体数量
    private final double CopulationRate = 0.9;  // 交配率
    private final double MutationRate = 0.05;   //变异率
    private List<List<Integer>> population = new ArrayList<>();   // 种群<染色体>
    private List<Double> fitness = new ArrayList<>();   // 种群适应值

    private double P = 0.5;

    private void setP(double P) {
        this.P = P;
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

    // 从种群中随机选择两个父代
    private int[] parentSelection() {
        int[] res = new int[2];
        int length = this.getPopulationLength();
        double[] probability = new double[length];
        double sum = 0d;
        for(int j=0; j<length; j++) sum += fitness.get(j);
        double total = 0d;
        double localVar = 0d;
        for(int j=0; j<length; j++){
            localVar = fitness.get(j);
            probability[j] = (total + fitness.get(j)) / sum;
            total += localVar;
        }
        for(int j=0; j<res.length; j++)
            res[j] = this.createRandomIndex(probability);
        return res;
    }

    // 轮盘赌产生随机父代下标
    private int createRandomIndex(double[] probability) {
        int index = -1;
        int length = probability.length;
        double value = random.nextDouble();
        for(int j=0; j<length; j++){
            if(value <= probability[j]){
                index = j;
                break;
            }
        }
        return index;
    }

    // 选中父代概率交配，产生后代
    // 单点交叉
    private List<List<Integer>> copulationSinglePoint(int[] parents) {
        List<Integer> parent1 = null;
        List<Integer> parent2 = null;
        int index=0;
        if(parents[0] == parents[1]){
            for(List<Integer> list : population) {
                if(index == parents[0]){
                    parent1 = parent2 = list; 
                }else if(parent1 != null && parent2 != null)    break;
                index++;
            }
        }else{
            for(List<Integer> list : population) {
                if(index == parents[0]){
                    parent1 = list; 
                }else if(index == parents[1]){
                    parent2 = list;
                }else if(parent1 != null && parent2 != null)    break;
                index++;
            }
        }
        List<List<Integer>> children = new ArrayList<>();
        if(random.nextDouble() < CopulationRate) {  // 概率交配
            int randomIndex = random.nextInt(N);
            List<Integer> child1 = new ArrayList<>();
            List<Integer> child2 = new ArrayList<>();
            for(int i=0; i<N; i++) {
                if(i < randomIndex){
                    child1.add(parent1.get(i));
                    child2.add(parent2.get(i));
                } else {
                    child1.add(parent2.get(i));
                    child2.add(parent1.get(i));
                } 
            }
            children.add(child1);
            children.add(child2);
        }else{
            children.add(parent1);
            children.add(parent2);
        }
        return children;
    }

    // 双点交叉
    private List<List<Integer>> copulationDoublePoint(int[] parents) {
        List<Integer> parent1 = null;
        List<Integer> parent2 = null;
        int index=0;
        if(parents[0] == parents[1]){
            for(List<Integer> list : population) {
                if(index == parents[0]){
                    parent1 = parent2 = list; 
                }else if(parent1 != null && parent2 != null)    break;
                index++;
            }
        }else{
            for(List<Integer> list : population) {
                if(index == parents[0]){
                    parent1 = list; 
                }else if(index == parents[1]){
                    parent2 = list;
                }else if(parent1 != null && parent2 != null)    break;
                index++;
            }
        }
        List<List<Integer>> children = new ArrayList<>();
        if(random.nextDouble() < CopulationRate) {  // 概率交配
            int x1 = random.nextInt(N);
            int x2 = random.nextInt(N-x1)+x1;
            List<Integer> child1 = new ArrayList<>();
            List<Integer> child2 = new ArrayList<>();
            for(int i=0; i<N; i++) {
                if(i >= x1 && i < x2){   
                    child1.add(parent2.get(i));
                    child2.add(parent1.get(i));     
                } else {
                    child1.add(parent1.get(i));
                    child2.add(parent2.get(i));
                }
            }
            children.add(child1);
            children.add(child2);
        }else{
            children.add(parent1);
            children.add(parent2);
        }
        return children;
    }

    // 单个染色体变异
    private List<Integer> mutation(List<Integer> child) {
        if(random.nextDouble() < MutationRate) {
            int randomIndex = random.nextInt(N);
            int value;
            do{
                value = random.nextInt(N);
            }while(value == child.get(randomIndex));
            child.set(randomIndex, value+1);
        }
        return child;
    }

    // 子代染色体变异
    private List<List<Integer>> Mutations(List<List<Integer>> children) {
        for(int i=0; i<children.size(); i++)
            children.set(i, this.mutation(children.get(i)));
        return children;
    }

    // 单个放入种群，优胜劣汰
    private void insertToPopulation(List<Integer> child) {
        List<Integer> maxList = null;
        int maxIndex = -1;
        double max = -1d;
        double value = max;
        int index = 0;
        for(List<Integer> list : population){
            value = fitness.get(index);
            if(value > max){
                maxIndex = index;
                max = value;
                maxList = list;
            }
        }
        double childValue = this.calculateAdaptionValue(child);
        if(childValue < max) {
            population.remove(maxIndex);
            fitness.remove(maxIndex);
            population.add(maxList);
            fitness.add(childValue);
        }
    }

    // 子代加入种群
    private void ToPopulation(List<List<Integer>> children) {
        for(List<Integer> child : children) 
            this.insertToPopulation(child);
    }


    // 迭代结束，寻找当前最优解
    private void findBest() {
        // System.out.println();
        double min = 99999999d;
        int index = -1;
        for(int i=0; i<fitness.size(); i++){
            if(fitness.get(i) < min) {
                min = fitness.get(i);
                index = i;
            }
        }
        int groups = evaluation.calculateK(this.ArrayToDouble(population.get(index)));
        System.out.println("最小值为"+min+", 最小分组数量为 "+groups);
        // for(int e : population.get(index))  System.out.print(e+"  ");
        // System.out.println();
    }

    // 默认运行模式（无参数设置，无进度条显示）
    public void run() {
        long startTime = System.currentTimeMillis();
        this.initPopulation();   // 初始化种群
        int i=1;
        do{
            this.calculatePopulationValue();  // 计算适应值
            // System.out.println("第"+i+"次迭代");
            int[] parents = this.parentSelection();  // 选择
            List<List<Integer>> children = this.copulationSinglePoint(parents);  // 交配
            children = this.Mutations(children);   // 变异
            this.ToPopulation(children);  // 子代加入种群，优胜劣汰
            i += 1;
        }while(i <= iterationTimes);
        this.calculatePopulationValue();  // 计算适应值
        this.findBest();
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
        int i=1;
        do{
            this.calculatePopulationValue();  // 计算适应值
            int[] parents = this.parentSelection();  // 选择
            List<List<Integer>> children = this.copulationDoublePoint(parents);  // 交配
            children = this.Mutations(children);   // 变异
            this.ToPopulation(children);  // 子代加入种群，优胜劣汰
            process = (int)((i*1.0) / iterationTimes * 100);
            if(process > pre){
                bar.process(process);
                pre = process;
            }
            i += 1;
        }while(i <= iterationTimes);
        this.calculatePopulationValue();  // 计算适应值
        this.findBest();
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
    
    public static void main(String[] args) {
        Genetic genetic = new Genetic(); 
        genetic.runWithBar();
        // genetic.runWithLength();
        // genetic.runWithP();
        
    }
}