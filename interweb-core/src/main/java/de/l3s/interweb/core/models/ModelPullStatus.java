package de.l3s.interweb.core.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ModelPullStatus {
    final private String status;
    private Long total;
    private Long completed;

    public ModelPullStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getCompleted() {
        return completed;
    }

    public void setCompleted(Long completed) {
        this.completed = completed;
    }

    public int getProgressPercent() {
        if (total == null || total == 0 || completed == null) {
            return 0;
        }
        return (int) ((completed * 100) / total);
    }
}
