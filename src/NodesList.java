package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/*
* 异构节点操作工具类
*/
public class NodesList {

    private ArrayList<Node> Nodes = new ArrayList<>();  // 异构节点
    private String initPath = "src\\Nodes.txt"; // 节点信息文件路径
    private int propertiesLen;  // 异构长度
    
    public NodesList(){
        this.initNodes();
        this.normalization();
    }

    public int getNodesSize(){
        return this.Nodes.size();
    }

    public int getPropertieslen() {
        return this.propertiesLen;
    }

    public ArrayList<Node> getNodes() {
        return this.Nodes;
    }

    // 根据 文件 初始化所有异构节点
    private boolean initNodes() {
        File file = new File(initPath);
        BufferedReader reader = null;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while((line = reader.readLine()) != null){
                String strs[] = line.split(",");
                int disk = Integer.parseInt(strs[0]);
                int bandwidth = Integer.parseInt(strs[1]);
                int memory = Integer.parseInt(strs[2]);
                int cpu = Integer.parseInt(strs[3]);
                Node node = new Node(disk,bandwidth,memory,cpu);
                Nodes.add(node);
            }
            this.propertiesLen = Nodes.get(0).getPropertiesLen();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    // 异构节点数据归一化
    private boolean normalization() {
        try {
            int n = this.getNodesSize();
            double maximum[] = new double[propertiesLen];
            double minimum[] = new double[propertiesLen];

            for(int j=0; j<n; j++){
                for(int i=0; i<propertiesLen; i++) {
                    maximum[i] = Math.max(maximum[i],Nodes.get(j).getProperty(i));
                    minimum[i] = Math.min(minimum[i],Nodes.get(j).getProperty(i));
                }
            }

            for(int j=0; j<n; j++){
                for(int i=0; i<propertiesLen; i++) {
                    double value = Nodes.get(j).getProperty(i);
                    value = (value - minimum[i]) / (maximum[i] - minimum[i]);
                    Nodes.get(j).setProperty(i, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    
}
