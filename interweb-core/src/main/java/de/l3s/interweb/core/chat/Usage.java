package de.l3s.interweb.core.chat;

import de.l3s.interweb.core.models.UsagePrice;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class Usage {
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

        if (other.promptTokensDetails != null) {
            if (this.promptTokensDetails == null) {
                this.promptTokensDetails = new PromptTokensDetails();
            }
            this.promptTokensDetails.audioTokens += other.promptTokensDetails.audioTokens;
            this.promptTokensDetails.videoTokens += other.promptTokensDetails.videoTokens;
            this.promptTokensDetails.cachedTokens += other.promptTokensDetails.cachedTokens;
            this.promptTokensDetails.cacheWriteTokens += other.promptTokensDetails.cacheWriteTokens;
        }

        if (other.completionTokensDetails != null) {
            if (this.completionTokensDetails == null) {
                this.completionTokensDetails = new CompletionTokensDetails();
            }
            this.completionTokensDetails.acceptedPredictionTokens += other.completionTokensDetails.acceptedPredictionTokens;
            this.completionTokensDetails.rejectedPredictionTokens += other.completionTokensDetails.rejectedPredictionTokens;
            this.completionTokensDetails.audioTokens += other.completionTokensDetails.audioTokens;
            this.completionTokensDetails.imageTokens += other.completionTokensDetails.imageTokens;
            this.completionTokensDetails.reasoningTokens += other.completionTokensDetails.reasoningTokens;
        }

        if (other.costDetails != null) {
            if (this.costDetails == null) {
                this.costDetails = new CostDetails();
            }
            this.costDetails.upstreamInferencePromptCost += other.costDetails.upstreamInferencePromptCost;
            this.costDetails.upstreamInferenceCompletionsCost += other.costDetails.upstreamInferenceCompletionsCost;
            this.costDetails.chatTotalCost += other.costDetails.chatTotalCost;
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
    }
}
