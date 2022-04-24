package utils;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordServices {
    public static String encryptSHA256(String testoChiaro){
        String key = DigestUtils.sha256Hex(testoChiaro).toUpperCase();
        return key;
    }
    public static boolean checkSHA256(String password, String testoChiaro){

        if (password.equals(encryptSHA256(testoChiaro).toUpperCase()))
            return true;
        else
            return false;
    }
}
