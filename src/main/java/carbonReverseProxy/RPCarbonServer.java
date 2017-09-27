package carbonReverseProxy;

import org.wso2.carbon.transport.http.netty.config.ListenerConfiguration;
import org.wso2.carbon.transport.http.netty.config.SenderConfiguration;
import org.wso2.carbon.transport.http.netty.contract.HttpClientConnector;
import org.wso2.carbon.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.carbon.transport.http.netty.contract.HttpResponseFuture;
import org.wso2.carbon.transport.http.netty.contract.ServerConnector;
import org.wso2.carbon.transport.http.netty.contract.ServerConnectorException;
import org.wso2.carbon.transport.http.netty.contract.ServerConnectorFuture;
import org.wso2.carbon.transport.http.netty.contractimpl.HttpWsConnectorFactoryImpl;
import org.wso2.carbon.transport.http.netty.listener.ServerBootstrapConfiguration;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;

import java.util.HashMap;

/**
 * Carbon Server
 */
public class RPCarbonServer {
    public static void main(String[] args) {

        HttpWsConnectorFactoryImpl httpWsConnectorFactory = new HttpWsConnectorFactoryImpl();

        ServerBootstrapConfiguration serverBootstrapConfiguration = ServerBootstrapConfiguration.getInstance();
        ListenerConfiguration listenerConfiguration = new ListenerConfiguration("xxx", "localhost", 9090);

        ServerConnector serverConnector = httpWsConnectorFactory
                .createServerConnector(serverBootstrapConfiguration, listenerConfiguration);
        final HttpClientConnector clientConnector = httpWsConnectorFactory
                .createHttpClientConnector(new HashMap<String, Object>(), new SenderConfiguration());

        ServerConnectorFuture serverConnectorFuture = serverConnector.start();
        System.out.println("Server connector started.. ");
        serverConnectorFuture.setHttpConnectorListener(new HttpConnectorListener() {
            @Override
            public void onMessage(final HTTPCarbonMessage httpRequestMessage) {

                httpRequestMessage.setProperty("HOST", "localhost");
                httpRequestMessage.setProperty("PORT", 8688);
                httpRequestMessage.setProperty("HTTP_METHOD", "POST");

                HttpResponseFuture responseFuture = clientConnector.send(httpRequestMessage);
                responseFuture.setHttpConnectorListener(new HttpConnectorListener() {
                    @Override
                    public void onMessage(HTTPCarbonMessage httpCarbonMessage) {
                        try {
                            httpRequestMessage.respond(httpCarbonMessage);
                        } catch (ServerConnectorException e) {
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

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
