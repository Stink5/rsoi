package stink5.oauth2;

import static java.lang.String.*;
import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Класс с настройками авторизации
 *
 */
public class AuthData {

    // имена свойств в файле
    private static final String CLIENT_ID_PROPERTY = "client.id";
    private static final String CLIENT_SECRET_PROPERTY = "client.secret";

    // имя файла с токеном доступа
    private static final String ACCESS_TOKEN_FILE_NAME = "access.token";
    private static final Path ACCESS_TOKEN_FILE = Paths.get(ACCESS_TOKEN_FILE_NAME);

    // имя файла с токеном обновления
    private static final String REFRESH_TOKEN_FILE_NAME = "refresh.token";
    private static final Path REFRESH_TOKEN_FILE = Paths.get(REFRESH_TOKEN_FILE_NAME);

    private final Properties props = new Properties();
    private final Map<Path, String> tokens = new HashMap<>(2, 1F);

    /**
     * Метод загружает файл с настройками авторизации
     * @param reader
     * @throws IOException
     */
    public void load(final Reader reader) throws IOException {
        this.props.load(reader);
    }

    /**
     * Метод возвращает свойство по его имени, если оно имеется в файле
     * @param prop
     * @return
     */
    private String getProperty(final String prop) {
        if (!this.props.containsKey(prop)) {
            throw new IllegalStateException(format("%s not found", prop));
        }
        return this.props.getProperty(prop);
    }

    public String getClientId() {
        return getProperty(CLIENT_ID_PROPERTY);
    }

    public String getClientSecret() {
        return getProperty(CLIENT_SECRET_PROPERTY);
    }

    /**
     * Метод возвращает токен авторизации
     * @return
     */
    public String getAccessToken() {
        return getToken(ACCESS_TOKEN_FILE);
    }

    /**
     * Метод устанавливает в настройках полученный токен авторизации
     * и сохраняет его в файл.
     * @param token
     */
    public void setAccessToken(final String token) {
        setToken(token, ACCESS_TOKEN_FILE);
    }

    /**
     * Метод возвращает токен обновления
     * @return
     */
    public String getRefreshToken() {
        return getToken(REFRESH_TOKEN_FILE);
    }

    /**
     * Метод устанавливает в настройках полученный токен обновления
     * и сохраняет его в файл.
     * @param token
     */
   // public void setRefreshToken(final String token)

    private String getToken(final Path tokenFile) {
        String t = this.tokens.get(tokenFile);
        if (t == null && Files.exists(tokenFile)) {
            // если токен не задан и имеется файл с токеном,
            // то токен будет загружен из файла
            try {
                t = new String(Files.readAllBytes(tokenFile), UTF_8);
                if (t.isEmpty()) {
                    t = null;
                } else {
                    this.tokens.put(tokenFile, t);
                }
            } catch (final IOException e) {
                throw new RuntimeException("can't load token from file", e);
            }
        }
        return t;

    }

    private void setToken(final String token, final Path tokenFile) {
        if (token == null) {
            throw new IllegalArgumentException("token is null");
        }
        try {
            Files.write(tokenFile, token.getBytes(UTF_8));
            this.tokens.put(tokenFile, token);
        } catch (final IOException e) {
            throw new RuntimeException("can't write token to file", e);
        }
    }



}
