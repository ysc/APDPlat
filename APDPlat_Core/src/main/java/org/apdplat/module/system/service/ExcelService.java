/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.module.system.service;

import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author 杨尚川
 */
@Service
public class ExcelService {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(ExcelService.class);

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
                    LOG.error("生成EXCEL出错",e);
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
            LOG.info("共导出" + (rowNum - 1) + "条数据");
        } catch (Exception e) {
            LOG.error("生成EXCEL出错",e);
        }
        return path + "/" + xlsName;
    }
}