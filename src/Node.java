package src;

/*
* 异构节点实体类
* 包括 磁盘容量,内存大小,带宽上限制,CPU
*/
public class Node {
    private double disk;
    private double bandwidth;
    private double memory;
    private double cpu;

    private int propertiesLength = 4;

    public Node(double disk, double bandwidth, double memory, double cpu){
        this.disk = disk;
        this.bandwidth = bandwidth;
        this.memory = memory;
        this.cpu = cpu;
    }

    private double getDisk() {
        return disk;
    }

    private void setDisk(double disk) {
        this.disk = disk;
    }

    private double getBandwidth() {
        return bandwidth;
    }

    private void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    private double getMemory() {
        return memory;
    }

    private void setMemory(double memory) {
        this.memory = memory;
    }

    private double getCpu() {
        return cpu;
    }

    private void setCpu(double cpu) {
        this.cpu = cpu;
    }

    // 返回异构条件数量
    public int getPropertiesLen() {
        return this.propertiesLength;
    }

    // 根据属性名称获取属性值
    public double getProperty(int index){
        double res = 0;
        switch(index){
            case 0:
                res = this.getDisk();
                break;
            case 1:
                res = this.getBandwidth();
                break;
            case 2:
                res = this.getMemory();
                break;
            case 3:
                res = this.getCpu();
                break;
        }
        return res;
    }

    // 归一化后重新赋值
    public void setProperty(int index, double value) {
        switch(index){
            case 0:
                this.setDisk(value);
                break;
            case 1:
                this.setBandwidth(value);
                break;
            case 2:
                this.setMemory(value);
                break;
            case 3:
                this.setCpu(value);
                break;
        }
    }
        
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(disk);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(bandwidth);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(memory);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(cpu);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (Double.doubleToLongBits(disk) != Double.doubleToLongBits(other.disk))
            return false;
        if (Double.doubleToLongBits(bandwidth) != Double.doubleToLongBits(other.bandwidth))
            return false;
        if (Double.doubleToLongBits(memory) != Double.doubleToLongBits(other.memory))
            return false;
        if (Double.doubleToLongBits(cpu) != Double.doubleToLongBits(other.cpu))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Node [disk=" + disk + ", bandwidth=" + bandwidth + ", memory=" + memory + ", cpu=" + cpu + "]";
    }


    // 计算两个异构节点之间的距离
    public double distance(Node node){
        double res = 0d;
        for(int i=0; i<this.propertiesLength; i++){
            double value1 = this.getProperty(i);
            double value2 = node.getProperty(i);
            res += Math.pow(value1-value2,2); 
        }
            res = Math.sqrt(res);
            return res;
    }


}
