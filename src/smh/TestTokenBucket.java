package smh;

import java.util.concurrent.TimeUnit;

/**
 * Created by smh on 2019/8/1.
 */
public class TestTokenBucket {
    public static void main(String[] args) {
        final TokenBucket tokenBucket=new TokenBucket(100);
        //provider 每100ms加一次令牌 一次加rate个
        new Thread(()-> {
            while(true){
                tokenBucket.add();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"provider").start();

        for(int i=0;i<5;i++){
            String name="consumer"+i;
            new Thread(()-> {
                while(true){
                    tokenBucket.take();
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },name).start();
        }

    }
}
