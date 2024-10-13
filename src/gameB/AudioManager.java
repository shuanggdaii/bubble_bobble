package gameB;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;


/**
 * AudioManager plays background music for the game
 */
public class AudioManager {

	private static AudioManager instance;
	private Sequencer sequencer;

	public static AudioManager getInstance() {
		if (instance == null) {
			instance = new AudioManager();
		}
		return instance;
	}

	private AudioManager() {}

	public void play(String filename) {
		if (filename.endsWith(".wav")) {
			playWav(filename);
		} else if (filename.endsWith(".mid")) {
			playMidi(filename);
		} else {
			System.out.println("Unsupported file format: " + filename);
		}
	}

	private void playWav(String filename) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filename));
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
	}

	private void playMidi(String filename) {
		try {
			if (sequencer == null) {
				sequencer = MidiSystem.getSequencer();
				sequencer.open();
			}

			// get midi
			File midiFile = new File(filename);
			sequencer.setSequence(MidiSystem.getSequence(midiFile));

			// start playing music
			sequencer.start();

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (sequencer != null && sequencer.isRunning()) {
			sequencer.stop();
		}
	}
}
