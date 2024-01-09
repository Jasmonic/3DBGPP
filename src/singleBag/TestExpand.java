package singleBag;

import baseObject.Bag;
import baseObject.Configuration;
import baseObject.Instance;
import scorer.Envelope;
import scorer.EnvelopeUtilization;
import scorer.PositionScorer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-18
 * @Description: BPP_Model
 * @version=1.0
 */
public class TestExpand {
    public static void main(String[] args) throws IOException {
        Instance instance=new Instance();
        instance.initialze(args[0]);
        Bag bag=instance.getBags().get(1);
        ArrayList<Integer> remainingItemsId=new ArrayList<>();
        for (int i = 0; i < instance.getAllItems().size(); i++) {
            remainingItemsId.add(i);
        }
        PositionScorer scorer=null;
        Node node0=new Node(bag,remainingItemsId,instance);
        switch (Configuration.positionScorer){
            case 0: scorer=new EnvelopeUtilization();
            case 1: scorer=new Envelope();
        }
        ArrayList<Node> expandedNodeList=node0.expand2(Configuration.beamWidth,Configuration.itemWidth,scorer,remainingItemsId);
        System.out.println(expandedNodeList.size());
        for(Node node:expandedNodeList){
            System.out.println(node.packedPosition);
        }
    }
}
