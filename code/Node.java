import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//Referenced https://www.geeksforgeeks.org/sha-256-hash-in-java/


class Node {
    
    String address;
    String balance;
    String hash;
    

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

}






class MerkleRoot{
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

    //takes a map and returns an arraylist of nodes
    public static ArrayList<Node> LeafNodes(Map<String, String> map){

        //instance of arraylist that will hold the nodes
        ArrayList<Node> leafNodes = new ArrayList<Node>();

        //for each key in the map
        for(String key : map.keySet()){
            //get the address and balance
            String address = key;
            String balance = map.get(key);
            //make a new node with the address and balance
            Node node = new Node(address, balance); //hashes it aswell
            //add the node to the arraylist
            leafNodes.add(node);
        }

        return leafNodes;
    }

    //takes an arraylist of nodes and returns the root by going through and making new arraylists of parent nodes until there is only one node left
    public static Node getMerkleRoot(ArrayList<Node> leafNodes){

        //instance of arraylist that will hold the parent nodes
        ArrayList<Node> parentNodes = new ArrayList<Node>();

        while(leafNodes.size() > 1){ //while there is more than one node in the leaf nodes 
            //for each node in the leaf nodes
           // System.out.println(leafNodes.size());
            for(int i = 0; i < leafNodes.size(); i+=2){

                //if there is another node after the current node
                if(i+1 < leafNodes.size()){

                    //get the current node and the next node
                    Node node1 = leafNodes.get(i);
                    Node node2 = leafNodes.get(i+1);

                    //concat the hashes of the two nodes
                    String concat = node1.hash.concat(node2.hash);

                    //hash the concat
                    String hashedParent = Node.hash(concat);

                    //make a new node with the hash
                    Node parentNode = new Node("", "");
                    parentNode.hash = hashedParent;

                    //add the node to the parent nodes
                    parentNodes.add(parentNode);
                }
                //if there is not another node after the current node
                else{
                    //get the current node
                    Node node1 = leafNodes.get(i);

                    //concat the hash of the current node with itself
                    String concat2 = node1.hash.concat(node1.hash);

                    //hash the concat
                    String hashParent2 = Node.hash(concat2);

                    //make a new node with the hash
                    Node parentNode = new Node("", "");
                    parentNode.hash = hashParent2;

                    //add the node to the parent nodes
                    parentNodes.add(parentNode);
                }
            }
            leafNodes = parentNodes; //set the leaf nodes to the parent nodes
            parentNodes = new ArrayList<Node>(); //make a new arraylist for the parent nodes

        }   
        return leafNodes.get(0); //return the root

 
    }







}

