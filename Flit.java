
public class Flit {
	
	private int firstStageRouter,
	            secondStageRouter,
	            thirdStageRouter;
	private int source, destination;
	private int start, end;
	
	public Flit(int startCycle){
		start = startCycle;
	}
	
	public void setRouter(int stage, int router) {
		if (stage == 0) firstStageRouter = router;
		else if (stage == 1) secondStageRouter = router;
		else if (stage == 2) thirdStageRouter = router;
	}
	
	public void setSource(int sourceCore) { source = sourceCore; }
	
	public void setDestination(int destCore) { destination = destCore; }
	
	public void setEndCycle(int cycle) { end = cycle; }
	
	public int getRouter(int stage) {
		if (stage == 0) return firstStageRouter;
		else if (stage == 1) return secondStageRouter;
		else if (stage == 2) return thirdStageRouter;
		else return -1;
	}
	
	public int getSource() { return source; }
	
	public int getDestination() { return destination; }
	
	public int getStartCycle() { return start; }
	
	public int getEndCycle() { return end; }
	
	public String toString() {
		String result = "|" + firstStageRouter + "|" + secondStageRouter +
				"|" + thirdStageRouter + "|" + source + "|" + destination + 
				"|" + start + "|" + end + "|";		
		return result;
	}

}
