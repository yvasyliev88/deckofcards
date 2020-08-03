package com.deckofcards.utils;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiClient {

    private static ApiClient client = null;

    private ApiClient() {
        httpRequest = (FilterableRequestSpecification) given().urlEncodingEnabled(false).contentType(ContentType.JSON).log().uri().filter(new AllureRestAssured());
    }

    public static ApiClient getInstance() {

        if (client == null)
            client = new ApiClient();

        return client;
    }

    private FilterableRequestSpecification httpRequest;
    private static Response response;

    /**
     * send GET with path and request parameters
     * @param url - URL of API endpoint
     * @param pathParams - map of path params, in form of key/value pairs,
     * @param reqParams - map of request params, in form of key/value pairs,
     * @return Response object instance
     */
    public Response sendGet(String url, Map<String, Object> pathParams, Map<String, Object> reqParams) {

        if (pathParams != null && !pathParams.isEmpty()) {
            httpRequest.pathParams(pathParams);
        }

        if (reqParams != null && !reqParams.isEmpty()) {
            httpRequest.params(reqParams);
        }

        response =
                given().
                        spec(httpRequest).
                        when().
                        get(url).
                        then().
                        log().ifValidationFails().
                        extract().response();
        return response;
    }

    /**
     * Validate that response status code matches to expected value and response body is non-empty
     * @param statusCode - expected status code
     * @return Response object instance
     */
    public Response validateResponse(int statusCode) {
        return
                response.
                        then().
                        log().ifValidationFails().
                        statusCode(statusCode).
                        and().
                        body("", allOf(notNullValue(), not(""))).
                        extract().
                        response();
    }

    /**
     * Get the Response data as string
     * @return string
     */
    public String getJsonAsString() {
        return response.body().asString();
    }

    /**
     * Get the response body and map it to a Java object
     * (in other words performs deserialization of JSON string into instance of POJO class)
     * @param cls - class name of java object, i.e. CardBalanceResponse.class
     * @return object instance of POJO class
     */
    public <T> T getJsonAsObject(Class<T> cls) {
        return response.body().as(cls, ObjectMapperType.GSON);
    }

    /**
     * Get a JsonPath view of the response body. This will let you use the JsonPath syntax to get values from the response.
     * @return JsonPath instance
     */
    public JsonPath getJsonPath() {
        String json = response.asString();
        return new JsonPath(json);
    }

    /**
     * Get value of specific field from response,
     * @param jsonLocator - json path for target field, i.e. "id"
     * @return value if target field
     */
    public String getJsonValue(String jsonLocator) {
        String jsonValue;
        try {
            jsonValue = getJsonPath().get(jsonLocator).toString(); }
        catch (NullPointerException npe) {
            throw new IllegalArgumentException(String.format("Response does not contain field %s", jsonLocator));
        }
        return jsonValue;
    }

    /**
     * Get size of element's list in response
     * @param jsonLocator - json path for target element's list
     * @return int - count of elements in target list
     */
    public int getJsonArraySize(String jsonLocator) {
        JsonPath jsonValue = getJsonPath();
        return jsonValue.getList(jsonLocator).size();
    }

    public void setHeader(String headerName, Object headerValue) {
        httpRequest.header(headerName, headerValue);
    }

    public void setContentType(ContentType contentType) {
        httpRequest.contentType(contentType);
    }

    public void removeHeaders() {
        httpRequest.removeHeaders();
    }

    public void removeRequestParams() {
        final List<String> params = httpRequest.getRequestParams().keySet().stream().collect(Collectors.toList());

        if (!params.isEmpty()) {
            for (String param : params) {
                httpRequest.removeParam(param);
            }
        }
    }

    public void removePathParams() {
        Set<String> params = httpRequest.getPathParams().keySet();
        if (!params.isEmpty()) {
            for (String param : params) {
                httpRequest.removePathParam(param);
            }
        }
    }
}
