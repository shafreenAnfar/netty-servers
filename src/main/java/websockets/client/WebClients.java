package websockets.client;

import java.util.concurrent.CountDownLatch;

public class WebClients implements Runnable {

    private CountDownLatch countDownLatch;

    public WebClients(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        WebSocketClient webSocketClient = new WebSocketClient("ws://10.100.5.38:9090/connector1/ws");
        try {
            webSocketClient.open();
            for (int i = 0; i < 10; i++) {
//            for (;;) {
                webSocketClient.eval("Test");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
