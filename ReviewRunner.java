import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Analyzes review sentiment in the first 90 days and in later reviews.
 */
public class ReviewRunner {

  public static void main(String[] args) {
    String fileName = "1429_1.csv";
    ArrayList<ReviewData> reviews = new ArrayList<ReviewData>();
    HashMap<String, LocalDate> firstReviewDates = new HashMap<String, LocalDate>();

    try {
      PushbackReader input = new PushbackReader(new FileReader(fileName), 1);
      String[] headers = readCSVRecord(input);

      int productColumn = findColumn(headers, "id");
      int dateColumn = findColumn(headers, "reviews.date");
      int textColumn = findColumn(headers, "reviews.text");

      if (productColumn == -1 || dateColumn == -1 || textColumn == -1) {
        System.out.println("The CSV needs id, reviews.date, and reviews.text columns.");
        input.close();
        return;
      }

      String[] row;
      while ((row = readCSVRecord(input)) != null) {
        if (row.length > textColumn) {
          String productID = row[productColumn].trim();
          String reviewText = row[textColumn].trim();
          LocalDate reviewDate = getDate(row[dateColumn]);

          if (!productID.equals("") && !reviewText.equals("") && reviewDate != null) {
            double score = getReviewScore(reviewText);
            reviews.add(new ReviewData(productID, reviewDate, score));

            if (!firstReviewDates.containsKey(productID)
                || reviewDate.isBefore(firstReviewDates.get(productID))) {
              firstReviewDates.put(productID, reviewDate);
            }
          }
        }
      }
      input.close();

      int[] first90Days = new int[4];
      int[] laterReviews = new int[4];

      for (int i = 0; i < reviews.size(); i++) {
        ReviewData review = reviews.get(i);
        LocalDate firstDate = firstReviewDates.get(review.productID);
        long daysAfterFirstReview = ChronoUnit.DAYS.between(firstDate, review.date);

        if (daysAfterFirstReview < 90) {
          addReview(first90Days, review.score);
        } else {
          addReview(laterReviews, review.score);
        }
      }

      System.out.println("Question: How does sentiment in the first 90 days compare with later reviews?");
      System.out.println("Products analyzed: " + firstReviewDates.size());
      printResults("First 90 days after the earliest recorded review", first90Days);
      printResults("Later reviews", laterReviews);
      System.out.println();
      System.out.println("Note: The dataset has no launch dates or prices. The earliest recorded review is used as a launch-date estimate.");
    } catch (IOException e) {
      System.out.println("Unable to locate or read " + fileName);
    }
  }

  /** Finds the index of a column in the CSV header. */
  public static int findColumn(String[] headers, String columnName) {
    for (int i = 0; i < headers.length; i++) {
      if (headers[i].trim().equalsIgnoreCase(columnName)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Adds all word sentiment values in a review.
   * @param reviewText the full text of one customer review
   * @return the sum of the sentiment values for its words
   */
  public static double getReviewScore(String reviewText) {
    String[] words = reviewText.toLowerCase().split("\\s+");
    double total = 0;

    for (int i = 0; i < words.length; i++) {
      String word = Review.removePunctuation(words[i]);
      if (word.length() > 0) {
        total += Review.sentimentVal(word);
      }
    }
    return total;
  }

  /** Converts an ISO date or timestamp from the CSV to a LocalDate. */
  public static LocalDate getDate(String dateText) {
    if (dateText.length() >= 10) {
      String dateOnly = dateText.substring(0, 10);
      try {
        return LocalDate.parse(dateOnly);
      } catch (DateTimeParseException e) {
        return null;
      }
    }
    return null;
  }

  /** Adds a review to positive, negative, neutral, and total counts. */
  public static void addReview(int[] counts, double score) {
    counts[3]++;
    if (score > 0) {
      counts[0]++;
    } else if (score < 0) {
      counts[1]++;
    } else {
      counts[2]++;
    }
  }

  /** Prints counts and percentages for one time period. */
  public static void printResults(String period, int[] counts) {
    System.out.println();
    System.out.println(period + ": " + counts[3] + " reviews");
    printPercentage("Positive", counts[0], counts[3]);
    printPercentage("Negative", counts[1], counts[3]);
    printPercentage("Neutral", counts[2], counts[3]);
  }

  /** Prints one sentiment count as a percentage. */
  public static void printPercentage(String label, int count, int total) {
    if (total > 0) {
      double percentage = count * 100.0 / total;
      System.out.printf("%s: %d (%.1f%%)%n", label, count, percentage);
    }
  }

  /** Reads one CSV record, keeping commas and line breaks inside quoted text. */
  public static String[] readCSVRecord(PushbackReader input) throws IOException {
    ArrayList<String> fields = new ArrayList<String>();
    StringBuilder field = new StringBuilder();
    boolean insideQuotes = false;
    boolean foundCharacter = false;
    int nextCharacter;

    while ((nextCharacter = input.read()) != -1) {
      foundCharacter = true;
      char character = (char) nextCharacter;

      if (character == '"') {
        if (insideQuotes) {
          int next = input.read();
          if (next == '"') {
            field.append('"');
          } else {
            insideQuotes = false;
            if (next != -1) {
              input.unread(next);
            }
          }
        } else if (field.length() == 0) {
          insideQuotes = true;
        } else {
          field.append(character);
        }
      } else if (character == ',' && !insideQuotes) {
        fields.add(field.toString());
        field.setLength(0);
      } else if ((character == '\n' || character == '\r') && !insideQuotes) {
        if (character == '\r') {
          int next = input.read();
          if (next != '\n' && next != -1) {
            input.unread(next);
          }
        }
        fields.add(field.toString());
        return fields.toArray(new String[fields.size()]);
      } else {
        field.append(character);
      }
    }

    if (!foundCharacter && field.length() == 0 && fields.size() == 0) {
      return null;
    }
    fields.add(field.toString());
    return fields.toArray(new String[fields.size()]);
  }

  /** Stores the product, date, and score from one review. */
  public static class ReviewData {
    String productID;
    LocalDate date;
    double score;

    public ReviewData(String productID, LocalDate date, double score) {
      this.productID = productID;
      this.date = date;
      this.score = score;
    }
  }
}
