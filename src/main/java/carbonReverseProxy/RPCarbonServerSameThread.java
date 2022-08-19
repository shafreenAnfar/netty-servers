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

/**
 * Carbon Server
 */
public class RPCarbonServerSameThread {
    public static void main(String[] args) {

        HttpWsConnectorFactory httpWsConnectorFactory = new DefaultHttpWsConnectorFactory(8, 50, 50);

        final ServerConnector serverConnector = createServerConnector(httpWsConnectorFactory);
        final HttpClientConnector clientConnector = createClientConnector(httpWsConnectorFactory);

        ServerConnectorFuture serverConnectorFuture = serverConnector.start();
        System.out.println("Server connector started on IO threads ... ");
        serverConnectorFuture.setHttpConnectorListener(new HttpConnectorListener() {
            @Override
            public void onMessage(final HttpCarbonMessage inboundRequestMsg) {
                inboundRequestMsg.setProperty("port", 8688);
                inboundRequestMsg.setProperty("host", "127.0.0.1");

                HttpResponseFuture responseFuture = clientConnector.send(inboundRequestMsg);
                responseFuture.setHttpConnectorListener(new HttpConnectorListener() {
                    @Override
                    public void onMessage(final HttpCarbonMessage inboundRespMsg) {
                        try {
                            inboundRequestMsg.respond(inboundRespMsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
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
