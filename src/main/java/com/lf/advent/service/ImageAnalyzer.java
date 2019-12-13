package com.lf.advent.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntBags;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@Setter
public class ImageAnalyzer implements LinesConsumer {

    private static final int width = 25;
    private static final int height = 6;
    private List<Layer> layers = Lists.mutable.empty();
    private Integer checksum;

    @Override
    public void consume(List<String> lines) {
        String rawImage = lines.get(0);
        int length = rawImage.length();
        int layerLenght = width * height;
        int nbrOfLayer = length / layerLenght;
        for (int i = 0; i < nbrOfLayer; i++) {
            String rawLayer = rawImage.substring(i * layerLenght, (i + 1) * layerLenght);
            Layer layer = new Layer(Arrays.stream(rawLayer.split(""))
                                          .filter(s -> !s.isEmpty())
                                          .mapToInt(Integer::parseInt)
                                          .toArray());
            layers.add(layer);
        }

        checksum = layers.stream()
                         .min(Comparator.comparingInt(layer -> layer.numberOf(0)))
                         .map(layer -> layer.numberOf(1) * layer.numberOf(2))
                         .orElse(0);

        log.info("Checksum = {}", checksum);

        int[] image = new int[layerLenght];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int finalI = i;
                int finalJ = j;
                int pixelIJ = layers.stream()
                                    .mapToInt(layer -> layer.getPixel(finalJ, finalI))
                                    .filter(pixel -> pixel != 2)
                                    .findFirst()
                                    .orElse(2);
                System.out.print(pixelIJ == 0 ? " " : pixelIJ);
                image[j + width * i] = pixelIJ;
            }
            System.out.println();
        }
    }

    public static class Layer {
        private int[] layer;
        private final MutableIntBag bag;

        public Layer(int[] layer) {
            this.layer = layer;
            bag = IntBags.mutable.of(layer);
        }

        public int numberOf(int item) {
            return bag.occurrencesOf(item);
        }

        public int getPixel(int x, int y) {
            return layer[x + y * width];
        }
    }
}
