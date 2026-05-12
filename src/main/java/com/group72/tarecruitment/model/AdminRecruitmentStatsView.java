package com.group72.tarecruitment.model;

public class AdminRecruitmentStatsView {
    private final int totalApplications;
    private final int pendingApplications;
    private final int shortlistedApplications;
    private final int offeredApplications;
    private final int rejectedApplications;
    private final int withdrawnApplications;
    private final int activeJobCount;
    private final int draftJobCount;
    private final int closedJobCount;

    public AdminRecruitmentStatsView(
            int totalApplications,
            int pendingApplications,
            int shortlistedApplications,
            int offeredApplications,
            int rejectedApplications,
            int withdrawnApplications,
            int activeJobCount,
            int draftJobCount,
            int closedJobCount
    ) {
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
        this.shortlistedApplications = shortlistedApplications;
        this.offeredApplications = offeredApplications;
        this.rejectedApplications = rejectedApplications;
        this.withdrawnApplications = withdrawnApplications;
        this.activeJobCount = activeJobCount;
        this.draftJobCount = draftJobCount;
        this.closedJobCount = closedJobCount;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public int getPendingApplications() {
        return pendingApplications;
    }

    public int getShortlistedApplications() {
        return shortlistedApplications;
    }

    public int getOfferedApplications() {
        return offeredApplications;
    }

    public int getRejectedApplications() {
        return rejectedApplications;
    }

    public int getWithdrawnApplications() {
        return withdrawnApplications;
    }

    public int getActiveJobCount() {
        return activeJobCount;
    }

    public int getDraftJobCount() {
        return draftJobCount;
    }

    public int getClosedJobCount() {
        return closedJobCount;
    }

    public int getFinalDecisionCount() {
        return offeredApplications + rejectedApplications;
    }

    public int getOpenPipelineCount() {
        return pendingApplications + shortlistedApplications;
    }

    public int getOfferRatePercent() {
        int finalDecisionCount = getFinalDecisionCount();
        return finalDecisionCount == 0 ? 0 : Math.round((float) offeredApplications * 100 / finalDecisionCount);
    }
}
