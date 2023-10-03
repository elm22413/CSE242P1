import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class Block {

    //Information for the current header
    String prevHeader;
    String root;
    Integer timeStamp;
    // a difficulty target NEEDS to be passed in as a value of "8" 
    //will result in a 50 percent sucess rate
    Integer difficultyTarget;
    //nonce will start at 0 and will be calculated in the block build below
    Integer nonce = 0;

    //Information for this block
    //Map including a string for address then 
    Map<String, String> map;

    //Header
    String header;


    public Block(String prevHeader, String root, Integer difficultyTarget, String inputFileName){
        this.prevHeader = prevHeader;
        this.root = root;
        timeStamp = (int)(System.currentTimeMillis() / 1000);
        //MAKE SURE TO ONLY PASS IN 2 FOR DIFFICULTY TARGET
        this.difficultyTarget = difficultyTarget;

        nonce = findNonce(root, nonce, difficultyTarget);
        
        //Create header. In the print block we need to take this apart
        header = prevHeader + root + Integer.toString(timeStamp) + Integer.toString(difficultyTarget) + Integer.toString(nonce);
        //Hash the header
        header = hash(header);


        //load in the account information. 
        //Not sure if this is supposed to be hashed or not
        try {
            map = InputToMap(inputFileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }

    public String getHeader(){
        return header;
    }

    public static String hash(String s){


        MessageDigest md;
        try{
            md = MessageDigest.getInstance("SHA-256");
        
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));

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
            
        }catch(Exception e){
            System.out.println("error making instance from messgae digest: " + e);
            return s;
        }

    }

    public Integer findNonce(String hash, Integer nonce, Integer target){
        while (true){
            //concatonate nonce to hash
            hash = hash + Integer.toString(nonce);
            //hash the current hash
            hash = hash(hash);

            //When target is set to 8, this loop will check the first 8 characters of the hash to  see if any
            //of them are equal to '0' and since there are 16 possible values of hex, there is a 50 percent chance
            //that there will be a '0' found each time.
            for (int i = 0; i<  target;  i++){
                if (hash.charAt(i) == '0'){
                    return nonce; 
                }
            }
            nonce++;

        }
    }

    //take a file called filename and returns a map of the address and balance on each line
    public static Map<String, String> InputToMap(String filename) throws Exception{

        //instance of map that will put the address and integer into hashmap for later use of making leaf
        Map<String, String> map = new HashMap<String, String>();

        try(BufferedReader read = new BufferedReader(new FileReader(filename))){ //try to read the file
            String line = read.readLine(); //start reading first line
            while(line != null){ //while there is another line
                String[] split = line.split(" "); //split the line by a space 
                String address = split[0]; //get the address
                String balance = split[1]; //get the balance 
                map.put(address, balance); //put the address and balance into the map
                line = read.readLine(); //read the next line 
            }
        }
        catch(Exception e){
            System.out.println("There was an error while reading file: " + e);
        }

        return map;
    }

}


