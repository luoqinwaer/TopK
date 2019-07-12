/**
 * 邻接表表示的"无向图(List Undirected Graph)"
 */

import java.io.IOException;
import java.util.*;

public class ListUDG {

    // 邻接表中表对应的链表的顶点
    private class ENode {
        int ivex;       // 该边所指向的顶点的位置
        ENode nextEdge; // 指向下一条弧的指针
    }

    // 邻接表中表的顶点
    private class VNode {
        int num;            //顶点编号
        char data;          // 顶点信息
        boolean isVisit;
        ENode firstEdge;    // 指向第一条依附该顶点的弧
    };

    public Map<Integer,Integer> numPosition =new HashMap<>();
    private VNode[] mVexs;  // 顶点数组
    //private char[] typrSet={'A','B','C','D','E','F','G'};
    private char[] typeSet={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};



    public ListUDG(ArrayList<Integer> vexs, int[][] edges) {
        //public ListUDG(int[] vexs, int[][] edges) {

        // 初始化"顶点数"和"边数"
        int vlen = vexs.size();
        int elen = edges.length;

        //维护一张顶点与其在列表中的位置的MAP <num, position> 提高访问效率
        for (int i = 0; i < vlen; i++) {
            numPosition.put(vexs.get(i),i);
        }

        // 初始化"顶点"
        mVexs = new VNode[vlen];  //顶点对象
        Random random =new Random();
        for (int i = 0; i < mVexs.length; i++) {
            mVexs[i] = new VNode();
            mVexs[i].num=vexs.get(i);
            //mVexs[i].data = typeSet[random.nextInt(26)];
            mVexs[i].data = typeSet[mVexs[i].num%26];
            mVexs[i].isVisit=false;
            mVexs[i].firstEdge = null;
        }

        // 初始化"边"
        for (int i = 0; i < elen; i++) {

            // 读取边的起始顶点和结束顶点

           /* int p1 = edges[i][0];
            int p2 = edges[i][1];*/

            // 读取边的起始顶点和结束顶点
            //在顶点数组中的位置
            //int p1 = getPosition(edges[i][0]);
            //int p2 = getPosition(edges[i][1]);
            int p1 =numPosition.get(edges[i][0]);
            int p2 =numPosition.get(edges[i][1]);

            // 初始化node1
            ENode node1 = new ENode();
            node1.ivex = p2;

            // 将node1链接到"p1所在链表的末尾"
            if(mVexs[p1].firstEdge == null)
            {
                mVexs[p1].firstEdge = node1;
            }
            else
            {
                linkLast(mVexs[p1].firstEdge, node1);
            }

            // 初始化node2
            ENode node2 = new ENode();
            node2.ivex = p1;

            // 将node2链接到"p2所在链表的末尾"
            //用数组还是链表要一致
            if(mVexs[p2].firstEdge == null){
                mVexs[p2].firstEdge = node2;
            }
            else{
                linkLast(mVexs[p2].firstEdge, node2);
            }

        }
    }

    /*
     * 将node节点链接到list的最后
     */
    private void linkLast(ENode list, ENode node) {
        ENode p = list;
        while(p.nextEdge!=null)
            p = p.nextEdge;

        p.nextEdge = node;
    }

    /*
     * 返回ch位置
     */
    private int getPosition(int ch) {

        for(int i=0; i<mVexs.length; i++)
            if(mVexs[i].num==ch)
                return i;

        return -1;
    }

    /*
     * 打印矩阵队列图
     */
    public void print() {
        System.out.printf("List Graph:\n");

        for (int i = 0; i < mVexs.length; i++) {

            System.out.printf("%d(%c): ", mVexs[i].num, mVexs[i].data);
            ENode node = mVexs[i].firstEdge;

            while (node != null) {
                System.out.printf("%d(%c) ", mVexs[node.ivex].num, mVexs[node.ivex].data);
                node = node.nextEdge;
            }
            System.out.printf("\n");

        }
    }

    //获取各个顶点的邻接表
    public ArrayList<Integer> getAdjacentList(int num){

        ArrayList<Integer>  AdjacentList=new ArrayList<>();
        int i =numPosition.get(num);
        ENode node = mVexs[i].firstEdge;
        while (node != null) {
            AdjacentList.add(mVexs[node.ivex].num);
            node = node.nextEdge;
        }
        return AdjacentList;
    }

    //获取顶点的data
    public char getNodeData(int num){
        return mVexs[num].data;
    }

    //获取顶点的访问信息
    public boolean getNodeStatu(int num){
        return mVexs[num].isVisit;
    }

    //设置顶点的访问信息
    public void setNodeStatu(int num){
        mVexs[num].isVisit=true;   //表示被访问过
    }

    public ArrayList<Integer> get_candidate(char type){     //返回候选者集合
        ArrayList<Integer>  candidate_set=new ArrayList<>();
        for (int i = 0; i < mVexs.length; i++) {
            if(mVexs[i].data==type){
                candidate_set.add(mVexs[i].num);
            }
        }
        return candidate_set;
    }

}