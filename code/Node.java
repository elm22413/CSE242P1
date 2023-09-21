import java.security.MessageDigest;
import java.util.ArrayList;


//Referenced https://www.geeksforgeeks.org/sha-256-hash-in-java/


class Node {
    
    String address;
    String balance;
    String hash;
    MessageDigest md = MessageDigest.getInstance("SHA-256");

    //Sets address balance and hash
    public Node(String address, String balance){
        this.address = address;
        this.balance = balance;
        //concat address and balance into one string
        String concat = address.concat(balance);
        hash = hash(concat);
    }


    //Referenced https://www.geeksforgeeks.org/sha-256-hash-in-java/
    //takes a string and uses SHA-256 to hash
    public static String hash(String s){
        byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8))

        //convert the bytes to signum representation
        BigInteger number = new BigInteger(1, bytes);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
 
        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
        //return hash
        return hexString.toString();


    }

}