import java.util.LinkedList;
import java.util.Random;


public class PclosMOSL {

	public static void main(String[] args) {
		
		
		// assume High BandWidth network 256b/cycle (256b/flit)
		final int totalCycle = 1000000; // 1M cycle
		int offeredFlitPerCycle = Integer.parseInt(args[0]); // 32 saturated traffic
		
		LinkedList<Flit> randomTraffic = new LinkedList<Flit>();
		LinkedList<Flit> output = new LinkedList<Flit>();
		int[] checkCores = new int[64];
		Random rand = new Random();
		int tempInt;
		Flit tempFlit;
		
		Router [][] R = new Router[3][8];
		for (int i = 0; i < 8; i++){
			R[0][i] = new Router(0);
			R[1][i] = new Router(1);
			R[2][i] = new Router(2);
		}
		System.out.println("Running simulation...   ");
		for (int cycle = 0; cycle < totalCycle; cycle++){ 
			if (cycle % (totalCycle/100) == 0) System.out.printf("\b\b\b%2d%%",cycle*100/totalCycle);
			// create random traffic
			for (int numFlitsPerCycle = 0; numFlitsPerCycle < offeredFlitPerCycle; numFlitsPerCycle++){
				tempFlit = new Flit(cycle);
				tempInt = rand.nextInt(64);
				tempFlit.setSource(tempInt);
				do {
					tempInt = rand.nextInt(64);
				} while (tempInt == tempFlit.getSource());
				tempFlit.setDestination(tempInt);
				
				tempInt = rand.nextInt(8);
				tempFlit.setRouter(1, tempInt);
				tempInt = tempFlit.getSource() / 8;
				tempFlit.setRouter(0, tempInt);
				tempInt = tempFlit.getDestination() / 8;
				tempFlit.setRouter(2, tempInt);
				randomTraffic.add(tempFlit);				
			}
			if (cycle == -1)
				for (int i = 0; i < randomTraffic.size(); i++){
					System.out.println("#"+ i + " = " + randomTraffic.get(i).toString());
				}
			
			// outputting
			for (int duo = 0; duo < 2; duo++)
			for (int i = 0; i < 64; i++) {
				tempFlit = R[2][i/8].getOutputData(i%8);
				if (tempFlit != null) {
					tempFlit.setEndCycle(cycle);
					output.add(tempFlit);
					R[2][i/8].removeOutputData(i%8);
				}
			}
			// stage 2 intra-propagating
			for (int duo = 0; duo < 2; duo++)
			for (int i = 0; i < 8; i++){
				R[2][i].switchInputToOutput();
			}
			
			// stage 1 to stage 2
			for (int duo = 0; duo < 2; duo++)
			for (int i = 0; i < 64; i++){
				tempFlit = R[1][i/8].getOutputData(i%8);
				if (tempFlit != null) {
					tempInt = tempFlit.getRouter(2);
					if (!R[2][tempInt].isInputFull(tempFlit.getRouter(1))){
						R[2][tempInt].addFlit(tempFlit);
						R[1][i/8].removeOutputData(i%8);
					}
				}
			}
			
			// stage 1 intra-propagating
			for (int duo = 0; duo < 2; duo++)
			for (int i = 0; i < 8; i++){
				R[1][i].switchInputToOutput();
			}
			
			// stage 0 to stage 1
			for (int duo = 0; duo < 2; duo++)
			for (int i = 0; i < 64; i++){
				tempFlit = R[0][i/8].getOutputData(i%8);
				if (tempFlit != null) {
					tempInt = tempFlit.getRouter(1);
					if (!R[1][tempInt].isInputFull(tempFlit.getRouter(0))){
						R[1][tempInt].addFlit(tempFlit);
						R[0][i/8].removeOutputData(i%8);
					}
				}
			}

			
			// stage 0 intra-propagating
			for (int duo = 0; duo < 2; duo++)
			for (int i = 0; i < 8; i++){
				R[0][i].switchInputToOutput();
			}
			
			for (int i = 0; i < 64; i++) checkCores[i] = 0;
			
			// inputting limited one flit each core
			for (int duo = 0; duo < 2; duo++)
			for (int i = 0; i < randomTraffic.size(); i++){
				tempFlit = randomTraffic.get(i);
				tempInt = tempFlit.getRouter(0);
				if (checkCores[tempFlit.getSource()] == 0){
					if (!R[0][tempInt].isInputFull(tempFlit.getSource() % 8)) {
						R[0][tempInt].addFlit(tempFlit);
						randomTraffic.remove(i--);
						checkCores[tempFlit.getSource()] = 1;
					}
				}
			}
			
			
			// error-checking every output @ every cycle
			/*
			for (int s = 0; s < 3; s++){
				for (int r = 0; r < 8; r++){
					for (int port = 0; port < 8; port++){
						tempFlit = R[s][r].getOutputData(port);
						if (tempFlit != null){
							System.out.println("#stage = "+s+" #router = "+r+" #outputPort = "+port+
									" Flit = "+tempFlit.toString());
						}
					}
				}
			}
			*/
			
			// cleaning output to save Java memory
			while ( output.size() > 100000) output.remove();
		}
		System.out.println();
		
		float avgLatency = 0;
		for (int i = output.size()-100000; i < output.size(); i++){
			// System.out.println("#"+ i + " = " + output.get(i).toString());
			avgLatency += output.get(i).getEndCycle() - output.get(i).getStartCycle();
		}
		avgLatency = avgLatency/100000;
		System.out.println("latency = "+ avgLatency);
	}
}
