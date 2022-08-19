package carbonReverseProxy;

import io.ballerina.stdlib.http.transport.contract.HttpClientConnector;
import io.ballerina.stdlib.http.transport.contract.HttpConnectorListener;
import io.ballerina.stdlib.http.transport.contract.HttpResponseFuture;
import io.ballerina.stdlib.http.transport.contract.HttpWsConnectorFactory;
import io.ballerina.stdlib.http.transport.contract.ServerConnector;
import io.ballerina.stdlib.http.transport.contract.ServerConnectorFuture;
import io.ballerina.stdlib.http.transport.contract.config.ListenerConfiguration;
import io.ballerina.stdlib.http.transport.contract.config.SenderConfiguration;
import io.ballerina.stdlib.http.transport.contract.config.ServerBootstrapConfiguration;
import io.ballerina.stdlib.http.transport.contractimpl.DefaultHttpWsConnectorFactory;
import io.ballerina.stdlib.http.transport.message.HttpCarbonMessage;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Carbon Server
 */
public class HttpsRpcCarbonServer {
    public static void main(String[] args) {

        final Executor executor = Executors.newFixedThreadPool(100);

        HttpWsConnectorFactory httpWsConnectorFactory = new DefaultHttpWsConnectorFactory(8, 16, 16);

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
                                            inboundRequestMsg.respond(inboundRespMsg);
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
        listenerConfiguration.setScheme("https");
        listenerConfiguration.setKeyStorePass("wso2carbon");
        listenerConfiguration.setKeyStoreFile("/home/shaf/Documents/wso2/software/wso2esb-4.9.0/repository/"
                + "resources/security/wso2carbon.jks");
        return httpWsConnectorFactory
                .createServerConnector(serverBootstrapConfiguration, listenerConfiguration);
    }
}
