package GUI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NaiveBayesGenderClassifier {
    private int totalSamples = 0;
    private Map<String, Integer> maleFeatureCounts = new HashMap<>();
    private Map<String, Integer> femaleFeatureCounts = new HashMap<>();
    private Map<String, Integer> maleWordCounts = new HashMap<>();
    private Map<String, Integer> femaleWordCounts = new HashMap<>();

    public void train(String gender, String long_hair, String forehead_width_cm, String forehead_height_cm, String nose_wide, String nose_long, String lips_thin, String distance_nose_to_lip_long) {
        totalSamples++;

        // Đếm số lần xuất hiện của các đặc trưng cho từng giới tính
        incrementFeatureCount(gender, long_hair, maleFeatureCounts, femaleFeatureCounts);
        incrementFeatureCount(gender, forehead_width_cm, maleFeatureCounts, femaleFeatureCounts);
        incrementFeatureCount(gender, forehead_height_cm, maleFeatureCounts, femaleFeatureCounts);
        incrementFeatureCount(gender, nose_wide, maleFeatureCounts, femaleFeatureCounts);
        incrementFeatureCount(gender, nose_long, maleFeatureCounts, femaleFeatureCounts);
        incrementFeatureCount(gender, lips_thin, maleFeatureCounts, femaleFeatureCounts);
        incrementFeatureCount(gender, distance_nose_to_lip_long, maleFeatureCounts, femaleFeatureCounts);

        // Đếm số lần xuất hiện của các từ cho từng giới tính
        String[] words = gender.split(" ");
        for (String word : words) {
            if (gender.equalsIgnoreCase("male")) {
                maleWordCounts.put(word, maleWordCounts.getOrDefault(word, 0) + 1);
            } else {
                femaleWordCounts.put(word, femaleWordCounts.getOrDefault(word, 0) + 1);
            }
        }
    }

    public String classify(String long_hair, String forehead_width_cm, String forehead_height_cm, String nose_wide, String nose_long, String lips_thin, String distance_nose_to_lip_long) {
        double maleProbability = calculateGenderProbability("male", long_hair, forehead_width_cm, forehead_height_cm, nose_wide, nose_long, lips_thin, distance_nose_to_lip_long);
        double femaleProbability = calculateGenderProbability("female", long_hair, forehead_width_cm, forehead_height_cm, nose_wide, nose_long, lips_thin, distance_nose_to_lip_long);

        return maleProbability > femaleProbability ? "male" : "female";
    }

    private double calculateGenderProbability(String gender, String long_hair, String forehead_width_cm, String forehead_height_cm, String nose_wide, String nose_long, String lips_thin, String distance_nose_to_lip_long) {
        double probability = 1.0;

        // Tính xác suất dựa trên đặc trưng
        probability *= (double) getFeatureCount(gender, long_hair) / totalSamples;
        probability *= (double) getFeatureCount(gender, forehead_width_cm) / totalSamples;
        probability *= (double) getFeatureCount(gender, forehead_height_cm) / totalSamples;
        probability *= (double) getFeatureCount(gender, nose_wide) / totalSamples;
        probability *= (double) getFeatureCount(gender, nose_long) / totalSamples;
        probability *= (double) getFeatureCount(gender, lips_thin) / totalSamples;
        probability *= (double) getFeatureCount(gender, distance_nose_to_lip_long) / totalSamples;

        // Tính xác suất dựa trên từ vựng
        String[] words = gender.split(" ");
        for (String word : words) {
            if (gender.equalsIgnoreCase("male")) {
                probability *= (double) (maleWordCounts.getOrDefault(word, 0) + 1) / (totalSamples + maleWordCounts.size());
            } else {
                probability *= (double) (femaleWordCounts.getOrDefault(word, 0) + 1) / (totalSamples + femaleWordCounts.size());
            }
        }

        return probability;
    }

    private void incrementFeatureCount(String gender, String feature, Map<String, Integer> maleCounts, Map<String, Integer> femaleCounts) {
        if (gender.equalsIgnoreCase("male")) {
            maleCounts.put(feature, maleCounts.getOrDefault(feature, 0) + 1);
        } else {
            femaleCounts.put(feature, femaleCounts.getOrDefault(feature, 0) + 1);
        }
    }

    private int getFeatureCount(String gender, String feature) {
        return gender.equalsIgnoreCase("male") ? maleFeatureCounts.getOrDefault(feature, 0) : femaleFeatureCounts.getOrDefault(feature, 0);
    }

    public static void main(String[] args) throws IOException {
        NaiveBayesGenderClassifier classifier = new NaiveBayesGenderClassifier();
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\nguye\\Desktop\\imgIphone\\data_train.csv"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String gender = parts[7];
            String long_hair = parts[0];
            String forehead_width_cm = parts[1];
            String forehead_height_cm = parts[2];
            String nose_wide = parts[3];
            String nose_long = parts[4];
            String lips_thin = parts[5];
            String distance_nose_to_lip_long = parts[6];
            classifier.train(gender, long_hair, forehead_width_cm, forehead_height_cm, nose_wide, nose_long, lips_thin, distance_nose_to_lip_long);
        }

        // Test the classifier with a new sample
        String long_hair = "1";
        String forehead_width_cm = "11.4_12.425";
        String forehead_height_cm = "5.1_5.6";
        String nose_wide = "0";
        String nose_long = "1";
        String lips_thin = "0";
        String distance_nose_to_lip_long = "1";

        String predictedGender = classifier.classify(long_hair, forehead_width_cm, forehead_height_cm, nose_wide, nose_long, lips_thin, distance_nose_to_lip_long);
        System.out.println("Predicted gender: " + predictedGender);
    }
}
