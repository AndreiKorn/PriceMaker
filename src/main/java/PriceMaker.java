import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by KORN on 23.03.2018
 */
public class PriceMaker {
    public static void main(String[] args) {
        PriceMaker priceMaker = new PriceMaker();
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) {
            Map<String, ProductInfo> goods = priceMaker.getInfo(scanner.nextLine());
            priceMaker.writeInfo("result.xls", goods);
        }
    }

    /**
     * Method parses the file, which name is gotten as a param. The file should be preprocesses and has next structure:
     * product_name (String) | amount (numeric) | price (numeric)
     * @param fileName - name of file, which should be parsed
     * @return a map, which contains productName as a key and a productInfo object with info about amount and price
     */
    public Map<String, ProductInfo> getInfo(String fileName) {
        Map<String, ProductInfo> goods = new HashMap<>();
        try {
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(new File(fileName)));
            HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
            Iterator<Row> rowIterator = myExcelSheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String productName = row.getCell(0).getStringCellValue();
                int amount = (int) row.getCell(1).getNumericCellValue();
                double price = row.getCell(2).getNumericCellValue();
                goods.put(productName, new ProductInfo(amount, price));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return goods;
    }

    public void writeInfo(String fileName, Map<String, ProductInfo> goods) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("products");

        int rowNum = 0;
        for (Map.Entry<String, ProductInfo> entry : goods.entrySet()) {
            ProductInfo productInfo = entry.getValue();
            int amount = productInfo.getAmount();
            for (int i = 0; i < amount; i++) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(Math.round(productInfo.getPrice() * 1.8));
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ProductInfo {
        private int amount;
        private double price;

        public ProductInfo(int amount, double price) {
            this.amount = amount;
            this.price = price;
        }

        public int getAmount() {
            return amount;
        }

        public double getPrice() {
            return price;
        }
    }
}
