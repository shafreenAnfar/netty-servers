package websockets.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args){
//        WebSocketClient webSocketClient = new WebSocketClient("ws://10.100.5.38:9090/connector1/ws");
//        try {
//            webSocketClient.open();
//            for (int i = 0; i < 100; i++) {
//                webSocketClient.eval("Test");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Executor executor = Executors.newCachedThreadPool();

        CountDownLatch countDownLatch = new CountDownLatch(30);
        for (int j = 0; j < 30; j++) {
            executor.execute(new WebClients(countDownLatch));
        }
        try {
            countDownLatch.await();
            System.out.println("===== SUCCESS ====");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
