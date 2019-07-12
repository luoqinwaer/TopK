import javax.xml.crypto.dsig.keyinfo.PGPData;
import java.sql.SQLOutput;
import java.util.*;

public class TopkQurey {

    public static final int N = 9;
    public static final int K = 10;
    public static final double alpha = 0.1;

    public static ArrayList<Integer> candidate_set = new ArrayList<>();    //候选者集合
    //维护一个specific node 和对应的迭代轮次的 字典  <node,iterNum>
    //public static Map<Integer,Double> candidate_offset=new HashMap<>();
    public static Map<Integer,Double> specific_offset=new HashMap<>();
    public static ArrayList<Integer> specificNodeSet=new ArrayList<>();   //specific节点集合
    public static char queryType;   //查询类型

    public static void main(String[] args) {
        ListUDG pG;

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入查询类型:");
        queryType = sc.nextLine().charAt(0); //获取输入的查询类型

        long startGraph=System.currentTimeMillis();
        FormatInput fi=new FormatInput();
        System.out.println("读取文件需要时间"+(System.currentTimeMillis()-startGraph));
        pG = new ListUDG(fi.vetex,fi.edge); //制图

        candidate_set=pG.get_candidate(queryType);  //获得候选者集合
        //System.out.println(candidate_set);

        System.out.println("请输入查询节点:");
        String specNodeLine=sc.nextLine();
        for (String i:specNodeLine.split(" ")) {    //获得specific节点集合
            specificNodeSet.add(Integer.valueOf(i));
        }

        //pG.print();   // 打印图
        TopkQurey k=new TopkQurey();
        k.initialize();

        int loopNum=0;
        long  start=System.currentTimeMillis();
        do{
            loopNum++;
            long  start1=System.currentTimeMillis();
            int i=arrangement(pG);
            long  end1=System.currentTimeMillis();
            long  start2=System.currentTimeMillis();
            //System.out.println(i);
            bfs(pG,i);
            long  end2=System.currentTimeMillis();
            System.out.println("计算优先顶点花费时间： "+(end1-start1));
            System.out.println("bfs花费时间： "+(end2-start2));
            System.out.println("这一轮迭代中 查询的节点总数");
        }while(!emergency_test(pG));
        long end=System.currentTimeMillis();
        System.out.println("完成查询需要时间"+(end-start));
        System.out.println(candidate_set);
        System.out.println("迭代的轮次总数为"+loopNum);
    }

    /**
     * 每一次迭代并不是说有的fai值都发生变化 大部分顶点的fai值要么为0  要么不发生变化
     * 我们要找出发生变化的那一部分 仅仅对这一部分进行计算即可
     */

    public static  specificNode[] specificNode;  //对象数组

    public  class specificNode{     //specificNode对象类
        //有一个队列存放邻接的信息  存放迭代次序
        ArrayList<Integer> visitedNode; //已经访问到的节点
        ArrayList<Integer> lastVisitedNode; //上一次新加进来的节点
        int iterNum;    //迭代轮次
        int nodeNum;    //该点在图中的顶点
        Map<Integer,Double> faiDownList;       //candidate, fai_down
        Map<Integer,Double> faiUpList;       //candidate, fai_up
        //存储在一轮迭代中fai值发生变化的节点和值
        Map<Integer,Double> changedFaiDownList;       //candidate, fai_up
        Map<Integer,Double> changedFaiUpList;       //candidate, fai_up
    }

    /**
     * 初始化
     */
    public  void initialize(){  //死的初始化  后面可以写活

        specificNode =new specificNode[specificNodeSet.size()];
        //对每一个 specificNode里面的节点 初始化对象
        for (int i = 0; i < specificNode.length; i++) {
            specificNode[i]=new specificNode();
            specificNode[i].visitedNode=new ArrayList<>();
            specificNode[i].lastVisitedNode=new ArrayList<>();
            specificNode[i].iterNum=0;
            specificNode[i].nodeNum=specificNodeSet.get(i);
            specificNode[i].faiDownList=new HashMap<>();
            specificNode[i].faiUpList=new HashMap<>();
            //这一次计算fai值的时候 改变的fai值
            specificNode[i].changedFaiDownList=new HashMap<>();
            specificNode[i].changedFaiUpList=new HashMap<>();
        }

        //初始化 specific_offset
        for (int i = 0; i < specificNodeSet.size(); i++) {
            specific_offset.put(i,0.0);
        }
    }

    //public static int num=0;
    /**
     * 进行一次迭代 将邻居节点加入访问过的节点列表中
     * @param pG
     * @param position
     */
    public static void bfs(ListUDG pG,int position){  //position表示在specificNode数组中的位置 即索引
        //num=0;
        ArrayList<Integer> temp ,temp1= new ArrayList<>();
        if (specificNode[position].iterNum == 0){  //一次迭代也没有进行 说明 两个list都是为空
            specificNode[position].visitedNode=pG.getAdjacentList(specificNode[position].nodeNum);
            specificNode[position].lastVisitedNode=pG.getAdjacentList(specificNode[position].nodeNum);
            //把初始节点加入
            specificNode[position].visitedNode.add(specificNode[position].nodeNum);
        }
        else    //进行过迭代 说明 两个list不为空
        {
            for(int j:specificNode[position].lastVisitedNode) { //对上一次刚刚加入列表中的节点进行bfs
                temp =pG.getAdjacentList(j);
                //num+=temp.size();
                for (int k:temp) {
                    if(!specificNode[position].visitedNode.contains(k)){
                        specificNode[position].visitedNode.add(k);
                        temp1.add(k);
                    }
                }
            }
            //把初始节点加入
            specificNode[position].visitedNode.add(specificNode[position].nodeNum);
            //调整上次访问节点列表
            specificNode[position].lastVisitedNode.clear();
            specificNode[position].lastVisitedNode=temp1;
        }
        //迭代过后  迭代轮次增加
        specificNode[position].iterNum++;

        //System.out.println(num);
    }


    /**
     * 按照情况 将值放入列表中
     * @param pG
     * @param position
     * @param candidateNode
     */
    public static void  fai_down ( ListUDG pG,int position, int candidateNode){
        if (specificNode[position].iterNum == 0) {  //iterNum为0 faiDownList
            if (specificNode[position].nodeNum== candidateNode){
                specificNode[position].faiDownList.put(candidateNode,1.0);

                specificNode[position].changedFaiDownList.put(candidateNode,1.0);
            }
            else{
                specificNode[position].faiDownList.put(candidateNode,0.0);

                specificNode[position].changedFaiDownList.put(candidateNode,0.0);
            }
        }
        else    //iterNum大于等于1
        {
            if(specificNode[position].visitedNode.contains(candidateNode)){     //迭代到该节点
                if(!(specificNode[position].faiDownList.get(candidateNode)>0)){       //小于等于0  需要从邻居获得    有问题
                    double neighboor =getFromNeighbor(pG,position,specificNode[position].nodeNum,candidateNode,specificNode[position].iterNum);
                    specificNode[position].faiDownList.put(candidateNode,neighboor);

                    specificNode[position].changedFaiDownList.put(candidateNode,neighboor);
                }
            }
            else
            {
                specificNode[position].faiDownList.put(candidateNode,0.0);

                specificNode[position].changedFaiDownList.put(candidateNode,0.0);
            }
        }

    }


    /**
     * 当节点的fai_down 值需要通过邻居节点获得时候 调用此函数
     * @param pG
     * @param position specificNode中的位置
     * @param v1    specificNode
     * @param v2    candidateNode
     * @param iterNum 迭代轮次
     * @return
     */
    public static double getFromNeighbor ( ListUDG pG,int position, int v1,int v2, int iterNum){
        double res=0;
        if (iterNum == 0) {     //迭代轮次等于0情况
            if (v1== v2)
                return 1;
            else
                return 0;
        }
        ArrayList<Integer> temp =specificNode[position].visitedNode; //获得已经访问过的节点
        if(temp.contains(v2)){   //如果包含  则进行计算
            if(getFromNeighbor(pG,position,v1,v2,(iterNum-1))>0){
                return getFromNeighbor(pG,position,v1,v2,(iterNum-1));
            }
            else{
                ArrayList<Integer> temp1=pG.getAdjacentList(v2);
                //加一个判断
                for (int i = 0; i < temp1.size(); i++) { //获得v2的邻居
                    if(specificNode[position].faiDownList.keySet().contains(temp1.get(i))){ //之前已经计算过，这里就不用计算直接取出来用就好
                        res+=specificNode[position].faiDownList.get(temp1.get(i));
                    }
                    else
                    {
                        res+=getFromNeighbor(pG,position,v1,temp1.get(i),(iterNum-1));  //没有的话  强行算
                    }
                    /*if(temp.contains(temp1.get(i))){
                        res+=getFromNeighbor(pG,position,v1,temp1.get(i),(iterNum-1));
                    }*/
                }
                return alpha*res;
            }
        }else{
            return 0;
        }

    }


    /**
     * 根据对应情况将值放入对应的列表中
     * @param pG
     * @param position specificNode中的位置
     * @param candidate 候选者集合中的值
     */
    public static void fai_up ( ListUDG pG,int position,  int candidate){
        if (specificNode[position].iterNum == 0) {
            if (specificNode[position].nodeNum == candidate) //二者值相等
            {
                specificNode[position].faiUpList.put(candidate,1.0);

                specificNode[position].changedFaiUpList.put(candidate,1.0);

            }
            else{
                specificNode[position].faiUpList.put(candidate,N*alpha);

                specificNode[position].changedFaiUpList.put(candidate,N*alpha);
            }

        }
        //迭代次序不等于0的情况
        if(specificNode[position].faiDownList.get(candidate)>0){
            specificNode[position].faiUpList.put(candidate,specificNode[position].faiDownList.get(candidate));

            specificNode[position].changedFaiUpList.put(candidate,specificNode[position].faiDownList.get(candidate));
        }
        else{
            specificNode[position].faiUpList.put(candidate,N*Math.pow(alpha,specificNode[position].iterNum+1));

            specificNode[position].changedFaiUpList.put(candidate,N*Math.pow(alpha,specificNode[position].iterNum+1));
        }
    }


    /**
     *  同S_down
     * @param pG
     * @param candidate
     * @return
     */
    public static double S_up(ListUDG pG,int candidate){   //要不要把迭代轮次带进来
        double res=0;
        for (int i = 0; i <specificNode.length ; i++) {
            res+=specificNode[i].faiUpList.get(candidate);
        }
        return res;
    }


    /**
     * 对于每个specificNode的节点 从faiDownList中取到对应candidate的值 计算S_down
     * @param pG
     * @param candidate 候选者集合中的元素值
     * @return 对于每个候选者集合中的值 得到S_down
     */
    public static double S_down(ListUDG pG,int candidate){   //要不要把迭代轮次带进来
        double res=0;
        for (int i = 0; i <specificNode.length ; i++) {
            //从specificNode中的faiDownList中直接获取
            res+=specificNode[i].faiDownList.get(candidate);
        }
        return res;
    }


    /***
     *  根据保存在specificNode节点对象中的fai_down 和 fai_up 值 计算count 值
     * @param pG
     * @param position
     * @return specificNode中节点的count 供arrangement使用
     */
    public static  double count_P(ListUDG pG,int position){
        specificNode[position].changedFaiDownList.clear();  //每次计算前先清空
        specificNode[position].changedFaiUpList.clear();
        //这里维护一张 Map<candidate, value> 大部分时候 这个值是不变的 我们只需要统计变的那部分即可
        //double res =0;
        double res1 =0;
        for (int i:candidate_set) {     //先执行 down 再执行up 保证有值
            //if(specificNode[position].lastVisitedNode.contains(i)){
                fai_down(pG,position,i);
                fai_up(pG,position,i);
            //}

         /*   if(specificNode[position].changedFaiUpList.get(i)!=null){
                res1+=specificNode[position].changedFaiUpList.get(i);
            }
            if(specificNode[position].changedFaiUpList.get(i)!=null){
                res1+=specificNode[position].changedFaiUpList.get(i);
            }*/
            //res+=specificNode[position].faiUpList.get(i)-specificNode[position].faiDownList.get(i);
        }
        //这里返回一个偏置值
        for (double val:specificNode[position].changedFaiUpList.values()) {
            res1+=val;
        }
        for (double val:specificNode[position].changedFaiDownList.values()) {
            res1-=val;
        }
        return res1;
    }


    /***
     *  选出下一次迭代的点
     * @param pG
     * @return 下次迭代的点在specificNode的position
     */
    public static int arrangement(ListUDG pG){
        //这里维护一个map <specificNode value> 下次计算时 用偏移量加上Map中的值 得到结果
        int arrange=0;
        double base=-100;
     /*   for (int i = 0; i <specificNode.length ; i++) {  //i position
            if(count_P(pG,i)>base)
                arrange=i;
        }*/
        // new
        for (int i = 0; i <specificNode.length ; i++) {  //i position
            double res=specific_offset.get(i)+count_P(pG,i);
            if(res>base){
                arrange=i;
                base=res;
            }
            specific_offset.put(i,res);
        }
        return arrange;
    }


    /**
     *  候选集合数据符合条件时退出循环
     * @param pG
     * @return boolean
     */
    public static boolean emergency_test(ListUDG pG){
        if(candidate_set.size()>K){
            ArrayList<Integer> removeList=new ArrayList<>();
            /*ArrayList<Double> s_Down=new ArrayList<>();     //先统计所有节点的S_down
            ArrayList<Integer> removeList=new ArrayList<>();
            s_Down.clear();
            for (int j:candidate_set) {
                s_Down.add(S_down(pG,j));
            }
            Collections.sort(s_Down);   //可以去底层看一看 找到更高效率的写法 默认升序
            double base=s_Down.get(s_Down.size()-K);*/

            //利用小顶堆实现 找到第K大的元素
            PriorityQueue<Double> pq=new PriorityQueue<>();
            for (int j:candidate_set){
                pq.add(S_down(pG,j));
                if(pq.size()>K){
                    pq.poll();
                }
            }
            double base =pq.peek();

            for (int i = 0; i <candidate_set.size();i++) {      //不符合条件的去掉
                //System.out.println(base+"      "+S_up(pG,candidate_set.get(i)));
                if(S_up(pG,candidate_set.get(i))<base){
                    removeList.add(candidate_set.get(i));
                    candidate_set.remove(i);
                }
            }
            System.out.println(removeList);
            return false;
        }
        else
        {
            return true;
        }
    }


            //利用fai_down中de值快速得到
            /*public static double fai_up ( ListUDG pG,int v1, int v2, int iterNum){
                if (iterNum == 0) {
                    if (v1 == v2)
                        return 1;
                    else
                        return N*alpha;
                }
                //迭代次序不等于0的情况
                if(fai_down(pG,v1,v2,iterNum)>0)
                    return fai_down(pG,v1,v2,iterNum);
                else
                    return N*Math.pow(alpha,iterNum+1);
            }*/

            //这个里面的操作会不断迭代 那么迭代种植的条件是什么
            //需要设置一个值来保存上一次的值
            /*public static double fai_down ( ListUDG pG,int v1, int v2, int iterNum){

                double res=0;
                if (iterNum == 0) {
                    if (v1 == v2)
                        return 1;
                    else
                        return 0;
                }
                ArrayList<Integer> temp =bfs(pG,v1,iterNum);
                //System.out.println(temp);
                if(temp.contains(v2)){   //如果包含  则进行计算
                    Character  data=pG.getNodeData(v2);
                    if(fai_down(pG,v1,v2,(iterNum-1))>0){   //这里存在问题 什么时候该进入判断 什么时候不应该
                        return fai_down(pG,v1,v2,(iterNum-1));
                    }
                    else{
                        ArrayList<Integer> temp1=pG.getAdjacentList(v2);
                        //加一个判断
                        for (int i = 0; i < temp1.size(); i++) {
                            if(temp.contains(temp1.get(i))){
                                res+=fai_down(pG,v1,temp1.get(i),(iterNum-1));
                            }
                        }
                        return alpha*res;
                    }
                }else{
                    return 0;
                }
            }*/

            //广度优先   源点也要加进去
            //每个specific node 是一个对象 对象有自己的一些属性和常量

            /*public static  ArrayList<Integer> bfs (ListUDG pG ,int nodeNum,int iterNum){
                //ListUDG pG;
                //迭代的时候通过大小的索引来


                ArrayList<Integer> temp,iter = new ArrayList<>();
                ArrayList<Integer> start = new ArrayList<>();
                start.add(nodeNum);
                while(iterNum>0){
                    //System.out.println(start.size());
                    int k=start.size();
                    for (int i = 0; i < k; i++) { // 迭代次数注意
                        temp=pG.getAdjacentList(start.get(i));  //获得邻居
                        //System.out.println(temp);

                        //加入到大队列中去
                        for (int j = 0; j < temp.size(); j++) {
                            if(iter.isEmpty()||!(iter.contains(temp.get(j)))){
                                iter.add(temp.get(j));
                            }
                        }
                    }
                    start = iter;  //这里start成为了iter的引用 就是副本 所以上述for循环中的循环次数一直在变化
                    --iterNum;

                }
                iter.add(nodeNum);
                return iter;
            }*/

            /*public static double S_up(ListUDG pG,int v){   //要不要把迭代轮次带进来
                double res=0;
                for (int i:specific_iterNum.keySet()) {
                    int iterNum=specific_iterNum.get(i);    //获得迭代轮次
                    res+=fai_up(pG,i,v,iterNum);
                }
                return res;
            }*/

        /*
            public static double S_down(ListUDG pG,int v){   //要不要把迭代轮次带进来
                double res=0;
                for (int i:specific_iterNum.keySet()) {
                    int iterNum=specific_iterNum.get(i);
                    res+=fai_down(pG,i,v,iterNum);
                }
                return res;
            }
        */

            // 计算P(Rv1)
        /*    public static  double count_P(ListUDG pG,int v){
                double res =0;
                int iterNum=specific_iterNum.get(v);
                //两层循环
                for (int i:candidate_set) {
                    res+=fai_up(pG,v,i,iterNum)-fai_down(pG,v,i,iterNum);
                }
                return res;
            }*/

            /*public static boolean emergency_test(ListUDG pG){
                if(candidate_set.size()>K){
                    ArrayList<Double> s_Down=new ArrayList<>();
                    for (int j:candidate_set) {
                        s_Down.add(S_down(pG,j));
                    }
                    Collections.sort(s_Down);
                    double base=s_Down.get(K-1);
                    for (int i = 0; i <candidate_set.size();i++) {
                        if(S_up(pG,candidate_set.get(i))<base){
                            System.out.println(candidate_set.get(i));
                            candidate_set.remove(i);
                        }
                    }
                    return false;
                }
                else
                {
                    return true;
                }
            }*/

            //返回 哪个顶点迭代
        /*    public static int arrangement(ListUDG pG){
                int arrange=-1;
                for (int i:specific_iterNum.keySet()) {
                    if(count_P(pG,i)>arrange){
                        arrange=i;
                    }
                }
                return arrange;
            }*/
}

