package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import chart.ProgressBar;
import src.Evaluation;
import src.Node;
import src.NodesList;
import src.Tools;

/*
* k-means 类
*/
public class Kmeans {
    private final Evaluation evaluation = new Evaluation();
    private ProgressBar bar = ProgressBar.build();
    private double P = 0.5;
    private int maxCount = 200;   // 单次聚类最大迭代次数
    private int K;  // 簇
    private NodesList nodesList = new NodesList();
    private ArrayList<Node> Nodes = nodesList.getNodes();  // 异构节点
    private List<Node> centerPoints = new ArrayList<>();   // 簇中心点
    private List<Node> preCenterPoints = new ArrayList<>();   // 上一次簇中心点
    private final int N = Nodes.size();  // 异构节点数量
    private int propertiesLen = nodesList.getPropertieslen();
    private int preResult[][] = new int[N][N];  // 上一次聚类结果
    private int result[][] = new int[N][N];  // 聚类结果
    private final int[] Range = {1, N};
 
    private void setK(int k){
        this.K = k;
    }

    // 生成初始中心点
    private List<Node> randomPoint() {
        int length = N;
        List<Node> locaList = new ArrayList<>();
        for(Node e : this.Nodes)    locaList.add(e);
        List<Node> lists = new ArrayList<>();
        Random random = new Random();
        int i=0;
        int randomKey = -1;
        while(i < this.K) {
            randomKey = random.nextInt(length);
            lists.add(locaList.get(randomKey));
            locaList.remove(randomKey);
            i += 1;
            length -= 1;
        }
        return lists;
    }

    // 寻找一维数组最大值对应的下标
    private int findBest(double[] value) {
        double max = -1d;
        int index = -1;
        for(int i=0; i<value.length; i++){
            if(value[i] > max) {
                max = value[i];
                index = i;
            }
        }
        return index;
    }

    // kmeans++,初始中心点改进版本
    private List<Node> randomPointUpGrades() {
        List<Node> lists = new ArrayList<>();
        lists.add(Nodes.get(new Random().nextInt(N)));
        double[] dis = new double[N];
        Arrays.fill(dis, 9999);
        for(int i=1; i<K; i++) {
            for(int j=0; j<N; j++)
                dis[j] = Math.min(dis[j],lists.get(i-1).distance(Nodes.get(j)));
            lists.add(Nodes.get(this.findBest(dis)));
        }
        return lists;
    }

    // 一次分配
    private void clustering() {
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++)    this.result[i][j] = 0;
        }
        double min;
        int mX;
        for(int i=0; i<N; i++){
            min = 99999999d;
            mX = 0;
            for(int j=0; j<this.K; j++){
                double dis = this.Nodes.get(i).distance(this.centerPoints.get(j));
                if( dis < min){
                    min = dis;
                    mX = j;
                }
            }
            result[mX][i] = 1;  // 第 i-1 个节点
        }
    }

    // 判断是还还需要迭代
    private boolean needGoOnClustering() {
        // 中心点
        boolean groupingTag = false, centerPointsTag = false;
        for(int i=0; i<this.K; i++){
            for(int j=0; j<N; j++){
                if(this.result[i][j] != this.preResult[i][j]) {
                    groupingTag = true;
                    break;
                }
            }
            if(groupingTag) break;
        }

        // 聚类结果
        for(int i=0; i<this.K; i++){
            if(!this.centerPoints.get(i).equals(this.preCenterPoints.get(i))){
                centerPointsTag = true;
                break;
            }
        }
        return groupingTag && centerPointsTag;
    }

    // 更新簇中心点
    private void updateCenterPoint(){
        List<Node> nLists = new ArrayList<>();
        for(int i=0; i<this.K; i++){
            double values[] = new double[this.propertiesLen];
            int nums = 0;
            for(int j=0; j<N; j++){
                if(this.result[i][j] > 0){
                    for(int p=0; p<this.propertiesLen; p++){
                        values[p] += this.Nodes.get(j).getProperty(p);
                    }
                    nums++;
                }
            }
            if(nums > 0)    nLists.add(new Node(values[0]/nums, values[1]/nums, values[2]/nums, values[3]/nums));
            else nLists.add(this.centerPoints.get(i));
        }
        this.preCenterPoints = this.centerPoints;
        this.centerPoints = nLists;
    }


    // 分为this.K个簇，返回聚类结果
    public int[][] runOnce() {
        int num = 1;
        // this.centerPoints = this.randomPoint();
        this.centerPoints = this.randomPointUpGrades();
        do{
            this.clustering();
            this.updateCenterPoint();
            num++;
        }while(this.needGoOnClustering() && num <= this.maxCount);
        return this.result;
    }

    private void run() {
        long startTime = System.currentTimeMillis();
        int[] X = new int[Range[1]-Range[0]+1];
        double[] Y = new double[Range[1]-Range[0]+1];
        for(int i=Range[0]; i<=Range[1]; i++){
            this.setK(i);
            int[][] result = this.runOnce();
            double value = evaluation.calculateDMvalue(result, P);
            X[i-Range[0]] = i;
            Y[i-Range[0]] = value;
        }
        System.out.println();
        Tools.writeIntegerToTxt(X, false);
        Tools.writeDoubleToTxt(Y, true);
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时："+Tools.formatDuration(endTime-startTime)); 
    }

    private void runWithBar() {
        long startTime = System.currentTimeMillis();
        bar.init();
        int process = 0;
        int pre = 0;
        int[] X = new int[Range[1]-Range[0]+1];
        double[] Y = new double[Range[1]-Range[0]+1];
        for(int i=Range[0]; i<=Range[1]; i++){
            this.setK(i);
            int[][] result = this.runOnce();
            double value = evaluation.calculateDMvalue(result, P);
            X[i-Range[0]] = i;
            Y[i-Range[0]] = value;
            process = (int)(((i-Range[0])*1.0) / (Range[1] - Range[0] ) * 100);
            if(process > pre){
                bar.process(process);
                pre = process;
            }
        }
        System.out.println();
        Tools.writeIntegerToTxt(X, false);
        Tools.writeDoubleToTxt(Y, true);
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时："+Tools.formatDuration(endTime-startTime)); 
    }

    public static void main(String[] args) {
        Kmeans kmeans = new Kmeans();
        kmeans.runWithBar();
    }

}
