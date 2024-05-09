package connexion.requests;

import ast.base.Base;
import ast.bexp.AndBExp;
import ast.bexp.BExp;
import ast.bexp.CExpBExp;
import ast.bexp.NotBExp;
import ast.bexp.OrBExp;
import ast.cexp.CExp;
import ast.cexp.EqCExp;
import ast.cexp.GEqExp;
import ast.cexp.LCExp;
import ast.cont.DCont;
import ast.cont.ECont;
import ast.cont.FCont;
import ast.cont.ICont;
import ast.dirs.Dirs;
import ast.dirs.FDirs;
import ast.dirs.RDirs;
import ast.gather.FGather;
import ast.gather.Gather;
import ast.gather.RGather;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.rand.CRand;
import ast.rand.Rand;
import ast.rand.SRand;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

/**
 * Classe pour créer des requêtes avec ou sans continuation,
 * elle utilise le Pattern Builder
 */
public class RequestBuilder {
	
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
		ICont cont = createCont(contType, base, maxDistance, dirs, maxJumps);
		 
		// build bexp
		BExp bexp = null;
		if ("BQuery".equals(queryType)) {
	        CExp condition = createCExp(cexpType, rand1, rand2);
	        bexp = createBExp(bexpType, left, right, condition);
	    }

		switch (queryType) {
	        case "GQuery":
	            if (gather == null || cont == null) {
	                throw new IllegalArgumentException("Gather and Cont must be provided for GQuery.");
	            }
	            return new GQueryBuilder().setGather(gather).setCont(cont).build();
	        
	        case "BQuery":
	            if (bexp == null || cont == null) {
	                throw new IllegalArgumentException("BExp and Cont must be provided for BQuery.");
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
    
    public static BExp createBExp(String type, BExp left, BExp right, CExp condition) {
        switch (type) {
            case "And":
                return new AndBExp(left, right);
            case "Or":
                return new OrBExp(left, right);
            case "Not":
                return new NotBExp(left);
            case "CExp":
                return new CExpBExp(condition);
            default:
                throw new IllegalArgumentException("Unsupported BExp type: " + type);
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
    
    public static ICont createCont(String type, Base base, double maxDistance, Dirs dirs, int maxJumps) {
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
	            throw new IllegalArgumentException("Unsupported continuity type: " + type);
	    }
    }
    
    /*****************************************
     * Builders 
     *****************************************/
    
    // Builder for GQuery
    public static class GQueryBuilder {
        private Gather gather;
        private ICont cont;

        public GQueryBuilder setGather(Gather gather) {
            this.gather = gather;
            return this;
        }

        public GQueryBuilder setCont(ICont cont) {
            this.cont = cont;
            return this;
        }

        public GQuery build() {
            if (gather == null || cont == null) {
                throw new IllegalStateException("Gather and Cont must be set before building a GQuery.");
            }
            return new GQuery(gather, cont);
        }
    }

    // Builder for BQuery
    public static class BQueryBuilder {
        private BExp bexp;
        private ICont cont;

        public BQueryBuilder setBExp(BExp bexp) {
            this.bexp = bexp;
            return this;
        }

        public BQueryBuilder setCont(ICont cont) {
            this.cont = cont;
            return this;
        }

        public BQuery build() {
            if (bexp == null || cont == null) {
                throw new IllegalStateException("BExp and Cont must be set before building a BQuery.");
            }
            return new BQuery(bexp, cont);
        }
    }

    
    // Builder for RGather
    public static class RGatherBuilder {
        private String sensorId;
        private Gather next;

        public RGatherBuilder setSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public RGatherBuilder setNext(Gather next) {
            this.next = next;
            return this;
        }

        public RGather build() {
            return new RGather(sensorId, next);
        }
    }

    // Builder for FGather
    public static class FGatherBuilder {
        private String sensorId;

        public FGatherBuilder setSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public FGather build() {
            return new FGather(sensorId);
        }
    }
    
    
    // Builder for CRand
    public static class CRandBuilder {
        private double cst;

        public CRandBuilder setConstant(double cst) {
            this.cst = cst;
            return this;
        }

        public CRand build() {
            return new CRand(cst);
        }
    }

    // Builder for SRand
    public static class SRandBuilder {
        private String sensorId;

        public SRandBuilder setSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public SRand build() {
            return new SRand(sensorId);
        }
    }
    
    // Builder for FDirs
    public static class FDirsBuilder {
        private Direction dir;

        public FDirsBuilder setDir(Direction dir) {
            this.dir = dir;
            return this;
        }

        public FDirs build() {
            if (dir == null) {
                throw new IllegalStateException("Direction must be set before building FDirs.");
            }
            return new FDirs(dir);
        }
    }
    
    // Builder for RDirs
    public static class RDirsBuilder {
        private Direction dir;
        private Dirs dirs;

        public RDirsBuilder setDir(Direction dir) {
            this.dir = dir;
            return this;
        }

        public RDirsBuilder setDirs(Dirs dirs) {
            this.dirs = dirs;
            return this;
        }

        public RDirs build() {
            if (dir == null || dirs == null) {
                throw new IllegalStateException("Both direction and dirs must be set before building RDirs.");
            }
            return new RDirs(dir, dirs);
        }
    }
    
    // FCont builder
    public static class FContBuilder {
        private Base base;
        private double maxDistance;

        public FContBuilder setBase(Base base) {
            this.base = base;
            return this;
        }

        public FContBuilder setMaxDistance(double maxDistance) {
            this.maxDistance = maxDistance;
            return this;
        }

        public FCont build() {
            if (base == null) {
                throw new IllegalStateException("Base must be set before building an FCont.");
            }
            return new FCont(base, maxDistance);
        }
    }
    
    // DCont builder
    public static class DContBuilder {
        private Dirs dirs;
        private int maxJumps;

        public DContBuilder setDirs(Dirs dirs) {
            this.dirs = dirs;
            return this;
        }

        public DContBuilder setMaxJumps(int maxJumps) {
            this.maxJumps = maxJumps;
            return this;
        }

        public DCont build() {
            if (dirs == null) {
                throw new IllegalStateException("Dirs must be set before building a DCont.");
            }
            return new DCont(dirs, maxJumps);
        }
    }

}
