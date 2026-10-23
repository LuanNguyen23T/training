package javacore.practice.practice_2;

public enum VatRate {
    FOOD(0.05),
    ELECTRONIC(0.10),
    CROCKERY(0.10);

    private final double rate;

    VatRate(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }
}