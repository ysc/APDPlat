package com.apdplat.module.system.service;

import com.apdplat.platform.log.APDPlatLogger;
import com.apdplat.platform.util.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class ExcelService {
    protected static final APDPlatLogger log = new APDPlatLogger(ExcelService.class);

    private static String outputFile = null;

    /**
     * 将数据写入EXCEL表格中
     * @param data
     * @param xlsName
     * @return 
     */
    public String write(List<List<String>> data, String xlsName) {
        String path = "/platform/temp/excel/" + System.currentTimeMillis();
        outputFile = FileUtils.getAbsolutePath(path);
        try {
            HSSFWorkbook workbook = null;
            HSSFSheet sheet = null;
            HSSFRow row = null;
            HSSFCell cell = null;
            String table_name = "导出数据";

            //创建新的Excel 工作簿
            workbook = new HSSFWorkbook();
            //在Excel工作簿中建一工作表，其名为缺省值
            sheet = workbook.createSheet(table_name);
            int rowNum = data.size();
            for (int i = 0; i < rowNum; i++) {
                row = sheet.createRow(i);
                List<String> rowData = data.get(i);
                int j = 0;
                try{
                    for (String value : rowData) {
                        cell = row.createCell(j);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(value == null ? "" : value.toString());
                        j++;
                    }
                }catch(Exception e){
                    log.error("生成EXCEL出错",e);
                }
            }
            File dir = new File(outputFile);
            dir.mkdirs();
            File file = new File(dir, xlsName);
            file.createNewFile();
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
                out.flush();
            }
            log.info("共导出" + (rowNum - 1) + "条数据");
        } catch (Exception e) {
            log.error("生成EXCEL出错",e);
        }
        return path + "/" + xlsName;
    }
}
