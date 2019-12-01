/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.security;

import static java.nio.charset.StandardCharsets.UTF_8;

import fj.data.Either;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.onap.sdc.security.logging.enums.EcompLoggerErrorCode;
import org.onap.sdc.security.logging.wrappers.Logger;

public class SecurityUtil {

    private static final Logger LOG = Logger.getLogger(SecurityUtil.class);
    private static final byte[] KEY =
            new byte[] {-64, 5, -32, -117, -44, 8, -39, 1, -9, 36, -46, -81, 62, -15, -63, -75};
    public static final SecurityUtil INSTANCE = new SecurityUtil();
    public static final String ALGORITHM = "AES";
    public static final String CHARSET = UTF_8.name();

    public static Key secKey = null;

    /**
     * cmd commands >$PROGRAM_NAME decrypt "$ENCRYPTED_MSG"
     * >$PROGRAM_NAME encrypt "message"
     **/

    private SecurityUtil() {
    }

    static {
        try {
            secKey = generateKey(KEY, ALGORITHM);
        } catch (Exception e) {
            LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR,"cannot generate key for {}", ALGORITHM);
        }
    }


    public static Key generateKey(final byte[] KEY, String algorithm) {
        return new SecretKeySpec(KEY, algorithm);
    }

    //obfuscates key prefix -> **********
    public String obfuscateKey(String sensitiveData) {

        if (sensitiveData == null) {
            return null;
        }
        int len = sensitiveData.length();
        StringBuilder builder = new StringBuilder(sensitiveData);
        for (int i = 0; i < len / 2; i++) {
            builder.setCharAt(i, '*');
        }
        return builder.toString();
    }


    /**
     * @param strDataToEncrypt - plain string to encrypt
     *                         Encrypt the Data
     *                         a. Declare / Initialize the Data. Here the data is of type String
     *                         b. Convert the Input Text to Bytes
     *                         c. Encrypt the bytes using doFinal method
     */
    public Either<String, String> encrypt(String strDataToEncrypt) {
        if (strDataToEncrypt != null) {
            try {
                LOG.debug("Encrypt key -> {}", secKey);
                Cipher aesCipherForEncryption = Cipher.getInstance(
                        "AES");          // Must specify the mode explicitly as most JCE providers default to ECB mode!!
                aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secKey);
                byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
                byte[] byteCipherText = aesCipherForEncryption.doFinal(byteDataToEncrypt);
                String strCipherText = new String(Base64.getMimeEncoder().encode(byteCipherText), CHARSET);
                LOG.debug("Cipher Text generated using AES is {}", strCipherText);
                return Either.left(strCipherText);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR,
                        "cannot encrypt data unknown algorithm or missing encoding for {}", secKey.getAlgorithm());
            } catch (InvalidKeyException e) {
                LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR, "invalid key recieved - > {} | {}",
                        new String(Base64.getDecoder().decode(secKey.getEncoded())), e.getMessage());
            } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
                LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR,
                        "bad algorithm definition (Illegal Block Size or padding), please review you algorithm block&padding",
                        e.getMessage());
            }
        }
        return Either.right("Cannot encrypt " + strDataToEncrypt);
    }

    /**
     * Decrypt the Data
     *
     * @param byteCipherText  - should be valid bae64 input in the length of 16bytes
     * @param isBase64Decoded - is data already base64 encoded&aligned to 16 bytes
     *                        a. Initialize a new instance of Cipher for Decryption (normally don't reuse the same
     *                        object)
     *                        b. Decrypt the cipher bytes using doFinal method
     */
    public Either<String, String> decrypt(byte[] byteCipherText, boolean isBase64Decoded) {
        if (byteCipherText != null) {
            byte[] alignedCipherText = byteCipherText;
            try {
                if (isBase64Decoded) {
                    alignedCipherText = Base64.getDecoder().decode(byteCipherText);
                }
                LOG.debug("Decrypt key -> " + secKey.getEncoded());
                Cipher aesCipherForDecryption = Cipher.getInstance(
                        "AES"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!
                aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secKey);
                byte[] byteDecryptedText = aesCipherForDecryption.doFinal(alignedCipherText);
                String strDecryptedText = new String(byteDecryptedText);
                LOG.debug("Decrypted Text message is: {}", obfuscateKey(strDecryptedText));
                return Either.left(strDecryptedText);
            } catch (NoSuchAlgorithmException e) {
                LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR,
                        "cannot encrypt data unknown algorithm or missing encoding for {}", secKey.getAlgorithm());
            } catch (InvalidKeyException e) {
                LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR, "invalid key recieved - > {} | {}",
                        new String(Base64.getDecoder().decode(secKey.getEncoded())), e.getMessage());
            } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
                LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR,
                        "bad algorithm definition (Illegal Block Size or padding), please review you algorithm block&padding",
                        e.getMessage());
            }
        }
        return Either.right("Decrypt FAILED");
    }

    public Either<String, String> decrypt(String byteCipherText) {
        try {
            return decrypt(byteCipherText.getBytes(CHARSET), true);
        } catch (UnsupportedEncodingException e) {
            LOG.warn(EcompLoggerErrorCode.PERMISSION_ERROR, "Missing encoding for {} | {} ", secKey.getAlgorithm(),
                    e.getMessage());
        }
        return Either.right("Decrypt FAILED");
    }
}
