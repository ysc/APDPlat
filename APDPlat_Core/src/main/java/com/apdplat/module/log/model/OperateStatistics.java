package com.apdplat.module.log.model;

/**
 *
 * @author ysc
 */
public class OperateStatistics {
    private String username;
    private int addCount;
    private int deleteCount;
    private int updateCount;
    
    public void increaseAddCount(){
        addCount++;
    }
    
    public void increaseDeleteCount(){
        deleteCount++;
    }
    
    public void increaseUpdateCount(){
        updateCount++;
    }
    
    public int getAddCount() {
        return addCount;
    }

    public void setAddCount(int addCount) {
        this.addCount = addCount;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
