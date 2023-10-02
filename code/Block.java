import java.util.Map;

public class Block {

    //Information for the current header
    String prevHeader;
    String root;
    Integer timeStamp;
    // a difficulty target NEEDS to be passed in as a value of 2 will result in a 50 percent sucess rate
    Integer difficultyTarget;
    //nonce will start at 0 and will be calculated in the block build below
    Integer nonce = 0;

    //Information for this block
    //Map including a string for address then 
    Map<String, String> map;

    //Header
    String header;


    public Block(String prevHeader, String root, Integer difficultyTarget){
        this.prevHeader = prevHeader;
        this.root = root;
        timeStamp = (int)(System.currentTimeMillis() / 1000);
        //MAKE SURE TO ONLY PASS IN 2 FOR DIFFICULTY TARGET
        this.difficultyTarget = difficultyTarget;

        //should we calculate this blocks header here??
        //after we get the header, how do we just "add on" the list of accounts

    }

}
