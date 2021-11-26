package io.virtualan.cucumblan.script;

import io.virtualan.util.Helper;
import java.io.IOException;

import java.util.Map;
import java.util.Random;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelAndMathHelper {

    public static Object evaluateWithVariables(Class type, String formula, Map<String, String> contextObject) throws Exception {
        return evaluate(type, Helper.getActualValueForAll(formula,contextObject).toString());
    }

    public static Object evaluate(Class type, String formula) throws Exception {
        Object cellValue = new Object();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        FormulaEvaluator evaluator = workbook.getCreationHelper()
            .createFormulaEvaluator();
        Row row = sheet.createRow(new Random().nextInt(100));
        Cell cell = row.createCell(new Random().nextInt(100));
        cell.setCellFormula(formula);
        if (cell.getCellType() == CellType.FORMULA) {
            CellType cellType = evaluator.evaluateFormulaCell(cell);
            if(type.getName().equals("java.lang.Boolean") && cellType == CellType.BOOLEAN) {
                cellValue = cell.getBooleanCellValue();
                return cellValue;
            } else if(type.getName().equals("java.lang.Integer") && cellType == CellType.NUMERIC) {
                cellValue = (int)cell.getNumericCellValue();
                return cellValue;
            } else if(type.getName().equals("java.lang.Double") && cellType == CellType.NUMERIC) {
                cellValue = cell.getNumericCellValue();
                return cellValue;
            } else if(type.getName().equals("java.lang.String") && cellType == CellType.STRING) {
                cellValue = cell.getStringCellValue();
                return cellValue;
            }
        }
        workbook.close();
        throw new Exception(formula +"is not resolved for type > " + type.getName());
    }
}