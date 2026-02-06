package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.models.ModelPullStatus;

@RegisterForReflection
public class PullResponse {
    private String status;
    private String digest;
    private Long total;
    private Long completed;
    private String error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getProgressPercent() {
        if (total == null || total == 0 || completed == null) {
            return 0;
        }
        return (int) ((completed * 100) / total);
    }

    public ModelPullStatus toModelPullStatus() {
        // If there's an error, return a failed status
        if (error != null && !error.isEmpty()) {
            return new ModelPullStatus("failed: " + error);
        }

        ModelPullStatus status = new ModelPullStatus(this.status);
        status.setTotal(this.total);
        status.setCompleted(this.completed);
        return status;
    }
}
