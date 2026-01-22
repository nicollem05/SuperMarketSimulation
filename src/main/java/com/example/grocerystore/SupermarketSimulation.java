package com.example.grocerystore;
//ADD ALL THE LIBRARIES THAT WE ARE GOING TO USE
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SupermarketSimulation extends Application {

    // CREATE CUSTOMER CLASS AND THE VARIABLES
    static class Customer {
        int arrivalTime;
        public Customer(int time) {
            this.arrivalTime = time;
        }
    }

    // WE ADD EVERYTHING FOR THIS SIMULATION
    private Queue<Customer> queue = new LinkedList<>();
    private Random random = new Random();
    private int currentMinute = 0;
    private int nextArrivalTime = random.nextInt(4) + 1;
    private int serviceEndTime = -1;
    private boolean isCashierBusy = false;
    private int maxQueueSize = 0;
    private int maxWaitTime = 0;

    // GRAPHIC ELEMENTS
    private Label statsLabel = new Label("Minute: 0 | Max num line: 0 | Max waiting: 0");
    private HBox queueContainer = new HBox(5);
    private Rectangle cashierShape = new Rectangle(50, 50, Color.LIGHTGRAY);
    private Timeline simulationTimeline;

    public void start(Stage stage) {
        // CREATE THE WINDOW AND PUT ALL THE ELEMENTS
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("SuperMarket Simulation");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox visualsArea = new HBox(20);
        visualsArea.setStyle("-fx-alignment: center-left;");

        VBox cashierBox = new VBox(5, new Label("Cashier"), cashierShape);
        cashierBox.setStyle("-fx-alignment: center;");

        visualsArea.getChildren().addAll(cashierBox, queueContainer);
        root.getChildren().addAll(title, statsLabel, visualsArea);

        // CREATE THE TIMER AND DO THE CONVERSION
        simulationTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> simulateOneMinute()));
        simulationTimeline.setCycleCount(720);
        simulationTimeline.setOnFinished(e -> {
            statsLabel.setText(statsLabel.getText() + " - ¡Is the end of the day!");
            cashierShape.setFill(Color.LIGHTGRAY);
        });

        // SHOW THE TABLE
        Scene scene = new Scene(root, 600, 300);
        stage.setTitle("Supermarket Simulator");
        stage.setScene(scene);
        stage.show();

        simulationTimeline.play();
    }

    private void simulateOneMinute() {
        currentMinute++;

        // USE THE CASHIER
        if (currentMinute == nextArrivalTime) {
            queue.add(new Customer(currentMinute));
            nextArrivalTime = currentMinute + random.nextInt(4) + 1;
        }

        // CHECK IF IT FINISHED
        if (isCashierBusy && currentMinute >= serviceEndTime) {
            isCashierBusy = false;
            cashierShape.setFill(Color.LIGHTPINK);
        }

        // IF THE CASHIER IS WORKING ON A CUSTOMER
        if (!isCashierBusy && !queue.isEmpty()) {
            Customer servedCustomer = queue.poll();
            int waitTime = currentMinute - servedCustomer.arrivalTime;
            if (waitTime > maxWaitTime) maxWaitTime = waitTime;

            isCashierBusy = true;
            int serviceDuration = random.nextInt(4) + 1;
            serviceEndTime = currentMinute + serviceDuration;
            cashierShape.setFill(Color.LIGHTCORAL);
        }

        if (queue.size() > maxQueueSize) maxQueueSize = queue.size();

        updateGraphics();
    }

    private void updateGraphics() {
        statsLabel.setText("Minute: " + currentMinute +
                " | Actual line: " + queue.size() +
                " | Max line: " + maxQueueSize +
                " | Max Waiting time: " + maxWaitTime + " min");

        queueContainer.getChildren().clear();
        for (int i = 0; i < queue.size(); i++) {
            queueContainer.getChildren().add(new Circle(15, Color.BLUEVIOLET));
        }
    }
}