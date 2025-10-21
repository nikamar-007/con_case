package com.example.conferenceapp.controller;

import com.example.conferenceapp.dao.UserDao;
import com.example.conferenceapp.model.User;
import com.example.conferenceapp.util.CaptchaUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class LoginController {

    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView captchaImage;
    @FXML private TextField captchaInput;
    @FXML private CheckBox rememberCheck;
    @FXML private Label errorLabel;
    @FXML private Label lockLabel;
    @FXML private Button loginBtn;

    private final UserDao userDao = new UserDao();
    private String captchaCode = "";
    private int attempts = 0;
    private boolean locked = false;

    public void initialize() {
        refreshCaptcha();
    }

    @FXML private void onRefreshCaptcha() { refreshCaptcha(); }

    private void refreshCaptcha() {
        CaptchaUtil.Captcha c = CaptchaUtil.generate();
        captchaImage.setImage(c.image());
        captchaCode = c.code();
        captchaInput.clear();
    }

    @FXML private void onLogin() {
        if (locked) return;

        // проверка CAPTCHA
        if (!captchaInput.getText().equalsIgnoreCase(captchaCode)) {
            showError("Неверная CAPTCHA");
            handleFailedAttempt();
            return;
        }

        String idNum = idField.getText().trim();
        String pwd = hash(passwordField.getText());

        User u = userDao.authenticate(idNum, pwd);
        if (u == null) {
            showError("Неверный ID или пароль");
            handleFailedAttempt();
        } else {
            // успех — открыть окно в зависимости от роли
            openRoleWindow(u);
            ((Stage) loginBtn.getScene().getWindow()).close();
        }
    }

    private void handleFailedAttempt() {
        attempts++;
        if (attempts >= 3) {
            lockFor10s();
        }
        refreshCaptcha();
    }

    private void lockFor10s() {
        locked = true;
        lockLabel.setText("Блокировка на 10 секунд...");
        loginBtn.setDisable(true);

        new Thread(() -> {
            try { Thread.sleep(10_000); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                locked = false;
                attempts = 0;
                lockLabel.setText("");
                loginBtn.setDisable(false);
            });
        }).start();
    }

    private static String hash(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(pwd.getBytes());
            return Base64.getEncoder().encodeToString(h);
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }

    private void openRoleWindow(User u) {
        String fxml = switch (u.getRole()) {
            case ORGANIZER  -> "/com/example/conferenceapp/fxml/Organizer.fxml";
            case MODERATOR  -> "/com/example/conferenceapp/fxml/Moderator.fxml";
            case JURY       -> "/com/example/conferenceapp/fxml/Jury.fxml";
            default         -> "/com/example/conferenceapp/fxml/Participant.fxml";
        };
        try {
            Stage st = new Stage();
            st.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
            st.setTitle("Добро пожаловать, " + u.getFullName());
            st.initModality(Modality.WINDOW_MODAL);
            st.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ---- открыть модальное окно авторизации из MainController
    public static void open() {
        try {
            Stage st = new Stage();
            st.setScene(new Scene(FXMLLoader.load(LoginController.class.getResource(
                    "/com/example/conferenceapp/fxml/Login.fxml"))));
            st.setTitle("Авторизация");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
