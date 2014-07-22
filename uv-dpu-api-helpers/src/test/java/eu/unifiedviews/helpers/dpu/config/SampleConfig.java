package eu.unifiedviews.helpers.dpu.config;

/**
 * Simple implementation used in {@link ConfigWrapTest}.
 * 
 * @author Škoda Petr
 */
public class SampleConfig {

    private String first;

    private String second;

    public SampleConfig() {
        first = "f";
        second = "s";
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

}
