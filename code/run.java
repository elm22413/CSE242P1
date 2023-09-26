import java.util.ArrayList;
import java.util.Map;

public class run {
    public static void main(String[] args) throws Exception {

        String filename = "../input.txt";
        // read input inot a map
        Map<String, String> map = MerkleRoot.InputToMap(filename);
        // leafnode arrayList
        ArrayList<Node> leafNode = MerkleRoot.LeafNodes(map);
        // get merkle tree and find the merkle root
        Node merkleRoot = MerkleRoot.getMerkleRoot(leafNode);
        // merket root hash output
        System.out.println("Merkle Root Hash: " + merkleRoot.hash);

    }
}
