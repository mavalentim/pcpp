//Exercise 10.?
//JSt vers Oct 23, 2023

//install  src/main/resources/english-words.txt
package exercises10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestWordStream {

  public static void main(String[] args) {
    // System.out.println(isPalindrome("anap"));

    String filename = "app\\src\\main\\resources\\english-words.txt";
    System.out.println(readWords(filename).count());

    // printing the first 100 words
    List<String> a = first100(readWords(filename));
    for (String string : a) {
      System.out.println(string);
    }

    // taylor swift stuff
    List<String> b = IdontKnowAboutYou(true, readWords(filename));
    for (String string : b) {
      System.out.println(string);
    }

    List<String> c = IdontKnowAboutYou(false, readWords(filename));
    for (String string : c) {
      System.out.println(string);
    }

    // palindrome stuff

    List<String> d = palindromeFilter(readWords(filename), true);
    for (String string : d) {
      System.out.println(string);
    }
  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      // first create a stream from the reader
      Stream<String> lines = reader.lines();

      return lines;
    } catch (IOException exn) {
      return Stream.<String>empty();
    }
  }

  public static List<String> first100(Stream<String> ogStream) {
    List<String> first100 = ogStream.limit(100).toList();

    return first100;
  }

  // making a predicate
  public static boolean isitThan22(boolean more, String string) {
    if (more) {
      return string.length() > 22;

    } else {
      return string.length() < 22;
    }

  }

  public static List<String> IdontKnowAboutYou(boolean all, Stream<String> ogStream) {
    if (all) {
      List<String> first100 = ogStream
          .filter(x -> isitThan22(true, x))
          .toList();

      return first100;
    } else {
      List<String> first100 = ogStream
          .filter(x -> isitThan22(true, x))
          .limit(1).toList(); // Couldnt find the some operator from f sharp

      return first100;
    }
  }

  public static boolean isPalindrome(String s) {
    char[] characters = s.toCharArray();
    Stack<Character> ba1 = new Stack<>();

    for (int i = 0; i < characters.length; i++) {
      ba1.push(characters[i]);

    }

    char[] inverted = new char[characters.length];

    for (int i = 0; i < characters.length; i++) {
      inverted[i] = ba1.pop();
    }

    boolean result = inverted.equals(characters);

    return result;
    /*
     * char head = characters[0];
     * char tail = characters[characters.length];
     * 
     * if (head == tail) {
     * 
     * 
     * } else {
     * return false;
     * }
     * 
     * return false;
     */

  }

  public static List<String> palindromeFilter(Stream<String> ogStream, boolean isParallel) {
    if (isParallel) {
      List<String> first100 = ogStream
          .parallel()
          .filter(x -> isPalindrome(x)).toList();

      return first100;
    } else {
      List<String> first100 = ogStream.filter(x -> isPalindrome(x)).toList();

      return first100;
    }
  }

  public static Map<Character, Integer> letters(String s) {
    Map<Character, Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }
}

/*
 * In a JavaStream, the word stream would be filtered to words that are
 * of length bigger or equal to 5.
 * 
 * 
 * 
 */
