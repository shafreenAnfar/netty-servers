package carbonReverseProxy;

import org.wso2.transport.http.netty.config.ListenerConfiguration;
import org.wso2.transport.http.netty.config.SenderConfiguration;
import org.wso2.transport.http.netty.contract.HttpClientConnector;
import org.wso2.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.ServerConnector;
import org.wso2.transport.http.netty.contract.ServerConnectorException;
import org.wso2.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;
import org.wso2.transport.http.netty.listener.ServerBootstrapConfiguration;
import org.wso2.transport.http.netty.message.HTTPCarbonMessage;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Carbon Server
 */
public class RPCarbonServer {
    public static void main(String[] args) {

        final Executor executor = Executors.newFixedThreadPool(100);

        HttpWsConnectorFactory httpWsConnectorFactory = new DefaultHttpWsConnectorFactory(8, 16, 16);

        final ServerConnector serverConnector = createServerConnector(httpWsConnectorFactory);
        final HttpClientConnector clientConnector = createClientConnector(httpWsConnectorFactory);

        ServerConnectorFuture serverConnectorFuture = serverConnector.start();
        System.out.println("Server connector started.. ");
        serverConnectorFuture.setHttpConnectorListener(new HttpConnectorListener() {
            @Override
            public void onMessage(final HTTPCarbonMessage inboundRequestMsg) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {

                        inboundRequestMsg.setProperty("port", 8688);
                        inboundRequestMsg.setProperty("host", "127.0.0.1");

                        HttpResponseFuture responseFuture = clientConnector.send(inboundRequestMsg);
                        responseFuture.setHttpConnectorListener(new HttpConnectorListener() {
                            @Override
                            public void onMessage(final HTTPCarbonMessage inboundRespMsg) {
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            inboundRequestMsg.respond(inboundRespMsg);
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
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
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
}
