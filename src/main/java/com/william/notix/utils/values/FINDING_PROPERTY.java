package com.william.notix.utils.values;

public enum FINDING_PROPERTY {
    CATEGORY("Category"),
    LOCATION("Location"),
    METHOD("Method"),
    ENVIRONMENT("Environment"),
    APPLICATION_NAME("Application Name"),
    IMAPCT("Impact"),
    LIKELIHOOD("Likelihood");

    public final String label;

    private FINDING_PROPERTY(String label) {
        this.label = label;
    }
}
