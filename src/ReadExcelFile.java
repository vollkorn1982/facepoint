import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;



/**
 * Description: Reading one excelfile <br>
 * Author: T. Tews<br>
 * Last change: 04.10.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class ReadExcelFile {

	private String fileName;
	

//	public static void main(String[] args) throws IOException {
//		ReadExcelFile.readCell();
//	}
	
	public double readCell(String fName, int sht, int rowId, int colId) throws IOException{
		

	fileName = fName;	
	HSSFWorkbook wb = readFile(fileName);
	
	int selectedSheet = sht;
	
	HSSFSheet sheet = wb.getSheetAt(selectedSheet);
	
	HSSFRow row = sheet.getRow(rowId);
	HSSFCell cell = row.getCell(colId);
	//System.out.println(cell.getNumericCellValue());
	//cell.setCellValue("MODIFIED CELL!!!!!");


	return cell.getNumericCellValue();
	}
	
	private HSSFWorkbook readFile(String filename) throws IOException {
		return new HSSFWorkbook(new FileInputStream(filename));
	}

	
}
