import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class run {
    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the names of the input files separated by spaces: ");
        String inputFilesStr = scan.nextLine();

        String[] inputFiles = inputFilesStr.split(" ");
        ArrayList<Block> blockChain = new ArrayList<>();

        for (String inputFile : inputFiles) {
            // Existing block creation logic
            String prevHeader = null;

            if (!blockChain.isEmpty()) {
                prevHeader = blockChain.get(blockChain.size() - 1).getHeader();
            }

            String root = Block.getMerkleRoot(inputFile);
            Block block = new Block(prevHeader, root, 8, inputFile);
            blockChain.add(block);
            block.printBlock(true);

            // Test the created block
            String testResults = runTests(block, blockChain); // pass blockChain to runTests

            System.out.println(block.balance("0x2a0f89d65a30bE98cDb42174674222A7bDeBfDfF", blockChain));// THIS IS
                                                                                                        // HARDCODED

            try {
                String outputFileName = inputFile.substring(0, inputFile.length() - 4) + ".block.out";
                FileWriter writer = new FileWriter(outputFileName);

                // Write block info to file
                String blockInfo = block.getBlockInfo(false);
                writer.write(blockInfo);

                // Write test results to file
                writer.write("\n\n--- TEST RESULTS ---\n");
                writer.write(testResults);

                writer.close();
            } catch (Exception e) {
                System.out.println("There was an error while writing to the file: " + e);
            }
        }

        scan.close();
    }

    private static String runTests(Block block, List<Block> blockChain) {
        StringBuilder testResults = new StringBuilder();

        // Sample test
        String prevHeader = block.prevHeader;
        if (prevHeader == null || blockChain.stream().anyMatch(b -> b.prevHeader.equals(prevHeader))) {
            testResults.append("Test Invalid Previous Header: PASS\n");
        } else {
            testResults.append("Test Invalid Previous Header: FAIL\n");
        }

        // Test for Timestamp Validity
        if (block.timeStamp <= System.currentTimeMillis()
                && (blockChain.isEmpty() || block.timeStamp >= blockChain.get(blockChain.size() - 1).timeStamp)) {
            testResults.append("Test Timestamp Validity: PASS\n");
        } else {
            testResults.append("Test Timestamp Validity: FAIL\n");
        }

        // Test for Difficulty Target Range (assuming valid range is 1-10 for example
        // purposes)
        if (block.difficultyTarget >= 1 && block.difficultyTarget <= 10) {
            testResults.append("Test Difficulty Target Range: PASS\n");
        } else {
            testResults.append("Test Difficulty Target Range: FAIL\n");
        }

        // Test for Merkle Root Validity
        if (block.root != null && !block.root.trim().isEmpty()) {
            testResults.append("Test Merkle Root Validity: PASS\n");
        } else {
            testResults.append("Test Merkle Root Validity: FAIL\n");
        }

        // Test for Nonce Range (assuming valid range is 0-10000 for example purposes)
        if (block.nonce >= 0 && block.nonce <= 10000) {
            testResults.append("Test Nonce Range: PASS\n");
        } else {
            testResults.append("Test Nonce Range: FAIL\n");
        }

        // TODO: more tests

        return testResults.toString();
    }
}
