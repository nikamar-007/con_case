package com.example.conferenceapp.controller;

import com.example.conferenceapp.model.User;
import com.example.conferenceapp.util.FxUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ProfileController {

    @FXML private ImageView photo;
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private Label idLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label directionLabel;
    @FXML private Label countryLabel;
    @FXML private Label birthLabel;
    @FXML private Label genderLabel;

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void setUser(User user) {
        FxUtil.loadUserPhoto(photo, user.getPhotoPath());
        nameLabel.setText(user.getRawFullName() != null ? user.getRawFullName() : user.getFullName());
        roleLabel.setText(roleToLabel(user.getRole()));
        idLabel.setText(user.getIdNumber());
        emailLabel.setText(user.getEmail() != null ? user.getEmail() : "—");
        phoneLabel.setText(user.getPhone() != null ? user.getPhone() : "—");
        directionLabel.setText(user.getDirectionName() != null ? user.getDirectionName() : "—");
        countryLabel.setText(user.getCountryName() != null ? user.getCountryName() : "—");
        if (user.getBirthDate() != null) {
            birthLabel.setText(user.getBirthDate().format(dateFmt));
        } else {
            birthLabel.setText("—");
        }
        genderLabel.setText(genderToLabel(user.getGender()));
    }

    private String genderToLabel(String gender) {
        if (gender == null || gender.isBlank()) {
            return "—";
        }
        return switch (gender.toLowerCase()) {
            case "male" -> "Мужской";
            case "female" -> "Женский";
            default -> gender;
        };
    }

    private String roleToLabel(User.Role role) {
        return switch (role) {
            case ORGANIZER -> "Организатор";
            case MODERATOR -> "Модератор";
            case JURY -> "Жюри";
            default -> "Участник";
        };
    }

    public static void open(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(ProfileController.class.getResource(
                    "/com/example/conferenceapp/fxml/Profile.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Профиль пользователя");
            Scene scene = new Scene(loader.load());
            FxUtil.applyAppStyles(scene);
            FxUtil.applyAppIcon(stage);
            stage.setScene(scene);
            ProfileController controller = loader.getController();
            controller.setUser(user);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
