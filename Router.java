import java.util.*;

public class Router {

	private ArrayList<ArrayList<Flit>> bufferIn,
	                                   bufferOut;
	
	private int[] occupancyIn,
	              occupancyOut;
	
	private int capacity;
	private int stage;
	
	public Router(int stage) {
		//Each input has four buffers
		capacity = 4; 
		this.stage = stage;
		occupancyIn = new int[8];
		occupancyOut = new int[8];
		bufferIn = new ArrayList<ArrayList<Flit>>();
		bufferOut= new ArrayList<ArrayList<Flit>>();	
		//Each router has eight input/output
		for (int i = 0; i < 8; i++)	{ 
			bufferIn.add(new ArrayList<Flit>());
			bufferOut.add(new ArrayList<Flit>());
			occupancyIn[i] = 0;
			occupancyOut[i] = 0;
		}
	}
	
	public Flit getOutputData(int outputPort){
		if (occupancyOut[outputPort] == 0) return null;
		return bufferOut.get(outputPort).get(0);
	}
	
	public void removeOutputData(int outputPort){
		if (occupancyOut[outputPort] == 0) return;
		ArrayList<Flit> temp = bufferOut.get(outputPort);
		temp.remove(0);
		bufferOut.set(outputPort, temp);
		occupancyOut[outputPort]--;
	}
	
	public void addFlit(Flit data){
		int inputPort;
		if (stage == 0) inputPort = data.getSource() % 8;
		else inputPort = data.getRouter(stage-1);
		ArrayList<Flit> temp = bufferIn.get(inputPort); 
		temp.add(data);
		bufferIn.set(inputPort,temp);
		occupancyIn[inputPort]++;		
	}
	
	public boolean isInputFull(int inputPort){
		return occupancyIn[inputPort] == capacity;
	}
	// move flits from input to output 
	public void switchInputToOutput(){
		int outputPort;
		Flit data;
		ArrayList<Flit> temp;
		for (int i = 0; i < 8; i++){
			if (occupancyIn[i] != 0){
				data = bufferIn.get(i).get(0);
				if (stage != 2) outputPort = data.getRouter(stage+1);
				else outputPort = data.getDestination() % 8;
				if (occupancyOut[outputPort] != capacity) {
					temp = bufferOut.get(outputPort);
					temp.add(data);
					bufferOut.set(outputPort, temp);
					occupancyOut[outputPort]++;
					temp = bufferIn.get(i);
					temp.remove(0);
					bufferIn.set(i, temp);
					occupancyIn[i]--;
									
				}
			}
		}
	}
	public int getOccupancyIn(int port) { return occupancyIn[port]; }
	public int getOccupancyOut(int port) { return occupancyOut[port]; }
}
