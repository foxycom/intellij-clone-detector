package com.github.foxycom.clone_detector;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Backend {
  private final String url;
  OkHttpClient client = new OkHttpClient();
  ObjectMapper objectMapper = new ObjectMapper();


  public Backend(String url) {
    this.url = url;
  }

  public double[] getResult(double[][] matrix) throws IOException {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("application/json"), matrixToString(matrix));

    Request request = new Builder().url(url).post(requestBody).build();
    Call call = client.newCall(request);

    Response response = call.execute();
    if (!response.isSuccessful()) {
      return new double[matrix.length];
    }

    return objectMapper.readValue(response.body().string(), double[].class);
  }


  private String matrixToString(double[][] matrix) {
    StringBuilder sb = new StringBuilder("[");
    String res = Arrays.stream(matrix).map(Arrays::toString).collect(Collectors.joining(", "));
    sb.append(res).append("]");
    return sb.toString();
  }
}
