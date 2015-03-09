package eu.unifiedviews.helpers.util;

import com.vaadin.ui.PasswordField;

import eu.unifiedviews.util.Cryptography;

@SuppressWarnings("serial")
public class CryptographyField extends PasswordField {

    private Cryptography cryptography;

    public CryptographyField(Cryptography cryptography) {
        super();

        this.cryptography = cryptography;
    }

    @Override
    public String getValue() {
        return cryptography.encrypt(super.getValue());
    }

    @Override
    public void setValue(String newValue) throws ReadOnlyException {
        super.setValue(cryptography.decrypt(newValue));
    }

}
