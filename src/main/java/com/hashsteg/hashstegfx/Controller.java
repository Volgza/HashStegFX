package com.hashsteg.hashstegfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

public class Controller {
    FileChooser fileCh = new FileChooser();
    StringBuilder AppendText = new StringBuilder();
    static int MessLength;
    short[] arrayKey;
    static StringBuilder cipherSb = new StringBuilder();
    static String[] arrayForMess;
    static String HashBit;
    StringBuilder OutAppendText = new StringBuilder();
    Stage InputStage = new Stage();
    @FXML
    private TextField CurrentSentence;
    @FXML
    private TextField CurrentValue;
    @FXML
    private TextField NeedValue;

    @FXML
    private Pane rootPane;
    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextArea outputTextArea;

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
    private void inputSecretMess(ActionEvent event) throws IOException, NoSuchAlgorithmException {

        MessLength = SecretMess.getLength();
        StringBuffer appendMess = new StringBuffer();
        String[] StringArMess = new String[MessLength];
        for (int i = 0; i < MessLength; i++) {
            int asciiValue = SecretMess.getText().charAt(i);
            String binaryMess = Integer.toBinaryString(asciiValue);
            if (binaryMess.length() < 11) {
                int LengthOfZero = 11 - binaryMess.length();
                binaryMess = "0".repeat(LengthOfZero) + binaryMess;
                appendMess.append(binaryMess);
                StringArMess[i] = binaryMess;
            } else {
                appendMess.append(binaryMess);
                StringArMess[i] = binaryMess;
            }
        }

        short[] appendMessAr = new short[MessLength];
        for (int i = 0; i < MessLength; i++) {
            appendMessAr[i] = Short.parseShort(StringArMess[i], 2);
        }

        short[] cipherMess = new short[MessLength];
        for (int i = 0; i < cipherMess.length; i++) {
            cipherMess[i] = (short) (appendMessAr[i] ^ arrayKey[i]);
        }

        for (short cipM : cipherMess) {
            String StcipM = Integer.toBinaryString(cipM);
            if (StcipM.length() < 32) {
                int LengthOfZero = 32 - StcipM.length();
                StcipM = "0".repeat(LengthOfZero) + StcipM;
                cipherSb.append(StcipM.substring(21, 32));
            } else {
                cipherSb.append(StcipM.substring(21, 32));
            }
        }
        System.out.println("зашифрованная строка\n" + cipherSb);

        char[] arrayChar = cipherSb.toString().toCharArray();
        //String[] array = appendText.toString().split(",|\\.|\\;|\"");
        String[] array = AppendText.toString().split("(?<=[.,!?:])\\s");

        arrayForMess = new String[arrayChar.length];
        if (array.length < arrayChar.length)
            System.out.println("Текст недостаточно длинный для вложения");
        else {

            for (int i = 0; i < arrayForMess.length; i++) {
                arrayForMess[i] = array[i];
            }
        }
        System.out.println(Arrays.toString(arrayForMess));

        MessageDigest md = MessageDigest.getInstance("MD5");
        char[] firstbit = new char[arrayForMess.length];
        for (int i = 0; i < arrayForMess.length; i++) {
            byte[] hash = md.digest(arrayForMess[i].getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < hash.length; j++) {
                String s = Integer.toBinaryString(0xff & hash[j]);
                if (s.length() < 8) {
                    int LengthOfZero = 8 - s.length();
                    s = "0".repeat(LengthOfZero) + s;
                }
                sb.append(s);
            }
            //System.out.println(String.format("%s", sb.toString()));
            firstbit[i] = sb.charAt(0);
        }
        StringBuilder cipherInSb = new StringBuilder();
        for (int k = 0; k < firstbit.length; k++) {
            cipherInSb.append(firstbit[k]);
        }
        HashBit = cipherInSb.toString();

        System.out.println("редактирование");
        Parent hashpane = (Parent) FXMLLoader.load(getClass().getResource("hash.fxml"));
        InputStage.setTitle("Редактирование текста");
        InputStage.setScene(new Scene(hashpane));
        InputStage.show();
    }

    int CounterForNext = 0;

    @FXML
    private void NextSentence(ActionEvent event) {
        CurrentSentence.setText(arrayForMess[CounterForNext]);
        CurrentValue.setText(String.valueOf(HashBit.charAt(CounterForNext)));
        NeedValue.setText(String.valueOf(cipherSb.charAt(CounterForNext)));
        if (cipherSb.charAt(CounterForNext) != HashBit.charAt(CounterForNext)) {
            CurrentValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(255, 102, 102);");
            NeedValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(255, 102, 102);");
        } else {
            CurrentValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(144, 238, 144);");
            NeedValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(144, 238, 144);");
        }
        CounterForNext++;

    }


    @FXML
    private void ApplySentence(ActionEvent event) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(CurrentSentence.getText().getBytes(StandardCharsets.UTF_8));
        StringBuilder HashBuilder = new StringBuilder();
            for (int j = 0; j < hash.length; j++) {
                String s = Integer.toBinaryString(0xff & hash[j]);
                if (s.length() < 8) {
                    int LengthOfZero = 8 - s.length();
                    s = "0".repeat(LengthOfZero) + s;
                }
                HashBuilder.append(s);

        }
        //System.out.println(HashBuilder);
        CurrentValue.setText(String.valueOf(HashBuilder.charAt(0)));
        if (Objects.equals(NeedValue.getText(), CurrentValue.getText())) {
            CurrentValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(144, 238, 144);");
            NeedValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(144, 238, 144);");
            OutAppendText.append(CurrentSentence.getText());
            System.out.print(CounterForNext+" ");
            System.out.println(cipherSb);


        }
        else {
            CurrentValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(255, 102, 102);");
            NeedValue.setStyle("-fx-text-inner-color: black; -fx-background-color: rgb(255, 102, 102);");
        }
        if ((Objects.equals(NeedValue.getText(), CurrentValue.getText())) && CounterForNext == cipherSb.length()) {
            InputStage.close();
        }
        outputTextArea.setText(OutAppendText.toString());

    }
    @FXML
    private void fileChooser(ActionEvent event) throws IOException{
        fileCh.setTitle("Выберете файл");
        fileCh.getExtensionFilters().add(new FileChooser.ExtensionFilter(".txt", "*.txt"));
        File file = fileCh.showOpenDialog(null);

        BufferedReader ReadText = new BufferedReader(new FileReader(file));
        String lineText;
        while ((lineText = ReadText.readLine()) != null)
            AppendText.append(lineText);
        inputTextArea.setText(AppendText.toString());

    }
    @FXML
    private void generateKey(ActionEvent event) {
        SecureRandom randomKey = new SecureRandom();
        MessLength = SecretMess.getLength();
        arrayKey = new short[MessLength];
        for (int l = 0; l < MessLength; l++) {
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
    @FXML
    private void CopyToClipboard(ActionEvent event){
        ClipboardContent ClipCont = new ClipboardContent();
        ClipCont.putString(KeyField.getText());
        Clipboard.getSystemClipboard().setContent(ClipCont);
    }

}