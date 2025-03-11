package com.sankuai.inf.leaf.common;

public class Result {
    private long id;
    private Status status;
    private int length;

    public Result(long id, Status status, int length) {
        this.id = id;
        this.status = status;
        this.length = length;
    }

    public Result(long id, Status status) {
        this.id = id;
        this.status = status;
    }

    public String getIdString() {
        String str = Long.toString(id);
        StringBuilder builder = new StringBuilder(length);
        builder.append(str);
        for (int i = 0; i < length - str.length(); i++) {
            builder.insert(i, "0");
        }
        return builder.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Result{");
        sb.append("id=").append(id);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
