import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Description: Creating and filling with data an excel file. <br>
 * Author: T. Tews<br>
 * Last change: 21.06.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class ExcelFile {

	private File destination;

	private Workbook wb;
	private Row row;
	private CreationHelper createHelper;

	private double retval = 0.0;
	static int [][] histogramm = new int[9][300]; //TODO MoreVideos
	static int [][] histogramm2 = new int[9][300]; //TODO MoreVideos
	//private double varianceValue; // Variance Value
	static double [][] varianceValueSum = new double[3][9]; // Variance sum //TODO MoreVideos

	static final int ANALYSIS_SHEET = 0;
	static final int INFO_SHEET = 1;

	static CellStyle BOLD;
	static CellStyle ITALIC;

	static CellStyle RIGHT;
	static CellStyle LEFT;
	static CellStyle CENTER;
	static Font f2;

	private double averageOfTheCol; // Function Average

	/**
	 * Generate the excel file with two sheets
	 * 
	 * @param destination
	 */
	public ExcelFile(File destination) {
		this.destination = destination;

		String extension = destination.getName().substring(
				destination.getName().lastIndexOf('.') + 1,
				destination.getName().length());

		if (extension.equals("xls"))
			wb = new HSSFWorkbook();
		else
			wb = new XSSFWorkbook();

		wb.createSheet("Point-Data"); // index 0
		wb.createSheet("Area-Size_Distance"); // index 1
		wb.createSheet("Varianz"); // index 2

		
		f2  = wb.createFont();

		createHelper = wb.getCreationHelper();

		generateCellStyles();
		generateCellAlignments();
	}

	/**
	 * Generate Cell Styles...
	 */
	private void generateCellStyles() {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();

		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		BOLD = style;

		style = wb.createCellStyle();
		font = wb.createFont();
		font.setItalic(true);
		style.setFont(font);
		ITALIC = style;
	}

	/**
	 * Generate Cell Alignments...
	 */
	private void generateCellAlignments() {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		RIGHT = style;

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		LEFT = style;

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		CENTER = style;
	}

	/**
	 * this method exports excel file to hard disk
	 * 
	 * @throws IOException
	 */
	public void export() throws IOException {

		FileOutputStream fileOut;

		/* open output stream to file */
		fileOut = new FileOutputStream(destination);

		/* write file to disk */
		wb.write(fileOut);

		/* close output stream */
		fileOut.close();
	}

	public File getDestination() {
		return destination;
	}

	/**
	 * Creates row with given id in given sheet. Only called implicitly.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet to write to
	 * @param rowId
	 *            int which specifies the row to write to
	 */
	private Row createDataRow(int sheetIndex, int rowId) {
		return wb.getSheetAt(sheetIndex).createRow(rowId);
	}

	/**
	 * Creates data cell with String value.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet to write to
	 * @param rowId
	 *            int which specifies the row to write to
	 * @param colId
	 *            int which specifies the col to write to
	 * @param value
	 */
	public void createDataCell(int sheetIndex, int rowId, int colId,
			String value) {
		if (wb.getSheetAt(sheetIndex).getRow(rowId) != null)
			row = wb.getSheetAt(sheetIndex).getRow(rowId);
		else {
			row = createDataRow(sheetIndex, rowId);
		}

		CellStyle cellStyle = wb.createCellStyle();

		// Create the first row bold
		Font f = wb.createFont();
		f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// f.setItalic(true);
		cellStyle.setFont(f);

		Cell cell = row.createCell(colId);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);

		// System.out.println("EF: string");
	}

	/**
	 * Creates data cell with double value.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet to write to
	 * @param rowId
	 *            int which specifies the row to write to
	 * @param colId
	 *            int which specifies the col to write to
	 * @param value
	 */
	public void createDataCell(int sheetIndex, int rowId, int colId,
			double value) {
		if (wb.getSheetAt(sheetIndex).getRow(rowId) != null)
			row = wb.getSheetAt(sheetIndex).getRow(rowId);
		else {
			row = createDataRow(sheetIndex, rowId);
		}
		row.createCell(colId).setCellValue(value);

		// System.out.println("EF: double");
	}

	/**
	 * Creates data cell with int value.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet to write to
	 * @param rowId
	 *            int which specifies the row to write to
	 * @param colId
	 *            int which specifies the col to write to
	 * @param value
	 */
	public void createDataCell(int sheetIndex, int rowId, int colId, int value) {
		if (wb.getSheetAt(sheetIndex).getRow(rowId) != null)
			row = wb.getSheetAt(sheetIndex).getRow(rowId);
		else {
			row = createDataRow(sheetIndex, rowId);
		}

		CellStyle cellStyle = wb.createCellStyle();

		if (colId == 0) { // Create the first col bold
			f2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			cellStyle.setFont(f2);
		}

		Cell cell = row.createCell(colId);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

	/**
	 * Creates data cell with long value.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet to write to
	 * @param rowId
	 *            int which specifies the row to write to
	 * @param colId
	 *            int which specifies the col to write to
	 * @param value
	 */
	public void createDataCell(int sheetIndex, int rowId, int colId, long value) {
		if (wb.getSheetAt(sheetIndex).getRow(rowId) != null)
			row = wb.getSheetAt(sheetIndex).getRow(rowId);
		else {
			row = createDataRow(sheetIndex, rowId);
		}
		row.createCell(colId).setCellValue(value);

		// System.out.println("EF: long");
	}

	/**
	 * Creates data cell with date value.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet to write to
	 * @param rowId
	 *            int which specifies the row to write to
	 * @param colId
	 *            int which specifies the col to write to
	 * @param date
	 */
	public void createDataCell(int sheetIndex, int rowId, int colId, Date date) {
		if (wb.getSheetAt(sheetIndex).getRow(rowId) != null)
			row = wb.getSheetAt(sheetIndex).getRow(rowId);
		else {
			row = createDataRow(sheetIndex, rowId);
		}

		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
				"HH:mm:ss.000"));

		Cell cell = row.createCell(colId);
		cell.setCellValue(date);
		cell.setCellStyle(cellStyle);

		// System.out.println("EF: date");
	}

	/**
	 * Creates formula cell with String value for formula.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet to write to
	 * @param rowId
	 *            int which specifies the row to write to
	 * @param colId
	 *            int which specifies the col to write to
	 * @param formula
	 *            String which contains a cell formula for Excel (without
	 *            leading '=')
	 */
	public void createFormulaCell(int sheetIndex, int rowId, int colId,
			String formula) {
		if (wb.getSheetAt(sheetIndex).getRow(rowId) != null)
			row = wb.getSheetAt(sheetIndex).getRow(rowId);
		else {
			row = createDataRow(sheetIndex, rowId);
		}

		Cell cell = row.createCell(colId);
		cell.setCellType(Cell.CELL_TYPE_FORMULA);
		cell.setCellFormula(formula);

		// System.out.println("EF: formula");
	}

	/**
	 * Creates formula (reference) to specified cell in specified sheet. ColId
	 * is converted to corresponding letter: 1 = A, 2 = B, etc. Formula looks
	 * like "SheetName!A5" by params "SheetName", 5, 1.
	 * 
	 * @param sheetIndex
	 *            int which specifies the sheet
	 * @param rowId
	 *            int which specifies the row
	 * @param colId
	 *            int which specifies the col
	 */
	public String convertReferenceToAlpha(int sheetIndex, int rowId, int colId) {
		String refSheet = wb.getSheetName(sheetIndex);

		CellReference ref = new CellReference(rowId, colId);
		String refCell = ref.formatAsString();

		return "'" + refSheet + "'!" + refCell;
	}

	/**
	 * read cells
	 * 
	 * @param sheetIndex
	 * @param rowId
	 * @param colId
	 */
	public int readCell(int sheetIndex, int rowId, int colId) {

		row = wb.getSheetAt(sheetIndex).getRow(rowId);
		Cell cell = row.getCell(colId);
		return (int) cell.getNumericCellValue();
	}

	/**
	 * calculate the distance of four points
	 * 
	 * @param sheetIndex
	 * @param rowId
	 * @param colIdValue1
	 * @param colIdValue2
	 * @param colIdValue3
	 * @param colIdValue4
	 * @return
	 */
	public double calcDistance(int sheetIndex, int rowId, int valueloc0,
			int valueloc2) {

		int valueloc1 = valueloc0 + 1; // y1
		int valueloc3 = valueloc2 + 1; // y2
		
		// wurzel((X1 - X2)^2 + (Y1 - Y2)^2)
		retval = Math
				.sqrt(Math
						.pow(
								(getCellValue(sheetIndex, rowId, valueloc0) - getCellValue(
										sheetIndex, rowId, valueloc2)), 2.0)
						+ Math
								.pow(
										(getCellValue(sheetIndex, rowId, valueloc1) - getCellValue(
												sheetIndex, rowId, valueloc3)),
										2.0));

		// two decimalPlaces only
		retval = setTwoDecimalPlaces(retval, 2);

		return retval;
	}

	/**
	 * calculate the area of four dots
	 * 
	 * @param sheetIndex
	 * @param rowId
	 * @param colIdValue1
	 * @param colIdValue2
	 * @param colIdValue3
	 * @param colIdValue4
	 * @param colIdValue5
	 * @param colIdValue6
	 * @return
	 */
	public double calcArea(int sheetIndex, int rowId,
			int valueloc0, int valueloc2, int valueloc4, int valueloc6) {
		
		int valueloc1 = valueloc0 + 1; // y1
		int valueloc3 = valueloc2 + 1; // y2
		int valueloc5 = valueloc4 + 1; // y3
		int valueloc7 = valueloc6 + 1; // y4
		
		// A = 1/2 |(y1 -y3)*(x4-x2)+(y2-y4)*(x1-x3)| ~ (1-5)*(6-2)+(3-7)*(0-4) // gaussschen Trapezformel
		retval = 0.5* Math.abs((getCellValue(sheetIndex, rowId, valueloc1)- getCellValue(sheetIndex, rowId, valueloc5))*
				(getCellValue(sheetIndex, rowId, valueloc6)- getCellValue(sheetIndex, rowId, valueloc2))+
						(getCellValue(sheetIndex, rowId, valueloc3)- getCellValue(sheetIndex, rowId, valueloc7))*
						(getCellValue(sheetIndex, rowId, valueloc0)- getCellValue(sheetIndex, rowId, valueloc4)));
		

		// two decimalPlaces only
		return setTwoDecimalPlaces(retval, 0);
	}
	
	/**
	 * calculate the triangle of three dots
	 * 
	 * @param sheetIndex
	 * @param rowId
	 * @param colIdValue1
	 * @param colIdValue2
	 * @param colIdValue3
	 * @return
	 */
	public double calcTriangle(int sheetIndex, int rowId,
			int valueloc0, int valueloc2, int valueloc4) {
		
		int valueloc1 = valueloc0 + 1; // y1
		int valueloc3 = valueloc2 + 1; // y2
		int valueloc5 = valueloc4 + 1; // y3
		
		// A = 1/2 |x1*(y2-y3)+x2(y3-y1)+x3(y1-y2)|
		retval = 0.5* (getCellValue(sheetIndex, rowId, valueloc0)*(getCellValue(sheetIndex, rowId, valueloc3)-
				getCellValue(sheetIndex, rowId, valueloc5))+getCellValue(sheetIndex, rowId, valueloc2)
				*(getCellValue(sheetIndex, rowId, valueloc5)-getCellValue(sheetIndex, rowId, valueloc1))
				+getCellValue(sheetIndex, rowId, valueloc4)*(getCellValue(sheetIndex, rowId, valueloc1)
				-getCellValue(sheetIndex, rowId, valueloc3)));

		// two decimalPlaces only
		return setTwoDecimalPlaces(retval, 0);
	}

	/**
	 * Calculate the last NP values
	 * 
	 * @param valueloc0
	 * @param valueloc1
	 * @param valueloc2
	 */
	public double calcND(int sheetIndex, int clustersize, int rowId,
			int valueloc0, int valueloc1, int valueloc2) {

		// (1/4) * SQRT ((AY3 + CH3 + CI3)*(AY3 + CH3 - CI3)*(-AY3 + CH3 +
		// CI3)*(AY3 - CH3 + CI3) )
		retval = 0.25 * Math.sqrt((getCellValue(sheetIndex, rowId,
				(clustersize * 2) + 1 + valueloc0)
				+ getCellValue(sheetIndex, rowId, (clustersize * 2) + 1
						+ valueloc1) + getCellValue(sheetIndex, rowId,
				(clustersize * 2) + 1 + valueloc2))
				* (getCellValue(sheetIndex, rowId, (clustersize * 2) + 1
						+ valueloc0)
						+ getCellValue(sheetIndex, rowId, (clustersize * 2) + 1
								+ valueloc1) - getCellValue(sheetIndex, rowId,
						(clustersize * 2) + 1 + valueloc2))
				* (-getCellValue(sheetIndex, rowId, (clustersize * 2) + 1
						+ valueloc0)
						+ getCellValue(sheetIndex, rowId, (clustersize * 2) + 1
								+ valueloc1) + getCellValue(sheetIndex, rowId,
						(clustersize * 2) + 1 + valueloc2))
				* (getCellValue(sheetIndex, rowId, (clustersize * 2) + 1
						+ valueloc0)
						- getCellValue(sheetIndex, rowId, (clustersize * 2) + 1
								+ valueloc1) + getCellValue(sheetIndex, rowId,
						(clustersize * 2) + 1 + valueloc2)));

		// two decimalPlaces only
		return setTwoDecimalPlaces(retval, 2);
	}
	
	/**
	 * Calc Area Average with 100% relation
	 * 
	 * @param sheetIndex
	 * @param rowIdStart
	 * @param colIdStart
	 * @param rowIdEnd
	 * @param colIdEnd
	 */
	public double calcAverageRelation(int sheetIndex, int rowId, int colId,
			double averValue, int lockparam) {
		
		averageOfTheCol = averValue;
		
		//averageOfTheCol = setTwoDecimalPlaces(averageOfTheCol, 2);
		 //System.out.println("EF: " + averageOfTheCol); //  + ", CV: " +
		 //System.out.println(getCellValue(sheetIndex, rowId, colId));

		// Average/(Value/100) Calc Area with the 100% comp.
		double retvalue = setTwoDecimalPlaces(averageOfTheCol
				/ ((getCellValue(sheetIndex, rowId, colId)) / 100), 2);
		
		// Set no decimal Places
		int ret = (int) retvalue;
		if(ret < 300 && ret > 0) { // save your data
			setHistogramm(ret, colId, lockparam);
		}
		

		return ret;
	}
	
	/**
	 * Calc Area Average with 100% relation
	 * 
	 * @param sheetIndex
	 * @param rowIdStart
	 * @param colIdStart
	 * @param rowIdEnd
	 * @param colIdEnd
	 */	
	public void createHistogramm(int sheet, int k, int h) {
		
		//exfile.createDataCell(seite, zeile, spalte, wert)
		
		//System.out.println("EF: " + k + ", " + h);
		if (sheet != 4)
			createDataCell(sheet,h,k+1,histogramm[k][h]);
		else 
			createDataCell(sheet,h,k+1,histogramm2[k][h]);
//		for(int h = 0; h < 300; h++){
//			createDataCell(2, h, 0, h);
//		}
	
	}

	/**
	 * Set Histogramm Value
	 * 
	 * @param k
	 * @param colIdEnd
	 * @param lockpar   
	 */	
	public static void setHistogramm(int k, int colId, int lockpar) {
		int lockparam = lockpar;
		//System.out.println("EF:" + colId);
		
		if(lockparam !=3) // neutral video
			histogramm [colId][k] = histogramm [colId][k] + 1; // Hier wird hochgezaehlt pro gleicher Wert in dem Array + 1
		else // emotional video
			histogramm2 [colId][k] = histogramm2 [colId][k] + 1; // Hier wird hochgezaehlt pro gleicher Wert in dem Array + 1

	}

	/**
	 * Calc to two decimal places
	 * 
	 * @param double value
	 * 
	 * @return double value
	 */
	public double setTwoDecimalPlaces(double deb, int places) {

		// two decimalPlaces only
		int decimalPlaces = places;

		// TODO: NaN & Infinite
		if (!(Double.isNaN(deb)) && !(Double.isInfinite(deb))) {
			BigDecimal bd = new BigDecimal(deb);
			// setScale is immutable
			bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_DOWN);
			deb = bd.doubleValue();
		} else {
			deb = 0;
		}

		return deb;
	}

	/**
	 * Calc Area Average with 100% relation
	 * 
	 * @param sheetIndex
	 * @param rowIdStart
	 * @param colIdStart
	 * @param rowIdEnd
	 * @param colIdEnd
	 */
	public double calcAverageRelation(int sheetIndex, int rowId, int colId,
			double averValue) {

		averageOfTheCol = averValue;
		// System.out.println("EF0: " + averageOfTheCol + ", CV: " +
		// getCellValue(sheetIndex, rowId, colId));

		// Average/(Value/100) Calc Area with the 100% comp.
		double retvalue = setTwoDecimalPlaces(averageOfTheCol
				/ ((getCellValue(sheetIndex, rowId, colId)) / 100), 2);

		return retvalue;
	}
	
	/**
	 * Calc Variance
	 * 
	 * @param sheetIndex
	 * @param rowIdStart
	 * @param colIdStart
	 * @param rowIdEnd
	 * @param colIdEnd
	 */
	public double calcVariance(int sheetIndex, int rowId, int colId,
			double averValue, int lockparam) {
		
		averageOfTheCol = averValue;
		
//		 System.out.println("EF: " + averageOfTheCol  + ", CV: " +
//		 getCellValue(sheetIndex, rowId, colId) + ", Frame: " + rowId);

		// Average/(Value/100) Calc Area with the 100% comp.
		double retvalue = setTwoDecimalPlaces(Math.sqrt(Math.pow((getCellValue(sheetIndex, rowId, colId) - averageOfTheCol),2.0)), 2);
		
		//System.out.println("EF: Ret: "+ retvalue +", LOK: "+ lockparam +", C: " + colId);
		
		setVarianceValue(retvalue, lockparam, colId); // save variance for sum up
		
		// Set no decimal Places
		int ret = (int) retvalue;
		

		return ret;
	}
	
	/**
	 * Set Variance Sum up Value
	 * 
	 * @param variance value   
	 */	
	public double createVarianceValue(int frameNo, int lockpa, int col) {
		
		double retvalue = setTwoDecimalPlaces(varianceValueSum[lockpa][col]/frameNo, 2);
		//System.out.println("EF: " + retvalue);

		return retvalue;
	}
	
	/**
	 * Set Variance Sum up Value
	 * 
	 * @param variance value   
	 */	
	public static void setVarianceValue(double ret, int lockpa, int col) {
		
		varianceValueSum[lockpa][col] = varianceValueSum[lockpa][col] + ret;
	}
	

	/**
	 * Get Cell Value
	 * 
	 * @param sheetIndex
	 * @param rowId
	 * @param colId
	 * @return
	 */
	public double getCellValue(int sheetIndex, int rowId, int colId) {
		row = wb.getSheetAt(sheetIndex).getRow(rowId);
		Cell cell = row.getCell(colId);
		return cell.getNumericCellValue();
	}

}
