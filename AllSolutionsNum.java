
// 求解问题所有可行解数量

import java.util.Random;

public class AllSolutionsNum {

    private final int N = 50;

    // 递归求解
    private long getSolutionsNumber(int size, int Fm) {
        if(size == 1 || Fm == size || Fm ==1)   return 1;
        int num = 1;
        // int Fm = N;
        // int Fz = Fm - size + 1;
        long total = 0;
        long local = 1;
        int temp = 0;
        int maxIter = size/2;
        for(int Fz=Fm-size+1; Fz>maxIter; Fz--){
            num = 1;
            while(num <= maxIter) {
                temp = Fm;
                local = 1;
                local *= (this.solutions(temp, Fz));
                temp -= Fz;
                local *= getSolutionsNumber(size-1, temp);
                num++;
            }
            total += local;
        }
        return total;
    }
    
    private int solutions(int a, int b){
        //分子
		int son = 1;
		//分母
		int mother = 1;
		// 应用组合数的互补率简化计算量
		b = b > (a - b) ? (a - b) : b;
		for(int i=b; i > 0; i--) {
		    //如果你还记得上面的求全排列数的实现，你应该会发现 son 就是在求 A(n，m)
			son *= a;
			mother *= i;
			a--;
		}
		return son / mother;
    }

    public long AllSolutions(int N) {
        long total = 0;
        for(int i=1; i<=N; i++) total += this.getSolutionsNumber(i, N);
        return total;
    }

    public static void main(String[] args) {
        // Demo demo = new Demo();
        // for(int i=1; i<25; i++)
        //     System.out.println(i+"\t"+demo.AllSolutions(i));
        for(int i=0; i<10; i++)
            System.out.println(new Random().nextInt(5-(-5)+1)-5);
    }
}
