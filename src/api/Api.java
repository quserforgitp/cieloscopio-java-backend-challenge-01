package api;

import interfaces.DataExtractor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Api<T> {
  private String baseURL;
  private String nameURL;
  private String params;
  private String apiKey;
  private String completeURL;
  private DataExtractor<T> extractor;
  private T extractedData;

  public Api(String baseURL, String nameURL, String params, String apiKey, DataExtractor<T> extractor) {
    this.baseURL = baseURL;
    this.nameURL = nameURL;
    this.params = params;
    this.apiKey = apiKey;
    this.extractor = extractor;
    setCompleteURL(baseURL, nameURL, params, apiKey);
  }

  private void setCompleteURL(String baseURL, String nameURL, String params, String apiKey) {
    this.completeURL = baseURL + nameURL + "?" + params + "&appid=" + apiKey;
  }

  public void fetchAndExtractData() throws IOException, InterruptedException {
    String response = fetchDataFromApi(completeURL);
    this.extractedData = extractor.extract(response);
  }

  private String fetchDataFromApi(String apiURL) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiURL)).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  public T getExtractedData() {
    return extractedData;
  }
}
