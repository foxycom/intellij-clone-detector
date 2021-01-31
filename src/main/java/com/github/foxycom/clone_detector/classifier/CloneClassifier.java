package com.github.foxycom.clone_detector.classifier;

import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class CloneClassifier {
  private final MultiLayerNetwork model;

  public CloneClassifier(String modelPath) throws IOException {
    model = ModelSerializer.restoreMultiLayerNetwork(modelPath);
  }

  public double run(double[] vector) {
    assert vector.length == 8;

    INDArray ndMatrix = Nd4j.create(new int[] {vector.length}, vector);
    INDArray predicted = model.output(ndMatrix);
    return predicted.getDouble(1);
  }
}
