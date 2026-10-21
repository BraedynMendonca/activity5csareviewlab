import java.io.File;
import java.util.Scanner;

/**
 * Counts positive, negative, and neutral review sentiment.
 */
public class ReviewRunner {

  public static void main(String[] args) {
    int positiveReviews = 0;
    int negativeReviews = 0;
    int neutralReviews = 0;
    int totalReviews = 0;

    try {
      Scanner input = new Scanner(new File("reviewTexts.txt"));

      while (input.hasNextLine()) {
        String reviewText = input.nextLine();

        if (reviewText.trim().length() > 0) {
          double score = getReviewScore(reviewText);
          totalReviews++;

          if (score > 0) {
            positiveReviews++;
          } else if (score < 0) {
            negativeReviews++;
          } else {
            neutralReviews++;
          }
        }
      }

      input.close();

      System.out.println("Question: What percentage of reviews have positive, negative, or neutral sentiment?");
      System.out.println("Reviews analyzed: " + totalReviews);

      if (totalReviews > 0) {
        double positivePercent = positiveReviews * 100.0 / totalReviews;
        double negativePercent = negativeReviews * 100.0 / totalReviews;
        double neutralPercent = neutralReviews * 100.0 / totalReviews;

        System.out.println("Positive: " + positiveReviews + " (" + positivePercent + "%)");
        System.out.println("Negative: " + negativeReviews + " (" + negativePercent + "%)");
        System.out.println("Neutral: " + neutralReviews + " (" + neutralPercent + "%)");
      }
    } catch (Exception e) {
      System.out.println("Could not read reviewTexts.txt");
    }
  }

  /**
   * Adds the sentiment values of the words in one review.
   * @param reviewText the text of one customer review
   * @return the sum of the review's word sentiment values
   */
  public static double getReviewScore(String reviewText) {
    String[] words = reviewText.toLowerCase().split(" ");
    double score = 0;

    for (int i = 0; i < words.length; i++) {
      String word = Review.removePunctuation(words[i]);

      if (word.length() > 0) {
        score += Review.sentimentVal(word);
      }
    }

    return score;
  }
}
