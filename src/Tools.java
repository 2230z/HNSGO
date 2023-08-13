package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Tools {

    private static int N = 1000;
    private static final String Path = "result/result.txt";
    // 判断矩阵元素之和是否为N
    public static boolean judgeResult(int[][] result){
        int sum = 0;
        for(int i=0; i<result.length; i++){
            for(int j=0; j<result[i].length; j++){
                if(result[i][j] > 0)    sum += 1;
            }
        }
        if(sum != N)    return false;
        else return true;
    }

    // double类型数值保留两位小数
    public static double twoDecimals(double value) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(1, RoundingMode.HALF_UP).doubleValue();
    }


    public static String formatDuration(long times) {
        int seconds = (int)times;
        seconds /= 1000;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d小时%02d分钟%02d秒", hours, minutes, remainingSeconds);
    }

    // 实验结果溢写到txt文件
    public static void writeIntegerToTxt(int[] values, boolean append){
        if(values.length < 1)   return;
        try{
            FileWriter fw = new FileWriter(Path,append);
            BufferedWriter out = new BufferedWriter(fw);
            for(int i=0; i<values.length; i++){
                out.write(values[i]+" ");
            }
            out.newLine();
            out.flush();
            out.close();
            fw.close();
        } catch (Exception e) {

        }
        
    }

    public static void writeDoubleToTxt(double[] values, boolean append){
        if(values.length < 1)   return;
        try{
            FileWriter fw = new FileWriter(Path,append);
            BufferedWriter out = new BufferedWriter(fw);
            for(int i=0; i<values.length; i++){
                out.write(values[i]+" ");
            }
            out.newLine();
            out.flush();
            out.close();
            fw.close();
        } catch (Exception e) {

        }
        
    }




}
