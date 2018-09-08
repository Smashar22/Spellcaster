import java.util.Random;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

// Dice rolling facade / decorator
public class Main {

  public static void main(String[] args) {
    title();
    int d20Challenge = 2;
    DiceRoller diceSet = new DiceRoller(d20Challenge);
    diceSet.roll();
    System.out.print("\n");
    System.out.println("\t    - * - * - * - \n");
  }

  public static void title(){
    System.out.println("\n\t- - - - - - - - - - -");
    System.out.println("\t|  - Spellcaster -  |");
    System.out.println("\t- - - - - - - - - - -");
  }
}

abstract class Dice {
  Random rand;
  int  faceUp;
  String diceType;

  public void description(){}
  public String getSound(int seed){
    return "";
  }
}

/* Facade */
class DiceRoller {
  public int challengeVal;
  private Dice20 d20;
  private Dice10 d10;
  private Dice8 d8;
  private Dice6 d6;
  private Dice4 d4;

  public DiceRoller(int taskDifficulty) {
    this.challengeVal = taskDifficulty;
    this.d20 = new Dice20();
    this.d10 = new Dice10();
    this.d8 = new Dice8();
    this.d6 = new Dice6();         
    this.d4 = new Dice4();
  }

  public void roll() {

    int soundSeed;
    SoundPlayer playFX;
    IncantDescribe latinDecorator;
    String sound;

    System.out.println("\n\t Challenge value is " + challengeVal);
    int challengeRoll = d20.faceUp;
    boolean passChal = (challengeRoll > challengeVal) ? true : false;
    d20.description();

    if(passChal) {
      System.out.println("Success! Needed "
                          +challengeVal+ " and got "
                          +challengeRoll+ " - Casting Spell!");

      // -- Element Dice Roll -- //
      // d4.description();
      latinDecorator = new IncantDescribe(d4, d4.faceUp);
      latinDecorator.description();
      soundSeed = d4.faceUp;
      sound = d4.getSound(soundSeed);
      backSoundThread(sound);


      // -- Incantation Dice Rolls -- //
      latinDecorator = new IncantDescribe(d6, d6.faceUp);
      latinDecorator.description();
      soundSeed = d6.faceUp;
      sound = d6.getSound(soundSeed);
      playFX = new SoundPlayer(sound);
      soundThread(playFX);


      latinDecorator = new IncantDescribe(d8, d8.faceUp);
      latinDecorator.description();
      soundSeed = d8.faceUp;
      sound = d8.getSound(soundSeed);
      playFX = new SoundPlayer(sound);
      soundThread(playFX);


      latinDecorator = new IncantDescribe(d10, d10.faceUp);
      latinDecorator.description();
      soundSeed = d10.faceUp;
      sound = d10.getSound(soundSeed);
      playFX = new SoundPlayer(sound);
      soundThread(playFX);


      sound = "fin.wav";
      playFX = new SoundPlayer(sound);
      soundThread(playFX);

    } else {
      System.out.println("You did not succeed in the challenge");
      playFX = new SoundPlayer("diceRoll.wav");
      soundThread(playFX);
    }
  }
  public void backSoundThread(String path) {
    try {
      String filePath = path;
      AudioInputStream ais = AudioSystem.getAudioInputStream( new File(filePath) );
      AudioFormat format = ais.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      Clip clip = (Clip) AudioSystem.getLine(info);
      clip.open(ais);
      clip.start();
    } catch (UnsupportedAudioFileException ex) {
      ex.printStackTrace();
    } catch (LineUnavailableException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void soundThread(SoundPlayer playFX){
    playFX.start();
    try {
        Thread.sleep(1500);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    playFX.stop();
  }
}

class SoundPlayer {

  private Clip clip;
  private Thread thread;
  private final Object loopLock = new Object();

  public SoundPlayer(String audioFile) {
    AudioInputStream audioStream = null;
    URL audioURL = this.getClass().getClassLoader().getResource(audioFile);
    // Obtain audio input stream from the audio file and load the information
    // into main memory using the URL path retrieved from above.
    try {
      audioStream = AudioSystem.getAudioInputStream(audioURL);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    try {
      // Retrieve the object of class Clip from the Data Line.
      this.clip = AudioSystem.getClip();
      
      // Load the audio input stream into memory for future play-back.
      this.clip.open(audioStream);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void stop() {
    synchronized (loopLock) {
      loopLock.notifyAll();
    }
  }

  public void start() {
    Runnable r = new Runnable() {
      public void run() {
        clip.setFramePosition(0);
        clip.start();
        synchronized (loopLock) {
          try {
            loopLock.wait();
          } catch (InterruptedException ex) {
          }
        }
        clip.stop();
      }
    };
    thread = new Thread(r);
    thread.start();
  }
}

class Dice4 extends Dice {
  // Ambient Sound
  public Dice4() {
    rand = new Random();
    faceUp = rand.nextInt(4) + 1;
    diceType = "D4";
  }

  @Override
  public void description() {
    System.out.print("\nNumber on D4 rolled is "+ faceUp + " - Element is ");
  }

  @Override
  public String getSound(int seed) {
    String sound = "";
    switch(seed) {
      case 1: sound = "airPortalMain.wav";
              break;
      case 2: sound = "airPortalMini.wav";
              break;
      case 3: sound = "idlePortalMain2.wav";
              break;
      case 4: sound = "fireBackground.wav";
              break;
      default: sound = null;
              break;
    }
    return sound;
  }
}

class Dice6 extends Dice {
  // Incant Word 3

  public Dice6() {
    rand = new Random();
    faceUp = rand.nextInt(6) + 1;
    diceType = "D6";
  }

  @Override
  public void description() {
    System.out.print("Incantation 1: Number on D6 rolled is "+ faceUp);
  }

  @Override
  public String getSound(int seed) {
    String sound = "";
    switch(seed) {
      case 1: sound = "inc1.wav";
              break;
      case 2: sound = "inc4.wav";
              break;
      case 3: sound = "inc7.wav";
              break;
      case 4: sound = "inc10.wav";
              break;
      case 5: sound = "inc13.wav";
              break;
      case 6: sound = "inc16.wav";
              break;
      default: sound = null;
              break;
    }
    return sound;
  }
}

class Dice8 extends Dice {
  // Incant Word 2 

  public Dice8() {
    rand = new Random();
    faceUp = rand.nextInt(8) + 1;
    diceType = "D8";
  }

  @Override
  public void description() {
    System.out.print("Incantation 2: Number on D8 rolled is "+ faceUp);
  }

  @Override
  public String getSound(int seed) {
    String sound = "";
    switch(seed) {
      case 1: sound = "inc2.wav";
              break;
      case 2: sound = "inc5.wav";
              break;
      case 3: sound = "inc8.wav";
              break;
      case 4: sound = "inc11.wav";
              break;
      case 5: sound = "inc14.wav";
              break;
      case 6: sound = "inc17.wav";
              break;
      case 7: sound = "inc20.wav";
              break;
      case 8: sound = "inc23.wav";
              break;
      default: sound = null;
              break;
    }
    return sound;
  }
}

class Dice10 extends Dice {
  // Incant Word 1

  public Dice10() {
    rand = new Random();
    faceUp = rand.nextInt(10) + 1;
    diceType = "D10";
  }

  @Override
  public void description() {
    System.out.print("Incantation 3: Number on D10 rolled is "+ faceUp);
  }

  @Override
  public String getSound(int seed) {
    String sound = "";
    switch(seed) {
      case 1: sound = "inc3.wav";
              break;
      case 2: sound = "inc6.wav";
              break;
      case 3: sound = "inc9.wav";
              break;
      case 4: sound = "inc12.wav";
              break;
      case 5: sound = "inc15.wav";
              break;
      case 6: sound = "inc18.wav";
              break;
      case 7: sound = "inc19.wav";
              break;
      case 8: sound = "inc21.wav";
              break;
      case 9: sound = "inc22.wav";
              break;
      case 10: sound = "inc24.wav";
              break;
      default: sound = null;
              break;
    }
    return sound;
  }
}

class Dice20 extends Dice {
  // Pass / Fail / Crit

  public Dice20() {
    rand = new Random();
    faceUp = rand.nextInt(20) + 1;
    diceType = "D20";
  }

  @Override
  public void description() {
    System.out.println("\tNumber on D20 rolled is "+ faceUp);
  }

  @Override
  public String getSound(int seed) {
    return "";
  }
}

abstract class DiceDecorator extends Dice {
  public abstract void description();
}

class IncantDescribe extends DiceDecorator {
  private Dice dice;
  public String incantWord;
  public String[] incants;

  public IncantDescribe(Dice d, int wordRef) {
    dice = d;
    String[] incants = {"Incertus", "Cupio", "Vita", "Scio", "Manus", "Veritas",
                        "Pulcher", "Virtus", "Mortis", "Didici", "Potentis", 
                        "Credo", "Alia", "Pulcher", "Imperio", "Licet", "Careo",
                        "Pecto", "Paro", "Oculos", "Praeses", "Fero", "Incertus",
                        "Imperio", "Wind", "Water", "Lightning", "Fire"};
    switch(dice.diceType) {
      case "D6": wordRef = wordRef - 1;
                break;
      case "D8": wordRef = wordRef + 5;
                break;
      case "D10": wordRef = wordRef + 13;
                break;
      case "D4": wordRef = wordRef + 23;
                break;
      default: wordRef = wordRef;
                break;
    }
    incantWord = incants[wordRef];
  }

  @Override
  public void description() {
    dice.description();
    if(dice.diceType.equals("D4")) { System.out.println(incantWord + "!\n"); }
    else { System.out.print(" - " + incantWord + "!\n"); }
  }
}




