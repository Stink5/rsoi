package stink5.oauth2.server;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public abstract class HttpClientServlet extends HttpServlet {

    private static final boolean USE_PROXY = Boolean.getBoolean("use.proxy");

    public static final String URL_PARAM = "url";

    protected final HttpClient client = client();

    private HttpClient client() {
        // Instantiate and configure the SslContextFactory
        final SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true);

        // Instantiate HttpClient with the SslContextFactory
        final HttpClient httpClient = new HttpClient(sslContextFactory);

        if (USE_PROXY) {
            httpClient
                .getProxyConfiguration()
                .getProxies()
                .add(new HttpProxy("localhost", 8081));
        }

        try {
            httpClient.start();
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return httpClient;
    }

    @Override
    public void destroy() {
        try {
            this.client.stop();
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            this.client.destroy();
        }
    }

    protected String getUrl() {
        return getInitParameter(URL_PARAM);
    }

}