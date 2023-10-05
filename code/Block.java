import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Block {

    // Information for the current header
    String prevHeader;
    String root;
    Integer timeStamp;
    // a difficulty target NEEDS to be passed in as a value of "8"
    // will result in a 50 percent sucess rate
    Integer difficultyTarget;
    // nonce will start at 0 and will be calculated in the block build below
    Integer nonce = 0;

    // Information for this block
    // Map including a string for address then
    Map<String, String> map;

    // Header
    static String header;

    public Block(String prevHeader, String root, Integer difficultyTarget, String inputFileName) {
        this.prevHeader = prevHeader;
        this.root = root;
        timeStamp = (int) (System.currentTimeMillis() / 1000);
        // MAKE SURE TO ONLY PASS IN 2 FOR DIFFICULTY TARGET
        this.difficultyTarget = difficultyTarget;

        nonce = findNonce(root, nonce, difficultyTarget);

        // Create header. In the print block we need to take this apart
        header = prevHeader + root + Integer.toString(timeStamp) + Integer.toString(difficultyTarget)
                + Integer.toString(nonce);
        // Hash the header
        header = hash(header);

        // load in the account information.
        // Not sure if this is supposed to be hashed or not
        try {
            map = InputToMap(inputFileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static String getHeader() {
        return header;
    }

    public static String hash(String s) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");

            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));

            // convert the bytes to signum representation
            BigInteger number = new BigInteger(1, bytes);

            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }
            // return hash
            return hexString.toString();

        } catch (Exception e) {
            System.out.println("error making instance from messgae digest: " + e);
            return s;
        }

    }

    public Integer findNonce(String hash, Integer nonce, Integer target) {
        while (true) {
            // concatonate nonce to hash
            hash = hash + Integer.toString(nonce);
            // hash the current hash
            hash = hash(hash);

            // When target is set to 8, this loop will check the first 8 characters of the
            // hash to see if any
            // of them are equal to '0' and since there are 16 possible values of hex, there
            // is a 50 percent chance
            // that there will be a '0' found each time.
            for (int i = 0; i < target; i++) {
                if (hash.charAt(i) == '0') {
                    return nonce;
                }
            }
            nonce++;

        }
    }

    // take a file called filename and returns a map of the address and balance on
    // each line
    public static Map<String, String> InputToMap(String filename) throws Exception {

        // instance of map that will put the address and integer into hashmap for later
        // use of making leaf
        Map<String, String> map = new HashMap<String, String>();

        try (BufferedReader read = new BufferedReader(new FileReader(filename))) { // try to read the file
            String line = read.readLine(); // start reading first line
            while (line != null) { // while there is another line
                String[] split = line.split(" "); // split the line by a space
                String address = split[0]; // get the address
                String balance = split[1]; // get the balance
                map.put(address, balance); // put the address and balance into the map
                line = read.readLine(); // read the next line
            }
        } catch (Exception e) {
            System.out.println("There was an error while reading file: " + e);
        }

        return map;
    }

    // Print the block
    // Your tree structure should include a print function, and that function must
    // take a parameter to print or not print the full account ledger (this may be
    // useful for your testing now and later on).
    public void printBlock(boolean printLedger) {
        System.out.println("BEGIN BLOCK");
        System.out.println("BEGIN HEADER:");

        System.out.println("Previous Header: " + prevHeader);
        System.out.println("Merkle Root: " + root);
        System.out.println("Timestamp: " + timeStamp);
        System.out.println("Difficulty Target: " + difficultyTarget);
        System.out.println("Nonce: " + nonce);
        System.out.println("END HEADER");

        if (printLedger) {
            System.out.println("Ledger: " + map);
        }

        System.out.println("END BLOCK");
        System.out.println("");
    }

    // helper fucntuon to get the merkle root of the input file
    public static String getMerkleRoot(String filename) throws Exception {
        // read input inot a map
        Map<String, String> map = MerkleRoot.InputToMap(filename);
        // leafnode arrayList
        ArrayList<Node> leafNode = MerkleRoot.LeafNodes(map);
        // get merkle tree and find the merkle root
        Node merkleRoot = MerkleRoot.getMerkleRoot(leafNode);

        // check output
        System.out.println("Merkle Root Hash: " + merkleRoot.hash);

        return merkleRoot.hash;

    }

    public static void main(String[] args) throws Exception {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the names of the input files separated by spaces: ");
        String inputFilesStr = scan.nextLine();

        String[] inputFiles = inputFilesStr.split(" ");

        for (String inputFile : inputFiles) {
            // Assuming getHeader() returns the header based on the last processed block
            String prevHeader = Block.getHeader();

            // Get the root for the current input file
            String root = Block.getMerkleRoot(inputFile);

            // Create a new block
            Block block = new Block(prevHeader, root, 8, inputFile);
            // Print the block
            block.printBlock(true);

            // Create the output file name
            try {
                // Create the output file using the input file name and replacing .txt with
                // .block.out
                String outputFileName = inputFile.substring(0, inputFile.length() - 4) + ".block.out";
                System.out.println("Output file name: " + outputFileName);

                // Write to the file
                FileWriter writer = new FileWriter(outputFileName);
                String blockInfo = block.getBlockInfo(false); // Get the block info as a string
                writer.write(blockInfo);
                writer.close(); // Close the writer
            } catch (Exception e) {
                System.out.println("There was an error while writing to the file: " + e);
            }
        }

        scan.close();
    }

    public String getBlockInfo(boolean printLedger) {
        StringBuilder blockInfo = new StringBuilder();
        blockInfo.append("BEGIN BLOCK\n");
        blockInfo.append("BEGIN HEADER:\n");
        blockInfo.append("Previous Header: ").append(prevHeader).append("\n");
        blockInfo.append("Merkle Root: ").append(root).append("\n");
        blockInfo.append("Timestamp: ").append(timeStamp).append("\n");
        blockInfo.append("Difficulty Target: ").append(difficultyTarget).append("\n");
        blockInfo.append("Nonce: ").append(nonce).append("\n");
        blockInfo.append("END HEADER\n");
        if (printLedger) {
            blockInfo.append("Ledger: ").append(map).append("\n");
        }
        blockInfo.append("END BLOCK\n\n");
        return blockInfo.toString();
    }

}
