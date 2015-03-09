package eu.unifiedviews.util;

public interface Cryptography {

    /**
     * @param plainText
     *            String for encryption.
     * @return Encrypted string if cryptography is turned on, input string otherwise.
     */
    String encrypt(String plainText);

    /**
     * @param cipherText
     *            String for decryption.
     * @return Decrypted string if cryptography is turned on, input string otherwise.
     */
    String decrypt(String cipherText);

}
