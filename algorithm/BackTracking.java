package algorithm;

import src.Evaluation;

// 回溯法
// 小规模遍历所有可行解
public class BackTracking {
    private final Evaluation evaluation = new Evaluation();
    private final int N = 10;
    private int[] Indexs = new int[N];
    private int[][] result = new int[N][N];
    private double MinValue = 99999d;
    private final double P = 0.5;

    private void init() {
        for(int i=0; i<N; i++)
            result[0][i] = 1;
    }

    private void show() {
        for(int i=0; i<N; i++){
            System.out.print(Indexs[i]+"  ");
        }
        System.out.println();
    }

    private void dfs() {
        int top = N-1;
        while(true) {
            for(int i=0; i<N; i++){
                result[Indexs[top]][top] = 0;
                Indexs[top] = i;
                result[Indexs[top]][top] = 1;
                double val = evaluation.calculateDMvalue(result, P);
                if(val < MinValue){
                    MinValue = val;
                    this.show();
                    System.out.println(val);
                    System.out.println();
                }
            }
            while(top >= 0 && Indexs[top] == N-1){
                result[Indexs[top]][top] = 0;
                Indexs[top] = 0;
                result[0][top] = 1;
                top -= 1;
            }
            if(top == 0)   break;
            result[Indexs[top]][top] = 0;
            Indexs[top] += 1;
            result[Indexs[top]][top] += 1;
            while(top < N-1)
                top += 1;
        }
    }

    public void run() {
        this.init();
        this.dfs();
        System.out.println("最优结果为："+MinValue);
    }

    public static void main(String[] args) {
        BackTracking backTracking = new BackTracking();
        backTracking.run();
    }
}
