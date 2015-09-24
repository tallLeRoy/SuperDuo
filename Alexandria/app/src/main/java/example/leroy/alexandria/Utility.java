package example.leroy.alexandria;

/**
 * Created by LeRoy on 8/25/2015.
 */
public class Utility {

    // found this at http://stackoverflow.com/questions/17108621/converting-isbn10-to-isbn13 with an entry from Arnav Gupta
    // this one looks right
    public static String ISBN10toISBN13( String ISBN10 ) {
        String ISBN13  = ISBN10;
        ISBN13 = "978" + ISBN13.substring(0,9);
        int d;

        int sum = 0;
        for (int i = 0; i < ISBN13.length(); i++) {
            d = ((i % 2 == 0) ? 1 : 3);
            sum += ((((int) ISBN13.charAt(i)) - 48) * d);
        }
        sum = 10 - (sum % 10);
        ISBN13 += sum;

        return ISBN13;
    }
}
