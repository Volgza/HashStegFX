package com.hashsteg.hashstegfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Controller {
    FileChooser fileCh = new FileChooser();
    @FXML
    private Pane rootPane;
    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextField SecretMess;

    @FXML
    private TextField KeyField;

    @FXML
    private void handleButtonAction1(ActionEvent event) throws IOException {
        System.out.println("вложение");
        Pane pane = FXMLLoader.load(getClass().getResource("input.fxml"));
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void handleButtonAction2(ActionEvent event) throws IOException {
        System.out.println("извлечение");
        Pane pane2 = FXMLLoader.load(getClass().getResource("output.fxml"));
        rootPane.getChildren().setAll(pane2);
    }
    @FXML
    private void inputSecretMess(ActionEvent event) throws IOException {
        System.out.println("редактирование");
        Parent hashpane = (Parent) FXMLLoader.load(getClass().getResource("hash.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Редактирование текста");
        stage.setScene(new Scene(hashpane));
        stage.show();
    }

    @FXML
    private void fileChooser(ActionEvent event) throws IOException{
        fileCh.setTitle("Выберете файл");
        fileCh.getExtensionFilters().add(new FileChooser.ExtensionFilter(".txt", "*.txt"));
        File file = fileCh.showOpenDialog(null);

        BufferedReader ReadText = new BufferedReader(new FileReader(file));
        String lineText;
        StringBuilder appendText = new StringBuilder();
        while ((lineText = ReadText.readLine()) != null)
            appendText.append(lineText);
        String[] array = appendText.toString().split(",|\\.|\\;|\"");
            /*for (String word : array) {
                System.out.println(word);
            }*/
        /*String[] arrayForMess = new String[arrayChar.length];
        if (array.length < arrayChar.length)
            System.out.println("Текст недостаточно длинный для вложения");
        else {

            for (int i = 0; i < arrayForMess.length; i++) {
                arrayForMess[i] = array[i];
            }
        }*/

        inputTextArea.setText(appendText.toString());

    }
    @FXML
    private void generateKey(ActionEvent event) {
        SecureRandom randomKey = new SecureRandom();
        int length = SecretMess.getLength();
        short[] arrayKey = new short[length];
        for (int l = 0; l < length; l++) {
            arrayKey[l] = (short) randomKey.nextInt();
        }
        StringBuilder Key = new StringBuilder();
        for (int i=0; i<arrayKey.length; i++) {
            String keyPart = Integer.toBinaryString(arrayKey[i]);
            if (keyPart.length()<32) {
                int LengthOfZero = 32 - keyPart.length();
                keyPart = "0".repeat(LengthOfZero) + keyPart;
                Key.append(keyPart, 21, 32);
            } else {
                Key.append(keyPart, 21, 32);
            }

        }
        KeyField.setText(Key.toString());

    }

}