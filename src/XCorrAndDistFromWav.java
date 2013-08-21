import org.apache.commons.math3.stat.StatUtils;
import com.musicg.wave.Wave;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;


public class XCorrAndDistFromWav {
	private double maxCorr;
	private double dist, timeDist, freqDist;
	private double[] timeSignal1, timeSignal2;
	private double[] freqSignal1, freqSignal2;
	private int N;	// number of samples (Note: 16bit/s means 2 bytes as 1 sample)
	
	
	public XCorrAndDistFromWav(Wave wave1, Wave wave2){
		
		N = Math.min(wave1.size()/2, wave2.size()/2);
		timeSignal1 = normalize(wave1.getSampleAmplitudes(), N);
		timeSignal2 = normalize(wave2.getSampleAmplitudes(), N);
		double[] ccf = new double[2*N-1];
		new CrossCorrelation(timeSignal1, timeSignal2).getCCF(ccf);
		maxCorr = StatUtils.max(ccf);
		timeDist = 1 - maxCorr;
		
		freqSignal1 = normalize(harmonics(timeSignal1), 0);
		freqSignal2 = normalize(harmonics(timeSignal2), 0);
		
		double freqSum = 0;
		for(int i=0; i<freqSignal1.length; i++){
			double diff = freqSignal1[i] - freqSignal2[i];
			freqSum += diff * diff; 
		}
		freqDist = Math.sqrt(freqSum);
		
		dist = Math.sqrt(freqSum + timeDist * timeDist); 
	}
	
	public double getMaxCorr(){		
		return maxCorr;
	}
	
	public double getDist(){
		return dist;
	}
	
	public double[] harmonics(double[] timeSignal){
		int windowSize = timeSignal.length;
		double[] tmpTimeSignal = new double[2*windowSize];
		for(int i=0; i<windowSize; i++){
			double hamWindow = 0.54 - 0.46*Math.cos(2*Math.PI*i/windowSize);
			tmpTimeSignal[i] = timeSignal[i] * hamWindow;
		}
		DoubleFFT_1D fft = new DoubleFFT_1D(windowSize); 
        fft.realForwardFull(tmpTimeSignal);
        double[] fullFFTList = tmpTimeSignal;
        
        int vector_end = Math.round((float)windowSize/2);
        double[] fftValues = new double[vector_end];
        for(int j=0; j<vector_end; j++){
        	fftValues[j] = Math.sqrt(fullFFTList[2*j]*fullFFTList[2*j] + fullFFTList[2*j+1]*fullFFTList[2*j+1]);
        }
        return fftValues;
	}
	
	public double[] normalize(short[] amps, int n){
		int size = 0;
		if(n == 0){
			size = amps.length;
		}else{
			size = n;
		}
		double sum_energy = 0;
		for(int i=0; i<size; i++){
			sum_energy += (double) amps[i] * ((double) amps[i]);
		}
		double avg_energy = Math.sqrt(sum_energy);
		double[] normalizedAmps = new double[size];
		for(int j=0; j<size; j++){
			normalizedAmps[j] = ((double) amps[j])/avg_energy ;
		}
		return normalizedAmps;
	}
	
	public double[] normalize(double[] amps, int n){
		int size = 0;
		if(n == 0){
			size = amps.length;
		}else{
			size = n;
		}
		double sum_energy = 0;
		for(int i=0; i<size; i++){
			sum_energy += amps[i] * amps[i];
		}
		double avg_energy = Math.sqrt(sum_energy);
		double[] normalizedAmps = new double[size];
		for(int j=0; j<size; j++){
			normalizedAmps[j] = ((double) amps[j])/avg_energy ;
		}
		return normalizedAmps;
	}
	
	public static void main(String[] args) {
		/*
		 * args[0] - wav1
		 * args[1] - wav2
		 * args[2] - SampleNo
		 * args[3] - groundtruth
		 */
		if(args.length != 4){
			return;
		}
		
		final float[] trimSeconds = {10, 5, 4, 3, 2, 1};
		Wave wave1 = new Wave(args[0]);
		Wave wave2 = new Wave(args[1]);		
		
		for(int i = 0; i < 6; i++){
			if(wave1.length() > trimSeconds[i]){
				wave1.rightTrim(wave1.length() - trimSeconds[i]);
			}
			if(wave2.length() > trimSeconds[i]){
				wave2.rightTrim(wave2.length()  - trimSeconds[i]);
			}
			
			XCorrAndDistFromWav xCorrAndDistFromWav = new XCorrAndDistFromWav(wave1, wave2);
			System.out.println(args[2]+(int)trimSeconds[i]+"\t"+args[3]+"\t"+xCorrAndDistFromWav.getMaxCorr()+"\t"+xCorrAndDistFromWav.getDist());
			
		}
		
		//XCorrAndDistFromWav xCorrAndDistFromWav = new XCorrAndDistFromWav(wave1, wave2);
		//System.out.println(args[2]+"\t"+args[3]+"\t"+xCorrAndDistFromWav.getMaxCorr()+"\t"+xCorrAndDistFromWav.getDist());
		System.exit(0);
		
	}
}
