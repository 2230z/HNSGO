package src;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class RandomNodes {

    private Random R = new Random();
    private int propertiesLen  = 4;
    private String path = "src\\Nodes.txt";
    private int nums = 10000;
    private int randomInt(int key){
        int res = 0;
        switch(key){
            case 0:
                // disk [50,200]
                res = R.nextInt(151)+50;
                break;
            case 1:
                // bandwidth  [100,1000]
                res = R.nextInt(901)+100;
                break;
            case 2:
                // memeory [1,4]
                res = R.nextInt(4)+1;
                break;
            case 3:
                // cpu [1,10]
                res = R.nextInt(10)+1;
                break;
        }
        return res;
    }

    private String randomNode() {
        StringBuilder str = new StringBuilder();
        for(int i=0; i<this.propertiesLen; i++){
            str.append(this.randomInt(i));
            if(i+1 < this.propertiesLen) str.append(",");
        }
        return new String(str);
    }

    private void writeToTxt() throws IOException {
        File fout = new File(this.path);  
        FileOutputStream fos = new FileOutputStream(fout);         
        OutputStreamWriter osw = new OutputStreamWriter(fos);          
        for (int i = 0; i < this.nums; i++) { 
            String node = this.randomNode(); 
            osw.write(node+"\n");  
        }     
        osw.close();
     }

     public static void main(String[] args) throws IOException {
        RandomNodes rm = new RandomNodes();
        rm.writeToTxt();
     }
    
}
