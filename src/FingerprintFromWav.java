import org.apache.commons.codec.binary.Hex;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;


public class FingerprintFromWav {

	private byte[] fingerprint1, fingerprint2;
	private FingerprintSimilarity fingerprintSimilarity;
	
	public FingerprintFromWav(String wavPath1, String wavPath2){
		Wave wave1 = new Wave(wavPath1);
		fingerprint1 = wave1.getFingerprint();
		Wave wave2 = new Wave(wavPath2);
		fingerprint2 = wave2.getFingerprint();
		fingerprintSimilarity = wave1.getFingerprintSimilarity(wave2);
	}
	
	public float getSimilarity(){
		return fingerprintSimilarity.getSimilarity();
	}
	
	public float getScore(){
		return fingerprintSimilarity.getScore();
	}
	
	public byte[] getFingerprint1(){
		return fingerprint1;
	}
	
	public byte[] getFingerprint2(){
		return fingerprint2;
	}
	
	public String getFingerprint1AsString(){
		return new String(Hex.encodeHex(fingerprint1));
	}
	
	public String getFingerprint2AsString(){
		return new String(Hex.encodeHex(fingerprint2));
	}
	
	public static void main(String[] args) {
		if(args.length != 4){
			return;
		}
		
		final FingerprintFromWav fingerprintFromWav = new FingerprintFromWav(args[0], args[1]);
		System.out.println(args[2]+"\t"+args[3]+"\t"+fingerprintFromWav.getSimilarity());
	}
}
