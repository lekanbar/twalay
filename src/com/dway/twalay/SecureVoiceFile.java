package com.dway.twalay;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.rim.device.api.crypto.AESDecryptorEngine;
import net.rim.device.api.crypto.AESEncryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.CryptoException;
import net.rim.device.api.crypto.PKCS5FormatterEngine;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

public class SecureVoiceFile {
	
	public static byte[] encrypt( byte[] keyData, byte[] data )
	    {
		try{
	        // Create the AES key to use for encrypting the data.
	        // This will create an AES key using as much of the keyData
	        // as possible.
			int len = data.length;
	        AESKey key = new AESKey( keyData);

	        // Now, we want to encrypt the data.
	        // First, create the encryptor engine that we use for the actual
	        // encrypting of the data.
	        AESEncryptorEngine engine = new AESEncryptorEngine( key );

	        // Since we cannot guarantee that the data will be of an equal block
	        // length we want to use a padding engine (PKCS5 in this case).
	        PKCS5FormatterEngine fengine = new PKCS5FormatterEngine( engine );

	        // Create a BlockEncryptor to hide the engine details away.
	        NoCopyByteArrayOutputStream output = new NoCopyByteArrayOutputStream();
	        //ByteArrayOutputStream output = new ByteArrayOutputStream();
	        BlockEncryptor encryptor = new BlockEncryptor( fengine, output );

	        // Now, all we need to do is write our data to the output stream.
	        // But before doing so, let's calculate a hash on the data as well.
	        // A digest provides a one way hash function to map a large amount
	        // of data to a unique 20 byte value (in the case of SHA1).
	        //SHA1Digest digest = new SHA1Digest();
	        //digest.update( data );
	        //byte[] hash = digest.getDigest();

	        // Now, write out all of the data and the hash to ensure that the
	        // data was not modified in transit.
	        encryptor.write( data );
	        //encryptor.write( hash );
	        encryptor.close();
	        output.close();
	        
	        int finalLength = output.size();
	        byte[] cbytes = new byte[finalLength];
	        System.arraycopy(output.getByteArray(), 0, cbytes, 0, finalLength);
	        //String encryptedData = bytesToHex(cbytes);

	        // Now, the encrypted data is sitting in the ByteArrayOutputStream.
	        // We simply want to retrieve it.
	        return cbytes;//bytesToHex(cbytes).getBytes();//output.toByteArray();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	    }
	
	public static String bytesToHex(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

	public static byte[] decrypt( byte[] keyData, byte[] ciphertext ) throws CryptoException, IOException
	{
	        // First, create the AESKey again.
	        AESKey key = new AESKey( keyData );

	        // Now, create the decryptor engine.
	        AESDecryptorEngine engine = new AESDecryptorEngine( key );
	        // Since we cannot guarantee that the data will be of an equal block length
	        // we want to use a padding engine (PKCS5 in this case).
	        PKCS5UnformatterEngine uengine = new PKCS5UnformatterEngine( engine );

	        // Create the BlockDecryptor to hide the decryption details away.
	        ByteArrayInputStream input = new ByteArrayInputStream( ciphertext );
	        BlockDecryptor decryptor = new BlockDecryptor( uengine, input );

	        // Now, read in the data. Remember that the last 20 bytes represent
	        // the SHA1 hash of the decrypted data.
	        byte[] temp = new byte[ 100 ];
	        DataBuffer buffer = new DataBuffer();

	        for( ;; ) {
	            int bytesRead = decryptor.read( temp );
	            buffer.write( temp, 0, bytesRead );

	            if( bytesRead < 100 ) {
	                // We ran out of data.
	                break;
	            }
	        }

	        byte[] plaintextAndHash = buffer.getArray();
	        int plaintextLength = plaintextAndHash.length - SHA1Digest.DIGEST_LENGTH;
	        byte[] plaintext = new byte[ plaintextLength ];
	        byte[] hash = new byte[ SHA1Digest.DIGEST_LENGTH ];

	        System.arraycopy( plaintextAndHash, 0, plaintext, 0, plaintextLength );
	        System.arraycopy( plaintextAndHash, plaintextLength, hash, 0,
	            SHA1Digest.DIGEST_LENGTH );

	        // Now, hash the plaintext and compare against the hash
	        // that we found in the decrypted data.
	        SHA1Digest digest = new SHA1Digest();
	        digest.update( plaintext );
	        byte[] hash2 = digest.getDigest();

	        if(!Arrays.equals( hash, hash2 )) {
	            throw new RuntimeException();
	        }

	        return plaintext;
	    }
}
