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

package org.apdplat.module.system.action;

import org.apdplat.platform.action.DefaultAction;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.FileUtils;
import org.apdplat.platform.util.Struts2Utils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apdplat.platform.log.APDPlatLoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
/**
*
* @author 杨尚川
*/
@Scope("prototype")
@Controller
@Namespace("/system")
public class UploadImageAction extends DefaultAction {
    private static final APDPlatLogger LOG = APDPlatLoggerFactory.getAPDPlatLogger(UploadImageAction.class);
    
    //上传
    private static int BUFFER_SIZE=1024*100*8;
    private static String uploadPath="/platform/upload";
    private String path=null;

    private File photo;
    private String photoContentType;
    private String photoFileName;

    public String photoPath(){
        String result=ServletActionContext.getRequest().getSession().getAttribute("path").toString();
        Struts2Utils.renderText(result);
        return null;
    }

    public String upload(){
        try{
            processPhotoFile();
            ServletActionContext.getRequest().getSession().setAttribute("path", path);
            Struts2Utils.renderHtml("true");
        }catch(Exception e){
            Struts2Utils.renderHtml("false");
        }
        return null;
    }
    public String delete(){
        try{
            deletePhotoFile();
            Struts2Utils.renderText("true");
        }catch(Exception e){
            Struts2Utils.renderText("false");
        }
        return null;
    }
    private void deletePhotoFile(){
        if(path==null || "".equals(path)) {
            return;
        }
        File file=new File(FileUtils.getAbsolutePath(path));
        file.delete();
    }
    private static String getExtention(String fileName) {
        int pos = fileName.lastIndexOf( "." );
        return fileName.substring(pos);
   }
   private static String getFileName(String fileName) {
        int pos = fileName.lastIndexOf( "." );
        return fileName.substring(0,pos);
   }
    //上传
   private void processPhotoFile(){
       SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
       File photoPath=new File(FileUtils.getAbsolutePath(uploadPath));
       if(!photoPath.exists()) {
           photoPath.mkdir();
       }

       String newPhotoFileName = getFileName(this.getPhotoFileName())+"_"+df.format(new Date()) + getExtention(this.getPhotoFileName());
       path=uploadPath+"/"+newPhotoFileName;
       File photoFile = new File(FileUtils.getAbsolutePath(path));
       copy(this.getPhoto(), photoFile);
   }

   private static void copy(File src, File dst) {
        try {
            InputStream in = null ;
            OutputStream out = null ;
            try {
                    in = new BufferedInputStream( new FileInputStream(src), BUFFER_SIZE);
                    out = new BufferedOutputStream( new FileOutputStream(dst), BUFFER_SIZE);
                    byte [] buffer = new byte [BUFFER_SIZE];
                    while (in.read(buffer) > 0 ) {
                        out.write(buffer);
                    }
            } finally {
                    if ( null != in) {
                        in.close();
                    }
                    if ( null != out) {
                        out.close();
                    }
            }
        } catch (Exception e) {
            LOG.error("生成验证码出错",e);
        }
   }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }
}