import java.io.File;
import java.io.IOException; //import java.io.PrintStream;
import java.util.List;

/**
 * Description: Controlles the reading and writing of <br>
 * "ExcelFile" Class for creating an Excelfile<br>
 * Author: T. Tews & Faasch <br>
 * Last change: 21.06.2010 <br>
 * Version: 2.1 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class ConvertExcelFile {

	private String excelfilename;
	private Result result;
	static List<PointCluster> cluster;
	// private PrintStream output;
	private int clusterid;
	private double[][] dataAr; // Array with the x, y point values
    private String[] functionsLabel; // x axes labels
    private double [] sumAllList;
	private int[][] pointlist; // Calculate ... the distance between these points
    private double [] firstPointsSumList; //....the first points
	private int minimumRegions;  // number of cluster
	private int saveFrameNo [] = {0,0,0};
	private int lock;
	private double [] emotionResult = new double [8]; // Neutral, Freude, Aerger
	private double [] sumVariance;
	private int skip_Threshold_frames = 3;

	public void startConvertExcelFile(String excfilename,
			List<PointCluster> clust, int clustid, double[][] dataAy,
			String[] func, int[][] plist,
			int numberOfCluster, int lockParam) {
		// Here convert into file
		excelfilename = excfilename;
		cluster = clust; // Cluster with values
		clusterid = clustid; // Clusterid
		dataAr = dataAy; // Array with the x, y point values
		functionsLabel = func; // mimik function array
		pointlist = plist; // Distance Point calcuation points
//		sumlistND = slistND; // Nase Dreieck point list
		minimumRegions = numberOfCluster; // Number of cluster
		sumAllList = new double [functionsLabel.length]; // sum up Areasize
		firstPointsSumList = new double[functionsLabel.length]; 
		sumVariance = new double[functionsLabel.length]; 
		//funcNrMax = functionsLabel.length; //how many areas can be calculated
		
		lock = lockParam; // select video
		ExcelFile exfile = new ExcelFile(new File(excelfilename)); // TODO: xlsx
		String tmpname = "";

		for (int frame = 0; frame < cluster.get(0).track.size(); frame++) { // Y
																			// axes
																			// Frames
			for (PointCluster pr : cluster) { // X axes Cluster, functions

				/*
				 * Create File with Data [Cluster with (x and y points)]
				 * exfile.createDataCell(seite, zeile, spalte, wert)
				 */
				if (frame != 0) { // Data (No Header)
					// Cluster mod 17 Nummer vom Cluster ausrechnen, *2 wegen
					// x,y und + 1 für frameNr
					exfile.createDataCell(0, frame + 1,
							(clusterid % minimumRegions) * 2 + 1, pr.track
									.get(frame).x); // X-Wert
					exfile.createDataCell(0, frame + 1,
							(clusterid % minimumRegions) * 2 + 2, pr.track
									.get(frame).y); // Y-Wert

					dataAr[frame][(clusterid % minimumRegions) * 2] = pr.track
							.get(frame).x;
					dataAr[frame][(clusterid % minimumRegions) * 2 + 1] = pr.track
							.get(frame).y;

				} else { // Header
					tmpname = "" + (clusterid % minimumRegions);
					exfile.createDataCell(0, 0,
							0, cluster.get(0).track.size()-1);
					exfile.createDataCell(0, frame,
							(clusterid % minimumRegions) * 2 + 1, tmpname + ": X");
					exfile.createDataCell(0, frame,
							(clusterid % minimumRegions) * 2 + 2, tmpname + ": Y");
					exfile.createDataCell(0, frame + 1,
							(clusterid % minimumRegions) * 2 + 1, pr.track
									.get(frame).x);
					exfile.createDataCell(0, frame + 1,
							(clusterid % minimumRegions) * 2 + 2, pr.track
									.get(frame).y);
				}
				// Write frame number
				exfile.createDataCell(0, frame + 1, 0, frame);
				exfile.createDataCell(1, frame + 1, 0, frame);

				if (frame == (cluster.get(0).track.size() - 1)) // Create label
					// Average
					exfile.createDataCell(1, frame + 2, 0, "Average");

				clusterid++;
			}

		} // for end

//		System.out.println("CE: " + cluster.get(0).track.size()); // Anzahl der Frames
//		System.out.println("CE: " + minimumRegions); // Clusteranzahl
		
		// calc point values
		for (int frame = 0; frame < cluster.get(0).track.size(); frame++) { // Y
			// axes
			// frames
			for (int g = 0; g < functionsLabel.length; g++) { // X axes function labels
				
				
				
				if (pointlist[g][0] != -1) { // calculate all point areas
					// Create cell and calculate the area
					double tmpVal = 0;
					
					try {
						if(pointlist[g][3] != -1){
							tmpVal = exfile.calcArea(0, frame + 1,  // calc area size
								(pointlist[g][0]) * 2 + 1,   //x1
								(pointlist[g][1]) * 2 + 1,   //x2
								(pointlist[g][2]) * 2 + 1,   //x3
								(pointlist[g][3]) * 2 + 1);  //x4
						} 
					} catch (Exception ArrayIndexOutOfBoundsException3) {
						try {
							if (pointlist[g][2] != -1){
								tmpVal = exfile.calcTriangle(0, frame + 1,  // calc area size
										(pointlist[g][0]) * 2 + 1,   //x1
										(pointlist[g][1]) * 2 + 1,   //x2
										(pointlist[g][2]) * 2 + 1);  //x3
							}
						} catch (Exception ArrayIndexOutOfBoundsException2) {
							tmpVal = exfile.calcDistance(0, frame + 1,  // calc distance to two points
									(pointlist[g][0]) * 2 + 1,   // x1 point 1
									(pointlist[g][1]) * 2 + 1);   // x2 point 2
						}
						
					}
//					if (frame == 0) 
//						firstPointsSumList[g] = tmpVal; // save first row values
					
					if (frame >= 0 && frame < skip_Threshold_frames)  // collect the first four values for the threshold
						firstPointsSumList[g] = tmpVal + firstPointsSumList[g]; // save first row values
					
					// Save all values exect the first three values
					if (frame > skip_Threshold_frames) 
					   sumAllList [g] =  sumAllList[g] + tmpVal; // sum up
					
					exfile.createDataCell(1, 1 + frame, 1 + g, tmpVal); // save area size //TODO: ZZZ Area Size save
				} 
				
				if (frame == 0) {// Create functions label
					exfile.createDataCell(1, 0, 1 + g,  // Area Size Distance header  
							functionsLabel[g]); 
					exfile.createDataCell(2, 0, 1 + g,  //variance header
							functionsLabel[g]);
					
				}
				}

			if (frame == cluster.get(0).track.size() - 1) { // Write the area
															// average inside
															// the excelfile
				for (int k = 0; k < functionsLabel.length; k++) //TODO: ZZZ Average of each area save
					exfile.createDataCell(1, frame + 2, k + 1, sumAllList[k]/(frame-skip_Threshold_frames)); // -4 skip the first three values because of the threshold
			}

		}

		// Save number of frames
			saveFrameNo[lock] = cluster.get(0).track.size()-skip_Threshold_frames;
			
			// Calculate variance
			for (int frame = 0; frame < cluster.get(0).track.size(); frame++) { // Y
				for (int funcNr = 0; funcNr < functionsLabel.length; funcNr++) { // X axes
					
					//System.out.println("CEF: " + sumAllList[funcNr] + ", " + saveFrameNo[lock]);
					double meanValue = (sumAllList[funcNr]) / (saveFrameNo[lock] -1);
					//System.out.println("CEF: ########" + meanValue + ", " + funcNr + " ");
					//exfile.createDataCell(seite, zeile, spalte, wert)
					// cluster * 2 (two points) + 2 (label + space) + 49 (number of distance calc) 
					exfile.createDataCell(2, frame + 1, funcNr+1, exfile           // Calc the variance value
							.calcVariance(1, frame+1, funcNr+1, meanValue, lock)); // Area Size variance //TODO: ZZZ Area Size Variance Value save
					
					
					if(frame == (cluster.get(0).track.size()-1)) // last frame
					{
					//System.out.println("CEF: " + funcNr + ", " + lock);
						sumVariance[funcNr] = exfile           // Calc the variance value
						.createVarianceValue(saveFrameNo[lock], lock, funcNr+1);   // TODO: ZZZ Calc all Areas Variance Values save
						
						
						exfile.createDataCell(2, frame + 2, funcNr + 1, sumVariance[funcNr]); // TODO: ZZZ Save the results 
					}
				}
				
				if(frame == (cluster.get(0).track.size()-1)) // last frame save
				{
				//System.out.println("CEF: " + funcNr + ", " + lock);
					exfile.createDataCell(2, frame + 2, 0, "Variance");
				}
			}
			
			
			for(int g = 0; g < sumVariance.length; g++) {  // Calc Variance of this face
			          if(g < 4 && sumVariance[g] < 100) { // area variance  4 pix ~ 100 pixel no Variance 
			        	  emotionResult[7] =  emotionResult[7] + 14.28;
			          }
			          if(g >= 4 && sumVariance[g] < 1) {  // distance Variance
			        	  emotionResult[7] =  emotionResult[7] + 14.28; 
			          }
			          
			}
			
			
			// How many areas or distances need to be compared
			int list_teiler []  = {5,7, 7, 7, 7, 7, 7};
			
			
		// Calc emotion value	
			double testValue = 0;
			int list [][] = {{0,2,3,4},{1,5,6}, // erste spalte
					  {0,4,5}, {1,2,3,6}, 
					  {1,2,5,6},{3,4}, // Horizontal
					{4},{0,1,2,3,5,6},
					{2}, {0,1,3,4,5,6}, 
					{},{1,2,3,5,6},
					{4}, {0,1,3,5,6}}; // Rainers tabelle
			
			double addvalue; // how much should be added in relation of count of each row (area and distance)
			// e.g. 5 Values = 20 procent
			// Check equal values 
			double equalValue_surprise = Math.abs(firstPointsSumList[5]-sumAllList[5]/(saveFrameNo[lock]-skip_Threshold_frames));
			double equalValue_happy = Math.abs(firstPointsSumList[6]-sumAllList[6]/(saveFrameNo[lock]-skip_Threshold_frames));
			
			if (equalValue_surprise < 100) {  // equal check 100 smaller (no difference = equal)
				addvalue = 100/list_teiler [4];
				emotionResult [4] = emotionResult [4] + addvalue; 
			}
			if (equalValue_happy < 100) {   // equal check
				addvalue = 100/list_teiler[2];
				emotionResult [2] = emotionResult [2] + addvalue; 
			}
			
			//System.out.println("CEF: " + functionsLabel.length); // 7
			for (int g = 0; g < functionsLabel.length; g++) { // List of the areas and distances  || 6 rows
				
				testValue = sumAllList[g]/(saveFrameNo[lock]-1); // average value of each row
				firstPointsSumList[g] = firstPointsSumList[g]/(skip_Threshold_frames); 
				
				//System.out.println("CEF: " + firstPointsSumList[g] + "<>" + testValue);
				if(firstPointsSumList[g] < testValue) { // first value < average value   Steigung
					
					for(int i = 0; i < list[g*2].length; i++ ) { //  colls
						//System.out.println("CEF: " + list[g].length);
//
						addvalue = 100/list_teiler [list[g*2][i]]; // Select value
						//System.out.println("CEF: " + addvalue + ", i: " + list[g*2][i] + ", g: " + g);
					
						// add each procent value
						emotionResult [list[g*2][i]] = emotionResult [list[g*2][i]] + addvalue; 
						
						//System.out.println("CEF1: Spalte: "+ g + ", Zeile: " + i + ", Ergebnis "+  emotionResult [list[g*2][i]]);
					}
					
				} else {   // keine Steigung
					for(int i = 0; i < list[g*2+1].length; i++ ) {
						
						// Add procent of the 100 procent value
						addvalue = 100/list_teiler [list[g*2+1][i]];  
			
						// add ech procent value
						emotionResult [list[g*2+1][i]] = emotionResult [list[g*2+1][i]] + addvalue; 
						
						//System.out.println("CEF2: Spalte: "+ g + ", Zeile: " + i + ", Ergebnis "+  emotionResult [list[g*2+1][i]]);
					}
				}	
			}
			
			
			
			/*
			 * Display the results
			 * 
			 */
			
			
			
			double resultArray = 0;
			
			// Calc 100 procent
//			for (int k = 0; k < emotionResult.length; k++) {
//				resultArray = resultArray + emotionResult[k]; // sum up all values
//			}
			
			//double varianzeValue = (emotionResult[7]*100) / (resultArray); // Variance with 100% rate
			
			
			/*
			 * ###################################################################
			 * #Calculate the procent of each emotion in relation of the variance#
			 * ###################################################################
			 */
			//rintln("CEF: Varianz: " + emotionResult[7]);
			double vari = emotionResult[7]; // Variance 100% => no variance 1 % => full
			for (int i = 0; i < emotionResult.length; i++) {
				
				//emotionResult[i] = (emotionResult[i]*100) / (resultArray);  // calc 100 procent relation  // *varianzeValue
				if(i != emotionResult.length-1) {  // check if we have reached the end
				
					emotionResult[i] = 	(emotionResult[i]/(vari/100)); // wight it with the variance
					
					//System.out.println("CEF_END: " + i + ", " +  emotionResult[i]); //TODO last value delete	
				//System.out.println("CEFX: " + i + ", " +  emotionResult[i]); //TODO last value delete

				resultArray = resultArray + emotionResult[i]; // sum up all procent values to calculate the norm

				} else {
					
					emotionResult[i] = 	(emotionResult[i]*(Math.pow(vari, 2.0)/100)); // wight it with the variance
					resultArray = resultArray + emotionResult[i]; // sum up  for norm
				}
				
			}
			
			//System.out.println("CEF: RA: " + resultArray); 
			for (int k = 0; k < emotionResult.length; k++) {
				emotionResult[k] = ((emotionResult[k]*100)/resultArray); // calc the norm
				
				emotionResult[k] = exfile.setTwoDecimalPlaces(emotionResult[k], 2); // convert value in two digits
				//System.out.println("CEF: " + k + ", " +  emotionResult[k]); 
			}
			//System.out.println("CEF: " + varianzeValue + ", " +  ((emotionResult[i]*100) / (resultArray)) + ", " + varianzeValue*((emotionResult[i]*100) / (resultArray)));
			
			
			// Set the result string for the screen
			String results = "<html>" + "Anger (opened mouth): " + emotionResult[0] + "<br>" +
			"Anger (closed mouth): " + emotionResult[1] + "<br> " +
			"Happiness: " + emotionResult[2] + "<br> " +
			"Frightend: " + emotionResult[3] + "<br>" +
			"Surprise: " + emotionResult[4] + "<br>" +
			"Sadness: " + emotionResult[5] + "<br> " +
			"Disgust: " + emotionResult[6] + "<br> " +
			"Neutral: " + emotionResult[7] + "</html>";
					
			//System.out.println("CEF:"+ results);
			
			// Starting a new Thread for the result display
			result = new Result(results, emotionResult);
			new Thread(result).start();
			

		// close workspace file and write it
		try {
			exfile.export();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
