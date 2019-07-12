import java.io.*;
import java.util.ArrayList;

public class FormatInput {
  /*  public static void main(String[] args) {
        readFile();
       //System.out.println(vetex);
        //System.out.println(a);
        //System.out.println(b);
        for (int i = 0; i <edge.length ; i++) {
            System.out.println(edge[i][0]+"   "+edge[i][1]);
        }
    }*/
    public static int[][] edge;
    public static ArrayList<Integer> vetex=new ArrayList<>();
    public  static ArrayList<Integer> a = new ArrayList();
    public  static ArrayList<Integer> b = new ArrayList();

    /**
     * 读取数据
     */
    /*public static void readFile()*/
    public  FormatInput()
    {
        //String pathname = "100n100e.txt"; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        String pathname = "10000n10000e.txt";
        //String pathname = "1000000n1000000e.txt";
        //String pathname = "100tn100te.txt";
        //不关闭文件会导致资源的泄露，读写文件都同理
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ){
            String line;
            //将数据读取到数数组中去
            // 图数据文件中数据的形式 7->7 CR LF
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                String[] res1=line.split("\n\r");
                String[] res2=res1[0].split("\t");
                //加入顶点表
                if(!vetex.contains(Integer.valueOf(res2[0])))   vetex.add(Integer.valueOf(res2[0]));
                if(!vetex.contains(Integer.valueOf(res2[1])))   vetex.add(Integer.valueOf(res2[1]));
                if(!(Integer.valueOf(res2[0])==Integer.valueOf(res2[1]))) //去除环
                {
                    a.add(Integer.valueOf(res2[0]));
                    b.add(Integer.valueOf(res2[1]));
                }
                else{
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将列表中的数据取出  组成二维数组
        edge=new int[a.size()][2];
        for (int i = 0; i <a.size() ; i++) {
            edge[i][0]=a.get(i);
            edge[i][1]=b.get(i);
        }

    }

}
