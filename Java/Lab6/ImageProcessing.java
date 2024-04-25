package org.example;
import org.apache.commons.lang3.tuple.Pair;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageProcessing {

    private static final String INPUT_DIRECTORY = "C:\\Users\\Pablo\\Desktop\\Platformy Technologiczne - laborki\\Lab6 - zrownoleglanie operacji\\lab6\\src\\main\\java\\org\\example\\photos\\input";
    private static final String OUTPUT_DIRECTORY = "C:\\Users\\Pablo\\Desktop\\Platformy Technologiczne - laborki\\Lab6 - zrownoleglanie operacji\\lab6\\src\\main\\java\\org\\example\\photos\\output";
    private static final int THREAD_POOL_SIZE = 6;

    public static void main(String[] args) {
        // Start pomiaru czasu
        long startTime = System.currentTimeMillis();

        try {
            List<Path> files;
            Path source = Path.of(INPUT_DIRECTORY);
            try (Stream<Path> stream = Files.list(source)) {
                files = stream.toList();
            } catch (IOException e) {
                System.err.println("Error - nie ma takiego folderu: " + e.getMessage());
                return;
            }

            ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_POOL_SIZE);

            // Przekazujemy kod do metody poprzez wyrazenie lambda
            forkJoinPool.submit(() ->
                    // processImage zwraca parę [nazwa,BufferedImage], dlatego uzywamy flatMap aby
                    // uzyskac strumien par
                    files.parallelStream().flatMap(ImageProcessing::processImage).forEach(pair -> saveImage(pair, OUTPUT_DIRECTORY))
            ).get();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Koniec pomiaru czasu
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Calkowity czas: " + totalTime + " milisekund");
    }

    private static Stream<Pair<String, BufferedImage>> processImage(Path path) {
        try {
            BufferedImage original = ImageIO.read(path.toFile());
            String name = path.getFileName().toString();

            // Zamiana skladowej zielonej z niebieska
            BufferedImage transformedImage = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
            for (int i = 0; i < original.getWidth(); i++) {
                for (int j = 0; j < original.getHeight(); j++) {
                    int rgb = original.getRGB(i, j);
                    Color color = new Color(rgb);
                    int red = color.getRed();
                    int blue = color.getBlue();
                    int green = color.getGreen();
                    Color outColor = new Color(red, blue, green);
                    transformedImage.setRGB(i, j, outColor.getRGB());
                }
            }

            return Stream.of(Pair.of(name, transformedImage));
        } catch (IOException e) {
            System.err.println("Error: " + path.toString());
            return Stream.empty();
        }
    }

    private static void saveImage(Pair<String, BufferedImage> pair, String outputDirectory) {
        String filename = pair.getLeft();
        BufferedImage image = pair.getRight();
        try {
            File outputFile = new File(outputDirectory, filename);
            ImageIO.write(image, "png", outputFile);
            System.out.println("Zapisano zdjecie: " + filename);
        } catch (IOException e) {
            System.err.println("Error: " + filename);
        }
    }
}
