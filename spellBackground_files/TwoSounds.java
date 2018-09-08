    import javax.swing.*;
    import javax.sound.sampled.*;
    import java.io.File;

    public class TwoSounds {

    public static void main(String[] args) throws Exception {
        String filePath = "airPortalMain.wav";
        AudioInputStream ais = AudioSystem.getAudioInputStream( new File(filePath) );
        AudioFormat format = ais.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip clip = (Clip) AudioSystem.getLine(info);
        clip.open(ais);

        String filePath2 = "idlePortalMain.wav";
        AudioInputStream ais2 = AudioSystem.getAudioInputStream( new File(filePath2) );
        AudioFormat format2 = ais2.getFormat();
        DataLine.Info info2 = new DataLine.Info(Clip.class, format2);
        Clip clip2 = (Clip) AudioSystem.getLine(info2);
        clip2.open(ais2);

        // loop continuously
        // clip.start();
        // clip2.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip2.loop(Clip.LOOP_CONTINUOUSLY);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // A GUI element to prevent the Clip's daemon Thread
                // from terminating at the end of the main()
                JOptionPane.showMessageDialog(null, "Close to exit!");
            }
        });
    }
    }