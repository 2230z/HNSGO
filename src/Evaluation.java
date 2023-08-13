package src;

import java.util.ArrayList;
import java.util.List;

public class Evaluation {

    private int M1, M2;  // 最好/最坏
    private double D1, D2;

    private double p,q; // p+q=1
    private int O;

    private int groupNum = 0;

    private NodesList nodesList = new NodesList();   
    private List<Node> Nodes = nodesList.getNodes();
    private int N = nodesList.getNodesSize();
    private int length = nodesList.getPropertieslen();

    public Evaluation(){
        this.init();
    }

    // 初始参数
    private void init(){
        p = 0.2;
        q = 0.8;
        O = 2;
        M1 = 1;
        M2 = N;
        D1 = 0;
        int[][] firstResult = new int[N][N];
        for(int i=0; i<N; i++) firstResult[0][i] = i+1;
        List<Node> centers = this.calculateCenterpoints(firstResult);
        D2 = this.calculateDvalue(firstResult, centers,1);
    }


    // 根据分组结果计算中心点
    private List<Node> calculateCenterpoints(int[][] result){
        List<Node> centerPoints = new ArrayList<>();
        int num;
        boolean tag;
        for(int i=0; i<N; i++){
            num = 0;
            tag = false;
            double[] properties = new double[length];
            for(int j=0; j<N; j++){
                if(result[i][j] > 0){
                    tag = true;
                    for(int k=0; k<length; k++){
                        properties[k] += Nodes.get(j).getProperty(k);
                    }
                    num += 1;
                }
            }
            if(tag) centerPoints.add(new Node(properties[0]/num, properties[1]/num, properties[2]/num, properties[3]/num));
        }
        return centerPoints;
    }

    // 分组后的的方差总和
    private double calculateDvalue(int[][] result, List<Node> centerPoints) {
        double res = 0d;
        double dis;
        for(int i=0; i<groupNum; i++){
            dis = 0d;
            for(int j=0; j<N; j++){
                if(result[i][j] > 0){
                    dis += centerPoints.get(i).distance(this.Nodes.get(j));
                }
            }
            res += dis;
        }
        return res;
    }

    private double calculateDvalue(int[][] result, List<Node> centerPoints, int groups) {
        double res = 0d;
        double dis;
        for(int i=0; i<groups; i++){
            dis = 0d;
            for(int j=0; j<this.N; j++){
                if(result[i][j] > 0){
                    dis += centerPoints.get(i).distance(this.Nodes.get(j));
                }
            }
            res += dis;
        }
        return res;
    }

    // 计算分组数量
    public int calculateK(int[][] result){
        boolean tag = false;
        int num = 0;
        for(int i=0; i<N; i++){
            tag = false;
            for(int j=0; j<N; j++){
                if(result[i][j] > 0){
                    tag = true;
                    break;
                }
            }
            if(tag) num += 1;
        }
        return num;
    }   

    // 把非空行提到前面
    private int[][] filter(int[][] result) {
        int[][] array = new int[N][N];
        boolean tag = false;
        int index = 0;
        for(int i=0; i<N; i++){
            tag = false;
            for(int j=0; j<N; j++) {
                if(result[i][j] > 0) {
                    tag = true;
                    break;
                }
            }
            if(tag){
                for(int j=0; j<N; j++)  array[index][j] = result[i][j];
                index++;
            }
        }
        return array;
    }

    // 唯一暴露
    // 折中单目标
    public double calculateDMvalue(int[][] result, double p) {
        this.p = p;
        this.q = 1 - p;
        result = this.filter(result);
        List<Node> centers = this.calculateCenterpoints(result);
        int M = this.calculateK(result);
        this.groupNum = M;
        double D = this.calculateDvalue(result, centers);
        double res = 0d;
        double factor1 = Math.pow(Math.abs((D - D1)*1.0 / (D2 - D1)),O);
        double factor2 = Math.pow(Math.abs((M1 - M)*1.0 / (M1 - M2)),O);
        res = Math.pow(p*factor1+q*factor2,1.0/O);
        return res;
    }

}
 