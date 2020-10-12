package framework;

import constants.HttpMethod;

public class RequestMapping {

    private final HttpMethod method;
    private final String path;
    private final byte[] body;

    public RequestMapping(HttpMethod method, String path, byte[] body) {
        this.method = method;
        this.path = path;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public byte[] getBody() {
        return body;
    }
}
