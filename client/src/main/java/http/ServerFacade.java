package http;

import com.google.gson.Gson;
import models.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) { serverUrl = url; }

    public AuthData createAccount(UserData body) throws ResponseException {
        var req = createRequest("POST", "/user", body);
        return makeRequest(req, AuthData.class);
    }

    public AuthData login(LoginUserData body) throws ResponseException {
        var req = createRequest("POST", "/session", body);
        return makeRequest(req, AuthData.class);
    }

    private HttpRequest createRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method,
                        body != null ?
                                HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)) :
                                HttpRequest.BodyPublishers.noBody());
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private <T> T makeRequest(HttpRequest request, Class<T> responseClass) throws ResponseException {
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var gson = new Gson();

            if (response.statusCode() / 100 != 2) {
                var body = gson.fromJson(response.body(), ErrorResponse.class);
                throw new ResponseException(body.message(), response.statusCode());
            }

            return gson.fromJson(response.body(), responseClass);
        } catch (IOException | InterruptedException e) {
            throw new ResponseException(e, 0);
        }
    }
}
