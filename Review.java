import java.util.Scanner;
import java.io.File;
import java.util.HashMap;

/**
 * Class that contains helper methods for the Consumer Review Lab.
 */
public class Review {

  private static HashMap<String, Double> sentiment = new HashMap<String, Double>();

  static {
    try {
      Scanner input = new Scanner(new File("cleanSentiment.csv"));
      while (input.hasNextLine()) {
        String[] temp = input.nextLine().split(",");
        if (temp.length == 2) {
          sentiment.put(temp[0].toLowerCase(), Double.parseDouble(temp[1]));
        }
      }
      input.close();
    } catch (Exception e) {
      System.out.println("Error reading or parsing cleanSentiment.csv");
    }
  }

  /**
   * Returns the sentiment value of a word. Unknown words have a value of 0.
   */
  public static double sentimentVal(String word) {
    try {
      return sentiment.get(word.toLowerCase());
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Returns a word without punctuation at the beginning or end.
   */
  public static String removePunctuation(String word) {
    while (word.length() > 0 && !Character.isAlphabetic(word.charAt(0))) {
      word = word.substring(1);
    }
    while (word.length() > 0
        && !Character.isAlphabetic(word.charAt(word.length() - 1))) {
      word = word.substring(0, word.length() - 1);
    }
    return word;
  }
}
