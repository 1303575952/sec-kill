package smh;

import static java.lang.Thread.currentThread;

/**
 * Created by smh on 2019/8/1.
 */
public class TokenBucket {
    //private int cnt;
    private Integer cnt=0;
    private int rate=2;
    private final int maxCnt;
    private Integer flag=0;
    public TokenBucket(int maxCnt){
        this.maxCnt=maxCnt;
    }

    public void add(){
        synchronized (flag){
            while(cnt>=maxCnt || cnt+rate>=maxCnt){
                try{
                    System.out.println(currentThread().getName()+"---TokenBucket is full");
                    flag.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            System.out.println(currentThread().getName()+"--TokenBucket add "+rate+" token");
            cnt+=rate;
            flag.notify();
        }
    }

    public int take(){
        synchronized (flag){
            while(cnt==0 || cnt-rate<0){
                try{
                    System.out.println(currentThread().getName()+"---TokenBucket is empty");
                    flag.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            cnt-=rate;
            flag.notify();
            System.out.println(currentThread().getName()+"TokenBucket subtract "+rate+" token");
        }

        return rate;
    }
}
