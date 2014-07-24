package eu.unifiedviews.helpers.dpu.config;


/**
 * Simple configuration object used in {@Link BaseConfigDialogTest}.
 * 
 * @author Petyr
 */
public class SimpleTestConfig {

    public String text;

    public int value;

    public SimpleTestConfig(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public boolean isValid() {
        return true;
    }

}
