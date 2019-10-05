package com.pixel.view;

import com.pixel.controller.UserController;
import com.pixel.dao.postgresql.PostgreSQLJDBC;
import com.pixel.dao.postgresql.implementations.SessionDAOI;
import com.pixel.dao.postgresql.implementations.UserDAOI;
import com.pixel.helper.Common;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Login implements HttpHandler {
    private SessionDAOI sessionDAOI;
    private Common common;

    public Login() {
        common = new Common();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        PostgreSQLJDBC postgreSQLJDBC = new PostgreSQLJDBC();
        Connection connection = postgreSQLJDBC.getConnection();
        sessionDAOI = new SessionDAOI(connection);
        UserController userController = new UserController(new UserDAOI(connection));
        CookieHandler cookieHandler = new CookieHandler();
        String response = "";
        String method = httpExchange.getRequestMethod();
        Optional<HttpCookie> cookie = cookieHandler.getCookie(httpExchange, "sessionId");
        HttpCookie cookies;


        if (method.equals("GET")) {
            if (cookie.isPresent()) {
                try {
                    int userId = sessionDAOI.getUserId(cookieHandler.extractCookieToString(cookie));
                    if (sessionDAOI.isCurrentSession(cookieHandler.extractCookieToString(cookie))) {
                        redirectUser(httpExchange, userController, response, userId);

                    } else {
                        response = common.getLoginTemplate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                response = common.getLoginTemplate();
            }
        }


        if (method.equals("POST")) {


            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map inputs = common.parseFormData(formData);
            String username = String.valueOf(inputs.get("username"));
            String password = String.valueOf(inputs.get("password"));
            try {
                if (userController.checkIfPasswordIsCorrect(username, password)) {
                    int userId = 0;
                    //todo nie zwracac -1 dodatkowa metoda czy user istnieje
                    cookies = new HttpCookie("sessionId", generateSessionID());
                    httpExchange.getResponseHeaders().add("Set-Cookie", cookies.toString());
                    try {
                        userId = userController.getUserIdFromCredentials(username, password);
                        sessionDAOI.createSession(cookies.getValue(), userId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    redirectUser(httpExchange, userController, response, userId);


                } else {
                    response = common.getLoginTemplate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }

        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void redirectUser(HttpExchange httpExchange, UserController userController, String response, int userId) throws SQLException, IOException {
        if (userController.checkUserRank(userId).equals("creep")) {
            httpExchange.getResponseHeaders().set("Location", "/creep");
            httpExchange.sendResponseHeaders(303, response.getBytes().length);
        } else if (userController.checkUserRank(userId).equals("mentor")){
            httpExchange.getResponseHeaders().set("Location", "/mentor");
            httpExchange.sendResponseHeaders(303, response.getBytes().length);
        } else {
            httpExchange.getResponseHeaders().set("Location", "/");
            httpExchange.sendResponseHeaders(303, response.getBytes().length);
        }
    }


    private String generateSessionID() {
        //todo poczytac o uuid
        UUID generatedId = UUID.randomUUID();
        return generatedId.toString();
    }


}
