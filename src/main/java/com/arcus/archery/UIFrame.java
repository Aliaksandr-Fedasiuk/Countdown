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
    private TimerTask preShotCountdownTimer;
    private TimerTask beeperPreShotTimerTask;

    private JPanel panel;
    private JLabel count;
    private JLabel team;

    private int globalCount;
    private boolean selectedTeam;
    private boolean isActive = false;

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
        count.setFont(new Font("Arial Black", Font.BOLD, 300));
        count.setHorizontalAlignment(SwingConstants.CENTER);
        count.setVerticalAlignment(SwingConstants.CENTER);

        team = new JLabel();
        team.setFont(new Font("Arial Black", Font.BOLD, 150));
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
        count.setForeground(Color.WHITE);
        team.setForeground(Color.WHITE);
        panel.setBackground(Color.RED);
        count.setText("PRE SHOT");
        team.setText("SELECT TEAM");
        System.out.println("Phase: Init.");
    }

    public void setTeamAndStartTimer(int teamNumber) {
        isActive = true;
        selectedTeam = (teamNumber - 1) == 1;
        restartShortCycle();
    }

    private void restartShortCycle() {
        System.out.println("=== Shot cycle " + globalCount +  " ===");
        System.out.println("Team " + ((selectedTeam ? 1 : 0) + 1));

        count.setForeground(Color.WHITE);
        team.setForeground(Color.WHITE);
        panel.setBackground(Color.RED);
        count.setText("PRE SHOT");
        team.setText("TEAM " + ((selectedTeam ? 1 : 0) + 1));

        selectedTeam = !selectedTeam;
        beeperPreShotTimerTask = new BeeperPreShotTimerTask(2);
        timer.scheduleAtFixedRate(beeperPreShotTimerTask, 1000, 2000);
    }

    private void startPreShotCountdownTimer() {
        preShotCountdownTimer = new PreShotCountdownTimerTask(10);
        timer.scheduleAtFixedRate(preShotCountdownTimer, 1000, 1000);
    }

    private void startShortTimer() {
        timer.scheduleAtFixedRate(new BeeperBeforeShortTimerTask(), 1000, 1000);
        timer.scheduleAtFixedRate(new ShotTimerTask(20), 1000, 1000);
    }

    class BeeperPreShotTimerTask extends TimerTask {
        int cnt;
        BeeperPreShotTimerTask(int cnt) {
            this.cnt = cnt;
        }
        @Override
        public void run() {
            System.out.println("BeeperPreShotTimerTask: " + dateFormat.format(Calendar.getInstance().getTime()));
            beep();
            cnt--;
            if (cnt == 0) {
                startPreShotCountdownTimer();
                cancel();
            }
        }
    }

    class PreShotCountdownTimerTask extends TimerTask {
        int cnt;
        public PreShotCountdownTimerTask(int cnt) {
            this.cnt = cnt;
        }
        @Override
        public void run() {
            System.out.println("PreShotCountdownTimerTask: " + dateFormat.format(Calendar.getInstance().getTime()));
            count.setForeground(Color.WHITE);
            panel.setBackground(Color.RED);
            count.setText(String.valueOf(cnt));
            cnt--;
            if (cnt == 0) {
                startShortTimer();
                cancel();
            }
        }
    }

    class BeeperBeforeShortTimerTask extends TimerTask {
        @Override
        public void run() {
            beep();
            cancel();
        }
    }

    class ShotTimerTask extends TimerTask {
        int cnt;
        public ShotTimerTask(int cnt) {
            this.cnt = cnt;
        }
        @Override
        public void run() {
            System.out.println("ShotTimerTask: " + dateFormat.format(Calendar.getInstance().getTime()));
            count.setForeground(Color.DARK_GRAY);
            team.setForeground(Color.DARK_GRAY);
            panel.setBackground(Color.GREEN);
            count.setText(String.valueOf(cnt));
            cnt--;
            if (cnt == 0) {
                globalCount--;
                if (globalCount > 0) {
                    restartShortCycle();
                } else {
                    count.setForeground(Color.WHITE);
                    team.setForeground(Color.RED);
                    count.setText("STOP SHOOTING");
                    panel.setBackground(Color.RED);
                }
                cancel();
                System.out.println("Finish: 'ShotTimerTask'");
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
