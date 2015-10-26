package stink5.oauth2;

import static java.lang.String.*;
import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
//import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class Launcher {

    //private static final HttpHost PROXY = new HttpHost("127.0.0.1", 8081, "http");

    private static final String REPOS_URL = "https://bitbucket.org/api/2.0/repositories";
    private static final String USER_REPOS_URL = "https://bitbucket.org/api/2.0/repositories/%s";
    private static final AuthData AUTH_DATA = authData();

    public static void main(final String[] args) throws Exception {
        // если настройки указаны не все, то отваливаемся
        if (!AUTH_DATA.isValid()) {
            System.out.println("Auth data is not defined.");
            return;
        }
        try (
            CloseableHttpClient client = client();
        ) {
            // шлём запрос на публичный url без авторизации
            System.out.printf("Get repository list:%n\t%s%n", REPOS_URL);
            try (
                CloseableHttpResponse response = client.execute(new HttpGet(REPOS_URL));
            ) {
                System.out.println("Repository list:\n\t");
                System.out.println(EntityUtils.toString(response.getEntity()));
            }

            // шлём запрос на закрытый url

            // формируем url для получения списка репозиториев конкретного пользователя
            final String url = format(USER_REPOS_URL, AUTH_DATA.getUsername());

            System.out.printf("Get repository list of user:%n\t%s%n", url);
            System.out.println("Try to get token");
            // получаем токен из настроек
            String token = AUTH_DATA.getAccessToken();
            // если токена в настройках нет, то
            if (token == null) {
                System.out.println("Token not found. Try get it.");
                // запрашиваем его с сервера
                reloadTokens();
                token = AUTH_DATA.getAccessToken();
                System.out.printf("Received token: %s%n", token);
            }

            // флаг о необходимости обновить токен авторизации
            boolean refreshToken = false;
            do {
                if (true || refreshToken) {
                    System.out.printf("Token %s is expired%n", token);
                    // обновляем токен
                    token = refreshToken();
                    if (token == null) {
                        // если токен не обновился, то просто запрашиваем новый
                        System.out.println("Token isn't refreshed. Try get new.");
                        reloadTokens();
                        token = AUTH_DATA.getAccessToken();
                        if (token == null) {
                            // маловероятный сценарий, добавлен, чтобы не зациклить программу.
                            System.out.println("Can't get a new token. Program will be closed.");
                            return;
                        }
                    } else {
                        System.out.printf("Token is refreshed: %s%n", token);
                    }
                }
                // шлём запрос на закрытый url
                final HttpUriRequest request = RequestBuilder.get(url)
                    .addParameter("access_token", token)
                    .build();

                try (
                    CloseableHttpResponse response = client.execute(request);
                ) {
                    // если ответ содержит ошибку авторизации,
                    // то выставляем флаг о необходимости обновить токен
                    refreshToken = response.getStatusLine().getStatusCode() == 401;
                    if (!refreshToken) {
                        // если токен не надо обновлять, то выводим ответ в консоль.
                        System.out.println("Repository list:\n\t");
                        System.out.println(EntityUtils.toString(response.getEntity()));
                    }
                }
            } while (refreshToken);
        }
    }

    /**
     * Метод создаёт клиента, который посылает/принимает запросы на/с bitbucket.org
     * @return
     * @throws Exception
     */
    private static CloseableHttpClient client() throws Exception {
        final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
            new SSLContextBuilder()
                .loadTrustMaterial(null, new TrustStrategy() {

                    @Override
                    public boolean isTrusted(final X509Certificate[] chain, final String authType)
                        throws CertificateException {
                        return true;
                    }
                })
                .build()
        );
        return HttpClients.custom()
            .setSSLSocketFactory(sslsf)
            //.setProxy(PROXY)
            .build();
    }

    /**
     * Метод получает новый токен для авторизации
     * @return
     * @throws Exception
     */
    private static void reloadTokens() throws Exception {
        final String url = "https://bitbucket.org/site/oauth2/access_token";

        try (
            CloseableHttpClient client = client();
        ) {
            final HttpUriRequest post = RequestBuilder.post(url)
                .addHeader("Authorization", "Basic " + authHeader())
                .addParameter("grant_type", "client_credentials")
                .build();
            final CloseableHttpResponse response = client.execute(post);
            final String entityAsString = EntityUtils.toString(response.getEntity());
            final Map<String, Object> tokens = new Gson().fromJson(entityAsString, Map.class);
            // сохраняем в настройках
            AUTH_DATA.setAccessToken((String) tokens.get("access_token"));
            AUTH_DATA.setRefreshToken((String) tokens.get("refresh_token"));
        }
    }

    /**
     * Метод обновляет просроченный токен авторизации.
     *
     * @param token
     * @return
     * @throws Exception
     */
    private static String refreshToken() throws Exception {
        final String url = "https://bitbucket.org/site/oauth2/access_token";

        try (
            CloseableHttpClient client = client();
            ) {
            final HttpUriRequest post = RequestBuilder.post(url)
                .addHeader("Authorization", "Basic " + authHeader())
                .addParameter("grant_type", "refresh_token")
                .addParameter("refresh_token", AUTH_DATA.getRefreshToken())
                .build();
            final CloseableHttpResponse response = client.execute(post);
            final String entityAsString = EntityUtils.toString(response.getEntity());
            final Map<String, Object> tokens = new Gson().fromJson(entityAsString, Map.class);

            AUTH_DATA.setAccessToken((String) tokens.get("access_token"));
            AUTH_DATA.setRefreshToken((String) tokens.get("refresh_token"));
            return (String) tokens.get("access_token");
        }
    }

    /**
     * Метод создаёт строчку в Base64-кодировке для базовой аутентификации на сервере
     * @return
     */
    private static String authHeader() {
        return Base64.encodeBase64URLSafeString(
            (AUTH_DATA.getClientId() + ':' + AUTH_DATA.getClientSecret()).getBytes(UTF_8)
        );
    }

    /**
     * Создание объекта, содержащего настройки авторизации
     * @return
     */
    private static AuthData authData() {
        final AuthData result = new AuthData();
        try (
            Reader reader = new InputStreamReader(
                Launcher.class.getResourceAsStream("/oauth2.properties"), UTF_8
            )
        ) {
            result.load(reader);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
