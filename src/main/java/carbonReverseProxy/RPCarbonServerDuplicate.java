package carbonReverseProxy;

import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.exceptions.ServerConnectorException;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contract.config.ListenerConfiguration;
import org.wso2.transport.http.netty.contract.config.SenderConfiguration;
import org.wso2.transport.http.netty.contract.config.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;
import org.wso2.transport.http.netty.message.HttpMessageDataStreamer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Carbon Server
 */
public class RPCarbonServerDuplicate {
    public static void main(String[] args) {

        final Executor executor = Executors.newFixedThreadPool(100);

//        HttpWsConnectorFactory httpWsConnectorFactory = new DefaultHttpWsConnectorFactory(8, 16, 8);
        HttpWsConnectorFactory httpWsConnectorFactory = new DefaultHttpWsConnectorFactory(8, 16, 16);

//        final StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < 9000 * 4; i++) {
//            stringBuilder.append("x");
//        }

        final ServerConnector serverConnector = createServerConnector(httpWsConnectorFactory);
        final HttpClientConnector clientConnector = createClientConnector(httpWsConnectorFactory);

        ServerConnectorFuture serverConnectorFuture = serverConnector.start();
        System.out.println("Server connector started.. ");
        serverConnectorFuture.setHttpConnectorListener(new HttpConnectorListener() {
            @Override
            public void onMessage(final HttpCarbonMessage inboundRequestMsg) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final HttpMessageDataStreamer inboundReqDataStreamer =
                                new HttpMessageDataStreamer(inboundRequestMsg);
                        String inboundReqContent;
                        try {
                            inboundReqContent = getStringFromInputStream(inboundReqDataStreamer.getInputStream());
                            System.out.println("Inbound request payload " + inboundReqContent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        HttpCarbonRequest outboundRequest = new HttpCarbonRequest(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/"));
//                        outboundRequest.setProperty("HOST", "127.0.0.1");
//                        outboundRequest.setProperty("PORT", 8688);
//                        outboundRequest.setProperty("HTTP_METHOD", "POST");
//                        outboundRequest.setHeader("Content-Type", "application/xml");
//                        outboundRequest.completeMessage();

//                        HttpCarbonRequest outboundRequest = new HttpCarbonRequest(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/"));
//                        outboundRequest.setProperty("host", "127.0.0.1");
//                        outboundRequest.setProperty("port", 43665);
//                        outboundRequest.setProperty("http_method", "POST");
//                        outboundRequest.setHeader("content-type", "application/xml");
//                        outboundRequest.completeMessage();

//                        outboundRequest.waitAndReleaseAllEntities();

//                        final HttpMessageDataStreamer outboundReqStream = new HttpMessageDataStreamer(outboundRequest);
//                        OutputStream outputStream = outboundReqStream.getOutputStream();
//                        final StringDataSource outboundReqPayload = new StringDataSource("<Ballerina>apple</Ballerina>", outputStream);

                        inboundRequestMsg.setProperty("port", 8688);
                        inboundRequestMsg.setProperty("host", "127.0.0.1");

                        HttpResponseFuture responseFuture = clientConnector.send(inboundRequestMsg);
                        responseFuture.setHttpConnectorListener(new HttpConnectorListener() {
                            @Override
                            public void onMessage(final HttpCarbonMessage inboundRespMsg) {
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
//                                            HttpMessageDataStreamer inboundResDataStreamer = new HttpMessageDataStreamer(inboundRespMsg);
//                                            String inboundRespContent = getStringFromInputStream(inboundResDataStreamer.getInputStream());
////                                            System.out.println(inboundRespContent);
//
//                                            inboundRespMsg.waitAndReleaseAllEntities();
//
//                                            HttpCarbonResponse outboundRespMsg =
//                                                    new HttpCarbonResponse(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
//                                            outboundRespMsg.completeMessage();
//
//                                            outboundRespMsg.waitAndReleaseAllEntities();
//
//                                            StringDataSource outboundRespDataStream = new StringDataSource("orange");
//                                            outboundRespDataStream.setOutputStream(new HttpMessageDataStreamer(outboundRespMsg).getOutputStream());
                                            inboundRequestMsg.respond(inboundRespMsg);

//                                            outboundRespDataStream.serializeData();
                                        } catch (ServerConnectorException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable throwable) {
//                                outboundReqStream.setIoException((IOException) throwable);
                            }
                        });

//                        outboundReqPayload.serializeData();
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
//        serverConnectorFuture.setWSConnectorListener(new WebSocketConnectorListener() {
//            @Override
//            public void onMessage(final WebSocketInitMessage webSocketInitMessage) {
//                final CountDownLatch latch = new CountDownLatch(1);
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            webSocketInitMessage.handshake();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onMessage(WebSocketTextMessage webSocketTextMessage) {
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println("text-message");
//                    }
//                });
//            }
//
//            @Override
//            public void onMessage(WebSocketBinaryMessage webSocketBinaryMessage) {
//
//            }
//
//            @Override
//            public void onMessage(WebSocketControlMessage webSocketControlMessage) {
//
//            }
//
//            @Override
//            public void onMessage(WebSocketCloseMessage webSocketCloseMessage) {
//
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//
//            }
//
//            @Override
//            public void onIdleTimeout(WebSocketControlMessage webSocketControlMessage) {
//
//            }
//        });
    }

    private static HttpClientConnector createClientConnector(HttpWsConnectorFactory httpWsConnectorFactory) {
        SenderConfiguration senderConfiguration = new SenderConfiguration();
        senderConfiguration.setSocketIdleTimeout(30000);
        return httpWsConnectorFactory
                .createHttpClientConnector(new HashMap<String, Object>(), senderConfiguration);
    }

    private static ServerConnector createServerConnector(HttpWsConnectorFactory httpWsConnectorFactory) {
        ServerBootstrapConfiguration serverBootstrapConfiguration =
                new ServerBootstrapConfiguration(new HashMap<String, Object>());
        ListenerConfiguration listenerConfiguration = new ListenerConfiguration("xxx", "localhost", 9090);
        return httpWsConnectorFactory
                .createServerConnector(serverBootstrapConfiguration, listenerConfiguration);
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String result;
        try {
            int data;
            while ((data = bis.read()) != -1) {
                bos.write(data);
            }
            result = bos.toString();
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            try {
                bos.close();
            } catch (IOException ignored) {
            }
        }
        return result;
    }
}
