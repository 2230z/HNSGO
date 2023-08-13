import java.util.List;

import src.Node;
import src.NodesList;

import src.Tools;

public class Test {
    public static void main(String[] args) {
        NodesList nodes = new NodesList();
        List<Node> list = nodes.getNodes();
        int N = list.size();
        double[] array1 = new double[N];
        double[] array2 = new double[N];
        double[] array3 = new double[N];
        double[] array4 = new double[N];
        int i=0;
        for(Node e : list) {
            array1[i] = e.getProperty(0);
            array2[i] = e.getProperty(1);
            array3[i] = e.getProperty(2);
            array4[i] = e.getProperty(3);
            i += 1;
        }
        Tools.writeDoubleToTxt(array1, false);
        Tools.writeDoubleToTxt(array2, true);
        Tools.writeDoubleToTxt(array3, true);
        Tools.writeDoubleToTxt(array4, true);
    }
}
