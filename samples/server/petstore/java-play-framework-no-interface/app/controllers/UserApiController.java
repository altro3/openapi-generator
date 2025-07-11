package controllers;

import java.time.OffsetDateTime;
import apimodels.User;

import com.typesafe.config.Config;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import java.io.File;
import play.libs.Files.TemporaryFile;
import openapitools.OpenAPIUtils;
import openapitools.SecurityAPIUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.validation.constraints.*;
import javax.validation.Valid;
import com.typesafe.config.Config;

import openapitools.OpenAPIUtils.ApiAction;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaPlayFrameworkCodegen", comments = "Generator version: 7.15.0-SNAPSHOT")
public class UserApiController extends Controller {
    private final UserApiControllerImp imp;
    private final ObjectMapper mapper;
    private final Config configuration;
    private final SecurityAPIUtils securityAPIUtils;

    @Inject
    private UserApiController(Config configuration, UserApiControllerImp imp, SecurityAPIUtils securityAPIUtils) {
        this.imp = imp;
        mapper = new ObjectMapper();
        this.configuration = configuration;
        this.securityAPIUtils = securityAPIUtils;
    }

    @ApiAction
    public Result createUser(Http.Request request) throws Exception {
        JsonNode nodebody = request.body().asJson();
        User body;
        if (nodebody != null) {
            body = mapper.readValue(nodebody.toString(), User.class);
            if (configuration.getBoolean("useInputBeanValidation")) {
                OpenAPIUtils.validate(body);
            }
        } else {
            throw new IllegalArgumentException("'body' parameter is required");
        }
                imp.createUser(request, body);
        return ok();

    }

    @ApiAction
    public Result createUsersWithArrayInput(Http.Request request) throws Exception {
        JsonNode nodebody = request.body().asJson();
        List<@Valid User> body;
        if (nodebody != null) {
            body = mapper.readValue(nodebody.toString(), new TypeReference<List<@Valid User>>(){});
            if (configuration.getBoolean("useInputBeanValidation")) {
                for (User curItem : body) {
                    OpenAPIUtils.validate(curItem);
                }
            }
        } else {
            throw new IllegalArgumentException("'body' parameter is required");
        }
                imp.createUsersWithArrayInput(request, body);
        return ok();

    }

    @ApiAction
    public Result createUsersWithListInput(Http.Request request) throws Exception {
        JsonNode nodebody = request.body().asJson();
        List<@Valid User> body;
        if (nodebody != null) {
            body = mapper.readValue(nodebody.toString(), new TypeReference<List<@Valid User>>(){});
            if (configuration.getBoolean("useInputBeanValidation")) {
                for (User curItem : body) {
                    OpenAPIUtils.validate(curItem);
                }
            }
        } else {
            throw new IllegalArgumentException("'body' parameter is required");
        }
                imp.createUsersWithListInput(request, body);
        return ok();

    }

    @ApiAction
    public Result deleteUser(Http.Request request, String username) throws Exception {
                imp.deleteUser(request, username);
        return ok();

    }

    @ApiAction
    public Result getUserByName(Http.Request request, String username) throws Exception {
                User obj = imp.getUserByName(request, username);

        if (configuration.getBoolean("useOutputBeanValidation")) {
            OpenAPIUtils.validate(obj);
        }

        JsonNode result = mapper.valueToTree(obj);

        return ok(result);

    }

    @ApiAction
    public Result loginUser(Http.Request request) throws Exception {
        String valueusername = request.getQueryString("username");
        String username;
        if (valueusername != null) {
            username = valueusername;
        } else {
            throw new IllegalArgumentException("'username' parameter is required");
        }
        String valuepassword = request.getQueryString("password");
        String password;
        if (valuepassword != null) {
            password = valuepassword;
        } else {
            throw new IllegalArgumentException("'password' parameter is required");
        }
                String obj = imp.loginUser(request, username, password);
        JsonNode result = mapper.valueToTree(obj);

        return ok(result);

    }

    @ApiAction
    public Result logoutUser(Http.Request request) throws Exception {
                imp.logoutUser(request);
        return ok();

    }

    @ApiAction
    public Result updateUser(Http.Request request, String username) throws Exception {
        JsonNode nodebody = request.body().asJson();
        User body;
        if (nodebody != null) {
            body = mapper.readValue(nodebody.toString(), User.class);
            if (configuration.getBoolean("useInputBeanValidation")) {
                OpenAPIUtils.validate(body);
            }
        } else {
            throw new IllegalArgumentException("'body' parameter is required");
        }
                imp.updateUser(request, username, body);
        return ok();

    }

}
