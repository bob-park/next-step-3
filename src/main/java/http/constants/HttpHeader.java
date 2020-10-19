package http.constants;

public enum HttpHeader {

  /*
   * General Headers
   */
  GENERAL_HEADER_CACHE_CONTROL("Cache-Control"),
  GENERAL_HEADER_CONNECTION("Connection"),
  GENERAL_HEADER_DATE("Date"),
  GENERAL_HEADER_PRAGMA("Pragm"),
  GENERAL_HEADER_TRAILER("Trailer"),
  GENERAL_HEADER_TRANSFER_ENCODING("Transfer-Encoding"),
  GENERAL_HEADER_UPGRADE("Upgrade"),
  GENERAL_HEADER_VIA("Via"),
  GENERAL_HEADER_WARING("Warning"),

  /*
   * Entity Headers
   */
  GENERAL_HEADER_ALLOW("Allow"),
  GENERAL_HEADER_CONTENT_ENCODING("Content-Encoding"),
  GENERAL_HEADER_CONTENT_LANGUAGE("Content-Language"),
  GENERAL_HEADER_CONTENT_LENGTH("Content-Length"),
  GENERAL_HEADER_CONTENT_LOCATION("Content-Location"),
  GENERAL_HEADER_CONTENT_MD5("Content-MD5"),
  GENERAL_HEADER_CONTENT_RANGE("Content-Range"),
  GENERAL_HEADER_CONTENT_TYPE("Content-Type"),
  GENERAL_HEADER_EXPIRES("Expires"),
  GENERAL_HEADER_LAST_MODIFIED("Last-Modified"),
  GENERAL_HEADER_EXTENSION_HEADER("extension-header"),

  /*
   * Request Headers
   */
  REQUEST_HEADER_ACCEPT("Accept"),
  REQUEST_HEADER_ACCEPT_CHARSET("Accept-Charset"),
  REQUEST_HEADER_ACCEPT_ENCODING("Accept-Encoding"),
  REQUEST_HEADER_ACCEPT_LANGUAGE("Accept-Language"),
  REQUEST_HEADER_AUTHORIZATION("Authorization"),
  REQUEST_HEADER_EXPECT("Expect"),
  REQUEST_HEADER_FROM("From"),
  REQUEST_HEADER_HOST("Host"),
  REQUEST_HEADER_IF_MATCH("If-Match"),
  REQUEST_HEADER_IF_MODIFIED_SINCE("If-Modified-Since"),
  REQUEST_HEADER_IF_NONE_MATCH("If-None-Match"),
  REQUEST_HEADER_IF_RANGE("If-Range"),
  REQUEST_HEADER_IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
  REQUEST_HEADER_MAX_FORWARDS("Max-Forwards"),
  REQUEST_HEADER_PROXY_AUTHORIZATION("Proxy-Authorization"),
  REQUEST_HEADER_RANGE("Range"),
  REQUEST_HEADER_TE("TE"),
  REQUEST_HEADER_USER_AGENT("User-Agent"),

  /*
   * Response Headers
   */
  RESPONSE_HEADER_ACCEPT_RANGES("Accept-Ranges"),
  RESPONSE_HEADER_AGE("Age"),
  RESPONSE_HEADER_ETAG("ETag"),
  RESPONSE_HEADER_LOCATION("Location"),
  RESPONSE_HEADER_PROXY_AUTHENTICATE("Proxy-Authenticate"),
  RESPONSE_HEADER_RETRY_AFTER("Retry-After"),
  RESPONSE_HEADER_SERVER("Server"),
  RESPONSE_HEADER_VARY("Vary"),
  RESPONSE_HEADER_WWW_AUTHENTICATE("WWW-Authenticate"),
  ;

  private final String name;

  HttpHeader(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
