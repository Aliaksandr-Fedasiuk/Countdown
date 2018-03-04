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
    private TimerTask timerTask = new MyTimerTask();

    private JPanel panel;
    private JLabel count;
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
                if (e.getKeyChar() == 32) {
                    if (countdown.isFinished()) {
                        init();
                    } else {
                        if (countdown.isPause()) {
                            start();
                        } else {
                            pause();
                        }
                    }
                } else if (e.getKeyChar() == 27) {
                    init();
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

    public void init() {
        countdown.reset();
        countdown.setPause(true);
        count.setForeground(Color.DARK_GRAY);
        panel.setBackground(Color.GREEN);
        count.setText(countdown.toReadableString());
        System.out.println("TimerTask started.");
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
        count.setFont(new Font("Arial Black", Font.BOLD, 500));
        count.setHorizontalAlignment(SwingConstants.CENTER);
        count.setVerticalAlignment(SwingConstants.CENTER);
        count.setForeground(Color.DARK_GRAY);
        panel = new JPanel(new GridLayout(1, 1));
        panel.add(count);
        panel.setBackground(Color.GREEN);
        return panel;
    }

    private void startTimer() {
        count.setText(countdown.toReadableString());
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
        System.out.println("TimerTask started.");
    }

    class MyTimerTask extends TimerTask {

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

        private void beep() {
            try {
                SoundUtils.tone(600,500);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

}