package baseObject;

/**
 * @Author: Feng Jixuan
 * @Date: 2022-12-2022-12-15
 * @Description: BPP_Model
 * @version=1.0
 */
public class Configuration {
//    public static int beamWidth = 2;
    public static int beamWidth = 500;
    public static int itemWidth = 1;


    //0: Envelope
    //1: BagFace
    //2: EnvelopeUtilization
    //3: EnvelopeUtilizationBagFace
    public static int positionScorer = 2;

    //0: VolumeItem
    //1: BagFaceItem
    public static int sortItem = 0;
    public static int gamma = 5;
    public static boolean useCutInBranchAndRepair = true;

    public static int masterProblemThreadNum = 1;
    public static int subProblemThreadNum = 1;

    public static double masterProblemTimeLimit = 3600;

    public static int listInitialCapacity=15;
    public static int candidateInitialCapacity =3*beamWidth;
}
