package de.l3s.interweb.core.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.core.models.UsagePrice;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class Usage {
    public static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;

    @JsonProperty("prompt_tokens")
    private int promptTokens;
    @JsonProperty("prompt_tokens_details")
    private PromptTokensDetails promptTokensDetails;
    @JsonProperty("completion_tokens")
    private int completionTokens;
    @JsonProperty("completion_tokens_details")
    private CompletionTokensDetails completionTokensDetails;
    @JsonProperty("total_tokens")
    private int totalTokens;

    @JsonProperty("estimated_cost")
    private double estimatedCost;
    @JsonProperty("cost_details")
    private CostDetails costDetails;

    /**
     * Time spent in the queue before processing started (in nanoseconds)
     */
    @JsonIgnore
    private long queueTime;
    /**
     * Time spent loading the model (in nanoseconds)
     */
    @JsonIgnore
    private long loadTime;
    /**
     * Time spent evaluating the prompt (in nanoseconds)
     */
    @JsonIgnore
    private long promptTime;
    /**
     * Time spent generating the response (in nanoseconds)
     */
    @JsonIgnore
    private long completionTime;
    /**
     * Total processing time to generate the response (in nanoseconds)
     */
    @JsonIgnore
    private long totalTime;

    public Usage() {
    }

    public Usage(int promptTokens, int completionTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = promptTokens + completionTokens;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(int promptTokens) {
        this.promptTokens = promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(int completionTokens) {
        this.completionTokens = completionTokens;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    @JsonProperty("queue_time")
    public double getQueueTimeInSeconds() {
        return (double) queueTime / NANOSECONDS_PER_SECOND;
    }

    public long getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(long queueTime) {
        this.queueTime = queueTime;
    }

    @JsonProperty("load_time")
    public double getLoadTimeInSeconds() {
        return (double) loadTime / NANOSECONDS_PER_SECOND;
    }

    public long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }

    @JsonProperty("prompt_time")
    public double getPromptTimeInSeconds() {
        return (double) promptTime / NANOSECONDS_PER_SECOND;
    }

    public long getPromptTime() {
        return promptTime;
    }

    public void setPromptTime(long promptTime) {
        this.promptTime = promptTime;
    }

    @JsonProperty("completion_time")
    public double getCompletionTimeInSeconds() {
        return (double) completionTime / NANOSECONDS_PER_SECOND;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    @JsonProperty("total_time")
    public double getTotalTimeInSeconds() {
        return (double) totalTime / NANOSECONDS_PER_SECOND;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public CompletionTokensDetails getCompletionTokensDetails() {
        return completionTokensDetails;
    }

    public void setCompletionTokensDetails(CompletionTokensDetails completionTokensDetails) {
        this.completionTokensDetails = completionTokensDetails;
    }

    public PromptTokensDetails getPromptTokensDetails() {
        return promptTokensDetails;
    }

    public void setPromptTokensDetails(PromptTokensDetails promptTokensDetails) {
        this.promptTokensDetails = promptTokensDetails;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public CostDetails getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(CostDetails costDetails) {
        this.costDetails = costDetails;
    }

    public void add(Usage other) {
        if (other == null) {
            return;
        }
        this.promptTokens += other.promptTokens;
        this.completionTokens += other.completionTokens;
        this.totalTokens += other.totalTokens;
        this.estimatedCost += other.estimatedCost;

        this.queueTime += other.queueTime;
        this.loadTime += other.loadTime;
        this.promptTime += other.promptTime;
        this.completionTime += other.completionTime;
        this.totalTime += other.totalTime;

        if (other.promptTokensDetails != null) {
            if (this.promptTokensDetails == null) {
                this.promptTokensDetails = new PromptTokensDetails();
            }
            this.promptTokensDetails.add(other.promptTokensDetails);
        }

        if (other.completionTokensDetails != null) {
            if (this.completionTokensDetails == null) {
                this.completionTokensDetails = new CompletionTokensDetails();
            }
            this.completionTokensDetails.add(other.completionTokensDetails);
        }

        if (other.costDetails != null) {
            if (this.costDetails == null) {
                this.costDetails = new CostDetails();
            }
            this.costDetails.add(other.costDetails);
        }
    }

    public void updateCost(UsagePrice usagePrice) {
        if (usagePrice != null && !usagePrice.equals(UsagePrice.FREE)) {
            CostDetails costDetails = new CostDetails();
            costDetails.setUpstreamInferencePromptCost(getPromptTokens() * (usagePrice.getInput() / UsagePrice.TOKENS_PER_PRICE));
            costDetails.setUpstreamInferenceCompletionsCost(getCompletionTokens() * (usagePrice.getOutput() / UsagePrice.TOKENS_PER_PRICE));
            this.costDetails = costDetails;
            this.estimatedCost = costDetails.getUpstreamInferencePromptCost() + costDetails.getUpstreamInferenceCompletionsCost();
        }
    }

    @RegisterForReflection
    public static class CompletionTokensDetails {
        @JsonProperty("accepted_prediction_tokens")
        private int acceptedPredictionTokens;
        @JsonProperty("rejected_prediction_tokens")
        private int rejectedPredictionTokens;

        @JsonProperty("audio_tokens")
        private int audioTokens;
        @JsonProperty("image_tokens")
        private int imageTokens;
        @JsonProperty("reasoning_tokens")
        private int reasoningTokens;

        public int getAcceptedPredictionTokens() {
            return acceptedPredictionTokens;
        }

        public void setAcceptedPredictionTokens(int acceptedPredictionTokens) {
            this.acceptedPredictionTokens = acceptedPredictionTokens;
        }

        public int getRejectedPredictionTokens() {
            return rejectedPredictionTokens;
        }

        public void setRejectedPredictionTokens(int rejectedPredictionTokens) {
            this.rejectedPredictionTokens = rejectedPredictionTokens;
        }

        public int getAudioTokens() {
            return audioTokens;
        }

        public void setAudioTokens(int audioTokens) {
            this.audioTokens = audioTokens;
        }

        public int getImageTokens() {
            return imageTokens;
        }

        public void setImageTokens(int imageTokens) {
            this.imageTokens = imageTokens;
        }

        public int getReasoningTokens() {
            return reasoningTokens;
        }

        public void setReasoningTokens(int reasoningTokens) {
            this.reasoningTokens = reasoningTokens;
        }

        public void add(CompletionTokensDetails other) {
            if (other == null) {
                return;
            }
            this.acceptedPredictionTokens += other.acceptedPredictionTokens;
            this.rejectedPredictionTokens += other.rejectedPredictionTokens;
            this.audioTokens += other.audioTokens;
            this.imageTokens += other.imageTokens;
            this.reasoningTokens += other.reasoningTokens;
        }
    }

    @RegisterForReflection
    public static class PromptTokensDetails {
        @JsonProperty("audio_tokens")
        private int audioTokens;
        @JsonProperty("video_tokens")
        private int videoTokens;
        @JsonProperty("cached_tokens")
        private int cachedTokens;
        @JsonProperty("cache_write_tokens")
        private int cacheWriteTokens;

        public int getAudioTokens() {
            return audioTokens;
        }

        public void setAudioTokens(int audioTokens) {
            this.audioTokens = audioTokens;
        }

        public int getVideoTokens() {
            return videoTokens;
        }

        public void setVideoTokens(int videoTokens) {
            this.videoTokens = videoTokens;
        }

        public int getCachedTokens() {
            return cachedTokens;
        }

        public void setCachedTokens(int cachedTokens) {
            this.cachedTokens = cachedTokens;
        }

        public int getCacheWriteTokens() {
            return cacheWriteTokens;
        }

        public void setCacheWriteTokens(int cacheWriteTokens) {
            this.cacheWriteTokens = cacheWriteTokens;
        }

        public void add(PromptTokensDetails other) {
            if (other == null) {
                return;
            }
            this.audioTokens += other.audioTokens;
            this.videoTokens += other.videoTokens;
            this.cachedTokens += other.cachedTokens;
            this.cacheWriteTokens += other.cacheWriteTokens;
        }
    }

    /**
     * The actual cost charged by upstream provider for this request.
     */
    @RegisterForReflection
    public static class CostDetails {
        @JsonProperty("upstream_inference_prompt_cost")
        private double upstreamInferencePromptCost;
        @JsonProperty("upstream_inference_completions_cost")
        private double upstreamInferenceCompletionsCost;
        @JsonProperty("chat_total_cost")
        private double chatTotalCost;

        public double getUpstreamInferencePromptCost() {
            return upstreamInferencePromptCost;
        }

        public void setUpstreamInferencePromptCost(double upstreamInferencePromptCost) {
            this.upstreamInferencePromptCost = upstreamInferencePromptCost;
        }

        public double getUpstreamInferenceCompletionsCost() {
            return upstreamInferenceCompletionsCost;
        }

        public void setUpstreamInferenceCompletionsCost(double upstreamInferenceCompletionsCost) {
            this.upstreamInferenceCompletionsCost = upstreamInferenceCompletionsCost;
        }

        public double getChatTotalCost() {
            return chatTotalCost;
        }

        public void setChatTotalCost(double chatTotalCost) {
            this.chatTotalCost = chatTotalCost;
        }

        public void add(CostDetails other) {
            if (other == null) {
                return;
            }
            this.upstreamInferencePromptCost += other.upstreamInferencePromptCost;
            this.upstreamInferenceCompletionsCost += other.upstreamInferenceCompletionsCost;
            this.chatTotalCost += other.chatTotalCost;
        }
    }
}
