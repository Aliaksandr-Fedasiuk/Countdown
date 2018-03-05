package com.arcus.archery;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class CountDownFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private Timer timer = new Timer(true);
    private TimerTask countdownTimerTask = new CountdownTimerTask();
    private TimerTask beeperTimerTask;

    private JPanel panel;
    private JLabel count;
    private JLabel team;
    private Countdown countdown;

    public CountDownFrame(final Countdown countdown) {
        this.countdown = countdown;
        setUndecorated(true);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setAlwaysOnTop(true);
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Pressed: " + e.getKeyCode());
                switch (e.getKeyChar()) {
                    case 32:
                        if (countdown.isFinished()) {
                            init();
                        } else {
                            if (countdown.isPause()) {
                                start();
                            } else {
                                pause();
                            }
                        }
                        break;
                    case 27:
                        init();
                        break;
                    case 49:
                        if (countdown.isReady()) {
                            setTeam(1);
                        }
                        break;
                    case 50:
                        if (countdown.isReady()) {
                            setTeam(2);
                        }
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

        });
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
        add(initText());
        startTimer();
        setVisible(true);
    }

    private void startTimer() {
        timer.scheduleAtFixedRate(countdownTimerTask, 1000, 1000);
        init();
        System.out.println("TimerTask started.");
    }

    public void init() {
        countdown.reset();
        count.setForeground(Color.WHITE);
        team.setForeground(Color.WHITE);
        panel.setBackground(Color.RED);
        count.setText("PRE SHOT");
        team.setText("SELECT TEAM");
        System.out.println("Phase: Init.");
    }

    private void setTeam(int teamNumber) {
        team.setText("TEAM " + teamNumber);
        beeperTimerTask = new BeeperTimerTask(2);
        timer.scheduleAtFixedRate(beeperTimerTask, 1000, 2000);
    }

    public void start() {
        countdown.setPause(false);
        System.out.println("TimerTask restarted.");
    }

    public void pause() {
        countdown.setPause(true);
        System.out.println("TimerTask paused.");
    }

    private JPanel initText() {
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

    private void beep() {
        try {
            SoundUtils.tone(600,600);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    class CountdownTimerTask extends TimerTask {

        @Override
        public void run() {
            if ((!countdown.isPause()) && (!countdown.isFinished())) {
                countdown.decrement();
                count.setForeground(countdown.isCloseToEnd() ? Color.WHITE : Color.DARK_GRAY);
                panel.setBackground(countdown.isCloseToEnd() ? Color.RED : Color.GREEN);
                if (!countdown.toReadableString().equals("00")) {
                    if (countdown.isCloseToEnd()) {
                        beep();
                    }
                    count.setText(countdown.toReadableString());
                } else {
                    beep();
                    count.setText("STOP");
                }

            }
        }
    }

    class BeeperTimerTask extends TimerTask {

        int count;

        public BeeperTimerTask(int count) {
            this.count = count;
        }

        @Override
        public void run() {
            if (count == 0) {
                start();
            } else {
                beep();
                count--;
            }
        }
    }


}