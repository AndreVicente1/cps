package connexion.requests;

import java.util.Random;

import ast.base.ABase;
import ast.base.Base;
import ast.base.RBase;
import ast.bexp.AndBExp;
import ast.bexp.BExp;
import ast.bexp.SBExp;
import ast.bexp.CExpBExp;
import ast.bexp.NotBExp;
import ast.bexp.OrBExp;
import ast.cexp.CExp;
import ast.cexp.EqCExp;
import ast.cexp.GEqExp;
import ast.cexp.LCExp;
import ast.cont.Cont;
import ast.cont.DCont;
import ast.cont.ECont;
import ast.cont.FCont;
import ast.dirs.Dirs;
import ast.dirs.FDirs;
import ast.dirs.RDirs;
import ast.gather.FGather;
import ast.gather.Gather;
import ast.gather.RGather;
import ast.position.Position;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.rand.CRand;
import ast.rand.Rand;
import ast.rand.SRand;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

/**
 * This class follows the Design Pattern Builder,
 * its purpose is to create a Request in an easier way.
 * So far, this class can create a parametized Request,
 * or a random Request with only the flag isAsynchronous and the URI of the request as parameters
 */
public class RequestBuilder {
	
	private static final Random random = new Random();
	private static final String[] sensor_data = {"temperature", "fumee", "humidite"};
	
	/**
	 * Creates a simple request without continuation
	 *
	 * @param isAsync Indicates whether the request is asynchronous
	 * @param uri The URI identifying the request
	 * @param clientInboundPortURI The client's inbound port URI for connection
	 * @param queryType The type of query to create ("BQuery" or "GQuery")
	 * @param gatherType The type of gather to use in the query ("FGather" or "RGather")
	 * @param contType The continuity context used in the query ("FCont", "DCont")
	 * @param sensorId The sensor ID used in gather
	 * @param nextGather The next gather object for recursive gathers (can be null if no recursive gather)
	 * @param bexpType The boolean expression type ("And", "Or", "Not", "CExp")
	 * @param left The left-hand side BExp for boolean expressions
	 * @param right The right-hand side BExp for boolean expressions
	 * @param cexpType The comparison expression type ("GEq", "Eq", "LC")
	 * @param rand1 The first operand for comparison expressions
	 * @param rand2 The second operand for comparison expressions
	 * @param base The base for FCont if needed
	 * @param maxDistance The maximum distance for FCont
	 * @param dirs The directions for DCont
	 * @param maxJumps The maximum jumps for DCont
	 * @return A new instance of RequestI configured as specified
	 */
	public static RequestI createRequest(
	    boolean isAsync, 
	    String uri, 
	    //ConnectionInfoI clientInfo, mis dans le client
	    String queryType,
	    String gatherType,
	    String contType,
	    String sensorId,
	    Gather nextGather,
	    String bexpType,
	    BExp left,
	    BExp right,
	    String cexpType,
	    Rand rand1,
	    Rand rand2,
	    Base base,
	    double maxDistance,
	    Dirs dirs,
	    int maxJumps
	) {
	    QueryI query = createQuery(queryType, gatherType, contType, sensorId, nextGather, bexpType, left, right, cexpType, rand1, rand2, base, maxDistance, dirs, maxJumps);
	    //EndPointDescriptorI endpoint = new EndPointDescriptor(clientInfo.endPointInfo().toString());
	    //ConnectionInfoI connectionInfo = new ConnectionInfo(uri, endpoint);

	    return new Request(isAsync, uri, query, null);
	}
	
	/**
	 * Create a randomized request, all types and values will be chosen randomly
	 * @param isAsync Indicates whether the request is asynchronous
	 * @param uri The URI identifying the request
	 * @param sensorId
	 * @return the random request generated
	 */
	public static RequestI createRandomRequest(boolean isAsync, String uri) {
		QueryI query = createRandomQuery();
		
		System.out.println("Rnadom Query created:\n" + query);
		return new Request(isAsync, uri, query, null);
	}
	
	public static QueryI createRandomQuery() {
        String queryType = random.nextBoolean() ? "GQuery" : "BQuery";

        Gather gather = createRandomGather(0);
        Cont cont = createRandomCont();

        BExp bexp = null;
        if ("BQuery".equals(queryType)) {
            bexp = createRandomBExp(0);
        }

        if ("GQuery".equals(queryType)) {
            return new GQueryBuilder().setGather(gather).setCont(cont).build();
        } else { // BQuery
            return new BQueryBuilder().setBExp(bexp).setCont(cont).build();
        }
    }

    
    // specify the query type as a string in the parameter
	public static QueryI createQuery(
			String queryType,	// the query type: "BQuery" or "GQuery"
		    String gatherType,	// the gather type: "FGather" or "RGather"
		    String contType,	// the cont type: "FCont" or "ECont" or "DCont"
		    String sensorId,
		    Gather nextGather,
		    String bexpType,
		    BExp left,
		    BExp right,
		    String cexpType,
		    Rand rand1,
		    Rand rand2,
		    Base base,
		    double maxDistance,
		    Dirs dirs,
		    int maxJumps
		) {
		
		// on renvoie des exceptions si les types ne sont pas corrects 
		
		Gather gather = createGather(gatherType, sensorId, nextGather);
		Cont cont = createCont(contType, base, maxDistance, dirs, maxJumps);
		 
		// build bexp
		BExp bexp = null;
		if ("BQuery".equals(queryType)) {
	        CExp condition = createCExp(cexpType, rand1, rand2);
	        bexp = createBExp(bexpType, left, right, condition);
	    }

		switch (queryType) {
	        case "GQuery":
	            if (gather == null || cont == null) {
	                throw new IllegalArgumentException("Gather and Cont must be provided for GQuery");
	            }
	            return new GQueryBuilder().setGather(gather).setCont(cont).build();
	        
	        case "BQuery":
	            if (bexp == null || cont == null) {
	                throw new IllegalArgumentException("BExp and Cont must be provided for BQuery");
	            }
	            return new BQueryBuilder().setBExp(bexp).setCont(cont).build();
	            
	        default:
	            throw new IllegalArgumentException("Unsupported query type: " + queryType);
	    }
    }
	
	
    
    // specify the gather type as a string in the parameter
    public static Gather createGather(String gatherType, String sensorId, Gather gather) {
        switch (gatherType) {
            case "FGather":
                return new FGatherBuilder().setSensorId(sensorId).build();
            case "RGather":
                return new RGatherBuilder().setSensorId(sensorId).setNext(gather).build();
            default:
                throw new IllegalArgumentException("Unsupported gather type: " + gatherType);
        }
    }
    
    // we use an integer stopStack to avoid an infinite loop of RGather creation
    public static Gather createRandomGather(int stopStack) {
    	String gatherType;
    	if (stopStack > 5) {
    		gatherType = "FGather";
    	} else 
    		gatherType = random.nextBoolean() ? "FGather" : "RGather";
        String sensorId = sensor_data[random.nextInt(3)];
        
        switch (gatherType) {
            case "FGather":
                return new FGatherBuilder().setSensorId(sensorId).build();
            case "RGather":
            	Gather nextGather = createRandomGather(stopStack++);
                return new RGatherBuilder().setSensorId(sensorId).setNext(nextGather).build();
            default:
                throw new IllegalArgumentException("Unsupported gather type: " + gatherType);
        }
    }
    
    // stopStack used to stop inifinite BExp creation
	public static BExp createRandomBExp(int stopStack) {
		int choice;
		if (stopStack > 5) {
			choice = 4;
		} else
			choice = random.nextInt(5);
        switch (choice) {
            case 0:
            	return new SBExp(sensor_data[random.nextInt(sensor_data.length)]);
            case 1:
                return new NotBExp(createRandomBExp(stopStack++));
            case 2:
                return new OrBExp(createRandomBExp(stopStack++), createRandomBExp(stopStack++));
            case 3:
                return new AndBExp(createRandomBExp(stopStack++), createRandomBExp(stopStack++));
            case 4:
            	return new CExpBExp(createCExp());
            default:
            	System.out.println("random nextInt > 6: resorting to default CExpBExp");
            	return new CExpBExp(createCExp());
        }
	
    }

    public static CExp createCExp() {
        int typeIndex = random.nextInt(3);
        SRand rand = new SRand(sensor_data[random.nextInt(sensor_data.length)]);
        CRand crand = new CRand(random.nextDouble(30));

        switch (typeIndex) {
            case 0:
                return new EqCExp(rand, crand);
            case 1:
                return new GEqExp(rand, crand);
            case 2:
                return new LCExp(rand, crand);
            default:
                return new EqCExp(rand, crand);
        }
    }
    
    public static BExp createBExp(String bexpType, BExp left, BExp right, CExp condition) {
        switch (bexpType) {
            case "And":
                return new AndBExp(left, right);
            case "Or":
                return new OrBExp(left, right);
            case "Not":
                return new NotBExp(left != null ? left : right);
            case "CExp":
                return new CExpBExp(condition);
            default:
                return new CExpBExp(condition);
        }
    }

   
    public static CExp createCExp(String type, Rand rand1, Rand rand2) {
        switch (type) {
            case "GEq":
                return new GEqExp(rand1, rand2);
            case "LC":
                return new LCExp(rand1, rand2);
            case "Eq":
                return new EqCExp(rand1, rand2);
            default:
                throw new IllegalArgumentException("Unsupported CExp type: " + type);
        }
    }
    
    private static Cont createCont(String type, Base base, double maxDistance, Dirs dirs, int maxJumps) {
    	switch (type) {
        case "FCont":
            if (base == null) {
                throw new IllegalStateException("Base must be provided for FCont.");
            }
            return new FContBuilder().setBase(base).setMaxDistance(maxDistance).build();
        case "DCont":
            if (dirs == null) {
                throw new IllegalStateException("Dirs must be provided for DCont.");
            }
            return new DContBuilder().setDirs(dirs).setMaxJumps(maxJumps).build();
        case "ECont":
            return new ECont();
        default:
            // Randomly pick a continuity type if not specified
            return random.nextBoolean() ? 
                   new FContBuilder().setBase(createRandomBase()).build() :
                   new DContBuilder().setDirs(createRandomDirs(0)).build();
    	}
    }
    
    public static Cont createRandomCont() {
        String contType = random.nextBoolean() ? "FCont" : (random.nextBoolean() ? "DCont" : "ECont");
        Base base = createRandomBase();
        double maxDistance = random.nextDouble() * 1000;
        Dirs dirs = createRandomDirs(0);
        int maxJumps = random.nextInt(15);
        return createCont(contType, base, maxDistance, dirs, maxJumps);
    }
    
    // Method to randomly create a Base instance
    public static Base createRandomBase() {
        Position position = new Position(random.nextDouble(50), random.nextDouble(50));
        if (random.nextBoolean()) {
            return new ABase(position);
        } else {
            return new RBase(position);
        }
    }

    // Method to randomly create a Dirs instance
    public static Dirs createRandomDirs(int stopStack) {
    	int choice;
    	if (stopStack > 5) {
    		choice = 1;
    	} else 
    		choice = random.nextInt(2);
        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        if (choice == 1) {
            return new FDirs(direction);
        } else {
            Dirs secondaryDirs = createRandomDirs(stopStack++);
            return new RDirs(direction, secondaryDirs);
        }
    }
    
    /*****************************************
     * Builders 
     *****************************************/
    
    // Builder for GQuery
    private static class GQueryBuilder {
        private Gather gather;
        private Cont cont;

        private GQueryBuilder setGather(Gather gather) {
            this.gather = gather;
            return this;
        }

        private GQueryBuilder setCont(Cont cont) {
            this.cont = cont;
            return this;
        }

        private GQuery build() {
            if (gather == null || cont == null) {
                throw new IllegalStateException("Gather and Cont must be set before building a GQuery");
            }
            return new GQuery(gather, cont);
        }
    }

    // Builder for BQuery
    private static class BQueryBuilder {
        private BExp bexp;
        private Cont cont;

        private BQueryBuilder setBExp(BExp bexp) {
            this.bexp = bexp;
            return this;
        }

        private BQueryBuilder setCont(Cont cont) {
            this.cont = cont;
            return this;
        }

        private BQuery build() {
            if (bexp == null || cont == null) {
                throw new IllegalStateException("BExp and Cont must be set before building a BQuery.");
            }
            return new BQuery(bexp, cont);
        }
    }

    
    // Builder for RGather
    private static class RGatherBuilder {
        private String sensorId;
        private Gather next;

        private RGatherBuilder setSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        private RGatherBuilder setNext(Gather next) {
            this.next = next;
            return this;
        }

        private RGather build() {
            return new RGather(sensorId, next);
        }
    }

    // Builder for FGather
    private static class FGatherBuilder {
        private String sensorId;

        private FGatherBuilder setSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        private FGather build() {
            return new FGather(sensorId);
        }
    }
    
    
    // Builder for CRand
    @SuppressWarnings("unused")
	private static class CRandBuilder {
        private double cst;

        private CRandBuilder setConstant(double cst) {
            this.cst = cst;
            return this;
        }

        private CRand build() {
            return new CRand(cst);
        }
    }

    // Builder for SRand
    @SuppressWarnings("unused")
	private static class SRandBuilder {
        private String sensorId;

        private SRandBuilder setSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        private SRand build() {
            return new SRand(sensorId);
        }
    }
    
    // Builder for FDirs
    @SuppressWarnings("unused")
	private static class FDirsBuilder {
        private Direction dir;

        private FDirsBuilder setDir(Direction dir) {
            this.dir = dir;
            return this;
        }

        private FDirs build() {
            if (dir == null) {
                throw new IllegalStateException("Direction must be set before building FDirs.");
            }
            return new FDirs(dir);
        }
    }
    
    // Builder for RDirs
    @SuppressWarnings("unused")
	private static class RDirsBuilder {
        private Direction dir;
        private Dirs dirs;

        private RDirsBuilder setDir(Direction dir) {
            this.dir = dir;
            return this;
        }

        private RDirsBuilder setDirs(Dirs dirs) {
            this.dirs = dirs;
            return this;
        }

        private RDirs build() {
            if (dir == null || dirs == null) {
                throw new IllegalStateException("Both direction and dirs must be set before building RDirs.");
            }
            return new RDirs(dir, dirs);
        }
    }
    
    // FCont builder
    private static class FContBuilder {
        private Base base;
        private double maxDistance;

        private FContBuilder setBase(Base base) {
            this.base = base;
            return this;
        }

        private FContBuilder setMaxDistance(double maxDistance) {
            this.maxDistance = maxDistance;
            return this;
        }

        private FCont build() {
            if (base == null) {
                throw new IllegalStateException("Base must be set before building an FCont.");
            }
            return new FCont(base, maxDistance);
        }
    }
    
    // DCont builder
    private static class DContBuilder {
        private Dirs dirs;
        private int maxJumps;

        private DContBuilder setDirs(Dirs dirs) {
            this.dirs = dirs;
            return this;
        }

        private DContBuilder setMaxJumps(int maxJumps) {
            this.maxJumps = maxJumps;
            return this;
        }

        private DCont build() {
            if (dirs == null) {
                throw new IllegalStateException("Dirs must be set before building a DCont.");
            }
            return new DCont(dirs, maxJumps);
        }
    }

}
