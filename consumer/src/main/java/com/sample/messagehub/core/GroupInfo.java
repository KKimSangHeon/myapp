package com.sample.messagehub.core;

import java.util.Date;

public class GroupInfo {

    int groupId;
    int actId;
    long partitionId;
    long offset;
    int retry_count;
    String status;
    String app;
    String app_inst;
    Date created_time;
    Date inserted_time;

    public GroupInfo(int groupId, int actId) {
        this.actId = actId;
        this.groupId = groupId;
    }

    public GroupInfo(int groupId) {
        this.groupId = groupId;
    }

    public GroupInfo() {

    }

    public int getActId() {
        return actId;
    }

    public void setActId(int actId) {
        this.actId = actId;
    }

    public long getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(long partitionId) {
        this.partitionId = partitionId;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getRetry_count() {
        return retry_count;
    }

    public void setRetry_count(int retry_count) {
        this.retry_count = retry_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getApp_inst() {
        return app_inst;
    }

    public void setApp_inst(String app_inst) {
        this.app_inst = app_inst;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Date getInserted_time() {
        return inserted_time;
    }

    public void setInserted_time(Date inserted_time) {
        this.inserted_time = inserted_time;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
