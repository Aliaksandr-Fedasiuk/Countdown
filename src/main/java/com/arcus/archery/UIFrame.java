package com.arcus.archery;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class UIFrame extends JFrame {

    public static final String STOP_SHOOTING = "<html><div style=\"text-align:center\">STOP<br>SHOOTING</div></html>";
    public static final String STOP = "STOP";
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    static final String ARIAL_BLACK = "Arial Black";
    static final String SELECT_TARGET = "SELECT TARGET";
    static final String PRE_SHOT = "PRE SHOT";
    static final String TARGET = "TARGET ";

    private java.util.Timer timer = new Timer(true);

    private JPanel panel;
    private JLabel count;
    private JLabel team;

    private int globalCount;
    private boolean selectedTeam;
    private boolean isActive = false;
    private boolean isSkipRound = false;
    private boolean isStopShooting = false;

    public UIFrame() {
        setUndecorated(true);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setAlwaysOnTop(true);
        addKeyListener(new FrameKeyListener());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
                dispose();
                setUndecorated(!isUndecorated());
                revalidate();
                setVisible(true);
            }
        });
        add(initUIElements());
        init();
        setVisible(true);
    }

    private class FrameKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Key code pressed : " + e.getKeyCode());
            switch (e.getKeyChar()) {
                case 32: //space
                    isSkipRound = true;
                    break;
                case 27: //esc
                    isStopShooting = true;
                    if (!isActive) {
                        init();
                    }
                    break;
                case 49: //1
                    if (!isActive) setTeamAndStartTimer(1);
                    break;
                case 50: //2
                    if (!isActive) setTeamAndStartTimer(2);
                    break;
            }
        }
    }

    private JPanel initUIElements() {
        count = new JLabel();
        count.setFont(new Font(ARIAL_BLACK, Font.BOLD, 200));
        count.setHorizontalAlignment(SwingConstants.CENTER);
        count.setVerticalAlignment(SwingConstants.CENTER);

        team = new JLabel();
        team.setFont(new Font(ARIAL_BLACK, Font.BOLD, 110));
        team.setHorizontalAlignment(SwingConstants.CENTER);
        team.setVerticalAlignment(SwingConstants.CENTER);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(count, BorderLayout.CENTER);
        panel.add(team, BorderLayout.SOUTH);
        return panel;
    }

    public void init() {
        globalCount = 6;
        isActive = false;
        count.setFont(new Font(ARIAL_BLACK, Font.BOLD, 200));
        count.setForeground(Color.WHITE);
        team.setForeground(Color.WHITE);
        panel.setBackground(Color.RED);
        count.setText(PRE_SHOT);
        team.setText(SELECT_TARGET);
        System.out.println("Phase: Init.");
    }

    public void setTeamAndStartTimer(int teamNumber) {
        isActive = true;
        isSkipRound = false;
        isStopShooting = false;

        selectedTeam = (teamNumber - 1) == 1;
        count.setFont(new Font(ARIAL_BLACK, Font.BOLD, 500));
        count.setText("10");
        team.setText(TARGET + ((selectedTeam ? 1 : 0) + 1));
        setRedSchema();

        System.out.println("BeeperPreShotTimer: " + dateFormat.format(Calendar.getInstance().getTime()));
        timer.scheduleAtFixedRate(new Beeper(2), 1000, 1000);
        timer.scheduleAtFixedRate(new PreShotCountdownTimer(), 1000, 1000);
    }

    class PreShotCountdownTimer extends TimerTask {
        int cnt = 10;

        public PreShotCountdownTimer() {
            setRedSchema();
        }

        @Override
        public void run() {
            //System.out.println("PreShotCountdownTimer : " + dateFormat.format(Calendar.getInstance().getTime())
            // + " :" + cnt + ", round: " + globalCount + ", isStopShooting = " + isStopShooting);
            if (isStopShooting || isSkipRound) {
                cancel();
                init();
            } else {
                count.setFont(new Font(ARIAL_BLACK, Font.BOLD, 500));
                count.setText(String.valueOf(cnt));
                cnt--;
                if (cnt <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startShortTimer();
                    cancel();
                }
            }
        }
    }

    private void startShortTimer() {
        beep();
        timer.scheduleAtFixedRate(new ShotTimer(), 1000, 1000);
    }

    class ShotTimer extends TimerTask {
        int cnt = 20;

        public ShotTimer() {
            setGreenSchema();
            count.setFont(new Font(ARIAL_BLACK, Font.BOLD, 500));
            count.setText(String.valueOf(cnt));
            team.setText(TARGET + ((selectedTeam ? 1 : 0) + 1));
        }

        @Override
        public void run() {
            //System.out.println("ShootTimer: " + dateFormat.format(Calendar.getInstance().getTime())
            // + " :" + cnt + ", round: " + globalCount + ", isStopShooting = " + isStopShooting);
            if (isStopShooting) {
                cancel();
                init();
            } else {
                cnt--;
                if (cnt < 4 || isSkipRound) {
                    setRedSchema();
                }

                if (cnt <= 0 || isSkipRound) {
                    count.setFont(new Font(ARIAL_BLACK, Font.BOLD, 300));
                    count.setText(STOP);
                } else {
                    count.setText(String.valueOf(cnt));
                }

                if (cnt < 1 || isSkipRound) {
                    isSkipRound = false;
                    globalCount--;
                    if (globalCount > 0) {
                        selectedTeam = !selectedTeam;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startShortTimer();
                    } else {
                        count.setFont(new Font(ARIAL_BLACK, Font.BOLD, 200));
                        count.setVisible(false);
                        count.setText(STOP_SHOOTING);
                        count.setVisible(true);
                        team.setText("");
                        timer.scheduleAtFixedRate(new Beeper(3), 1000, 1000);
                        isActive = false;
                        isStopShooting = true;
                    }
                    cancel();
                }
            }
        }
    }

    private void setRedSchema() {
        count.setForeground(Color.WHITE);
        team.setForeground(Color.WHITE);
        panel.setBackground(Color.RED);
    }

    private void setGreenSchema() {
        count.setForeground(Color.DARK_GRAY);
        team.setForeground(Color.DARK_GRAY);
        panel.setBackground(Color.GREEN);
    }

    class Beeper extends TimerTask {
        int cnt;

        public Beeper(int cnt) {
            this.cnt = cnt;
        }

        @Override
        public void run() {
            beep();
            cnt--;
            if (cnt <= 0) {
                cancel();
            }
        }
    }


    private void beep() {
        try {
            //System.out.println("Beep");
            SoundUtils.tone(600, 500);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
