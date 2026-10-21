# Consumer Review Lab

## Research question

How does review sentiment in the first 90 days compare with later reviews?

The dataset does not include product prices or launch dates. The program treats each product's earliest recorded review as an estimate of its launch date and compares sentiment for days 0-89 with sentiment for reviews 90 or more days later.

## Result

Using the 34,620 reviews with nonempty text and valid dates in `1429_1.csv`:

| Period | Reviews | Positive | Negative | Neutral |
| --- | ---: | ---: | ---: | ---: |
| First 90 days after earliest recorded review | 3,351 | 88.6% | 11.2% | 0.2% |
| Later reviews | 31,269 | 88.8% | 11.0% | 0.2% |

Positive sentiment was almost the same in both periods, and was slightly lower in the first 90 days. This result is an estimate based on the sentiment word scores, not the reviewer's recorded recommendation.

## Files

- `ReviewRunner.java` contains `main` and analyzes the dataset.
- `Review.java` contains the word-scoring and punctuation helper methods, following the starter-code format.
- `cleanSentiment.csv` is the sentiment dictionary from the starter archive.
- `1429_1.csv` is the review data file from the provided archive.

## Run the program

Keep the four files above in the same folder, then compile and run:

```text
javac Review.java ReviewRunner.java
java ReviewRunner
```

## Activity concepts used

- `ReviewRunner.main` calls methods with parameters, including `getReviewScore(String reviewText)`.
- Sentiment is calculated by looking up the value of each word in `Review.sentimentVal` and adding the values together.
- String methods include `toLowerCase`, `split`, `trim`, `equals`, `equalsIgnoreCase`, `length`, and `substring`.
- `if` and `else if` statements classify positive, negative, and neutral scores.
- `for` and `while` loops process CSV records and words.
