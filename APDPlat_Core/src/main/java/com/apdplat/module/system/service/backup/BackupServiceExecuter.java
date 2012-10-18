package com.apdplat.module.system.service.backup;

import com.apdplat.module.system.service.PropertyHolder;
import com.apdplat.platform.util.SpringContextUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author ysc
 */
@Service
public class BackupServiceExecuter extends BackupService{  
    private BackupService backupService=null;
    @Override
    public boolean backupImpl() {
        if(backupService==null){
            backupService=SpringContextUtils.getBean(PropertyHolder.getProperty("jpa.database"));
        }
        return backupService.backupImpl();
    }

    @Override
    public boolean restoreImpl(String date) {
        if(backupService==null){
            backupService=SpringContextUtils.getBean(PropertyHolder.getProperty("jpa.database"));
        }
        return backupService.restoreImpl(date);
    }    
}
