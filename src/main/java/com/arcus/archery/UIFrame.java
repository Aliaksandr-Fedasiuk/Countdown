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

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private java.util.Timer timer = new Timer(true);

    private JPanel panel;
    private JLabel count;
    private JLabel team;

    private int globalCount;
    private boolean selectedTeam;
    private boolean isActive = false;
    private boolean isSkipRound = false;

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
                    init();
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
        count.setFont(new Font("Arial Black", Font.BOLD, 200));
        count.setHorizontalAlignment(SwingConstants.CENTER);
        count.setVerticalAlignment(SwingConstants.CENTER);

        team = new JLabel();
        team.setFont(new Font("Arial Black", Font.BOLD, 110));
        team.setHorizontalAlignment(SwingConstants.CENTER);
        team.setVerticalAlignment(SwingConstants.CENTER);

        panel = new JPanel(new GridLayout(2, 1));
        panel.add(count);
        panel.add(team);
        return panel;
    }

    public void init() {
        globalCount = 6;
        isActive = false;
        isSkipRound = false;
        count.setForeground(Color.WHITE);
        team.setForeground(Color.WHITE);
        panel.setBackground(Color.RED);
        count.setText("PRE SHOT");
        team.setText("SELECT BOARD");
        System.out.println("Phase: Init.");
    }

    public void setTeamAndStartTimer(int teamNumber) {
        isActive = true;
        selectedTeam = (teamNumber - 1) == 1;

        count.setForeground(Color.WHITE);
        count.setText("10");
        team.setForeground(Color.WHITE);
        team.setText("BOARD " + ((selectedTeam ? 1 : 0) + 1));
        panel.setBackground(Color.RED);

        System.out.println("BeeperPreShotTimer: " + dateFormat.format(Calendar.getInstance().getTime()));
        timer.scheduleAtFixedRate(new Beeper(2), 1000, 1000);
        timer.scheduleAtFixedRate(new PreShotCountdownTimer(), 1000, 1000);
    }

    class PreShotCountdownTimer extends TimerTask {
        int cnt = 10;
        @Override
        public void run() {
            System.out.println("PreShotCountdownTimer: " + dateFormat.format(Calendar.getInstance().getTime()) + " :" + cnt);
            count.setForeground(Color.WHITE);
            panel.setBackground(Color.RED);
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

    private void startShortTimer() {
        beep();
        timer.scheduleAtFixedRate(new ShotTimer(), 1000, 1000);
    }

    class ShotTimer extends TimerTask {
        int cnt = 20;
        @Override
        public void run() {
            System.out.println("ShotTimer: " + dateFormat.format(Calendar.getInstance().getTime()) + " :" + cnt);
            count.setForeground(Color.DARK_GRAY);
            count.setText(String.valueOf(cnt));
            team.setForeground(Color.DARK_GRAY);
            team.setText("BOARD " + ((selectedTeam ? 1 : 0) + 1));
            panel.setBackground(Color.GREEN);
            cnt--;
            if (cnt < 0 || isSkipRound) {
                isSkipRound = false;
                globalCount--;
                if (globalCount > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    selectedTeam = !selectedTeam;
                    startShortTimer();
                } else {
                    count.setForeground(Color.WHITE);
                    count.setText("STOP");
                    team.setForeground(Color.WHITE);
                    team.setText("SHOOTING");
                    panel.setBackground(Color.RED);
                    timer.scheduleAtFixedRate(new Beeper(3), 1000, 1000);
                }
                cancel();
            }
        }
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
            System.out.println("Beep");
            SoundUtils.tone(600, 500);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
