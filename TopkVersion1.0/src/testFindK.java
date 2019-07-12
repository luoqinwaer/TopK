import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;


/**
 * 找出数组中 第K大的元素
 * 各种方法比较
 */
public class testFindK {
    public static void main(String[] args) {
        FormatInput fi =new FormatInput();
        long start=System.nanoTime();
//        System.out.println(straight(fi.vetex,10) );
        System.out.println(heapFinaMax(fi.vetex,10) );
        long end=System.nanoTime();
        System.out.println(end-start);
    }


    //直接排序  然后找出第K个  16392253
    public static int straight(ArrayList<Integer> arr,int k){
        Collections.sort(arr);      //默认升序   用归并排序实现
        return arr.get(arr.size()-k);
    }

    //冒泡排序完成后 获取  7666185861
    public static int bumbSort(ArrayList<Integer> arr,int k){
        int temp=0;
        for (int i = 0; i < arr.size(); i++) {
            for(int j=i;j<arr.size()-1;j++){
                if(arr.get(j)>arr.get(j+1)){
                    temp =arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1,temp);
                }
            }
        }
        return arr.get(arr.size()-k);
    }

    // 仅排序部分    23620558
    public static int bumbPartSort(ArrayList<Integer> arr,int k){
        int temp=0;
        for (int i = 0; i < k+1; i++) {
            for(int j=i;j<arr.size()-1;j++){
                if(arr.get(j)>arr.get(j+1)){
                    temp =arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1,temp);
                }
            }
        }
        return arr.get(arr.size()-k);
    }

    //快速排序 7859202
    public static int divide(ArrayList<Integer> arr,int low,int high){
        Random random =new Random();
        //int idx=random.nextInt(arr.size()-1);     //为社么这里用随机数 不可以
        int idx=(high+low)/2;
        int temp=arr.get(low);
        arr.set(low,arr.get(idx));
        arr.set(idx,temp);

        temp=arr.get(low);
        while(low<high){
            while (low<high && arr.get(high) >= temp) {
                high--;
            }
            if (low < high) {
                arr.set(low,arr.get(high));
                low++;
            }
            while (low < high && arr.get(low) <= temp) {
                low++;
            }
            if (low < high) {
                arr.set(high,arr.get(low));
                high--;
            }
        }
        //此时 low 等于 high
        arr.set(high,temp);
        return high;
    }

    public static int findMax(ArrayList<Integer> arr,int low,int high,int k){
        int mid=divide(arr,low,high);
        int rightLength=high-mid+1;
        if(rightLength==k) return arr.get(mid);
        else if(rightLength>k){
            return findMax(arr,mid+1,high,k);
        }
        else
        {
            return findMax(arr,low,mid-1,k-rightLength);
        }
    }

    //利用小顶堆排序 9034527
    public static int heapFinaMax(ArrayList<Integer> arr,int k){
        PriorityQueue<Integer> pq =new PriorityQueue<>();       //建立小顶堆
        for (int val:arr) {
            pq.add(val);
            if(pq.size()>k){
                pq.poll();
            }
        }
        return pq.peek();
    }

}
