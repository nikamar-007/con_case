package com.example.conferenceapp.controller;

import com.example.conferenceapp.dao.EventDao;
import com.example.conferenceapp.model.Event;
import com.example.conferenceapp.util.FxUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainController {

    /* ---------- UI-элементы ---------- */
    @FXML private ImageView logoBig;          // логотип в шапке
    @FXML private ComboBox<String> directionFilter;
    @FXML private DatePicker dateFilter;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, ImageView> logoCol;
    @FXML private TableColumn<Event, String> titleCol;
    @FXML private TableColumn<Event, String> directionCol;
    @FXML private TableColumn<Event, String> dateCol;
    @FXML private TableColumn<Event, String> timeCol;
    @FXML private Button loginBtn;

    /* ---------- данные ---------- */
    private final ObservableList<Event> master   = FXCollections.observableArrayList();
    private final FilteredList<Event>   filtered = new FilteredList<>(master, p -> true);
    private final EventDao eventDao = new EventDao();

    /* ---------- инициализация контроллера ---------- */
    public void initialize() {

        /* логотип шапки */
        URL logoUrl = getClass().getResource(
                "/com/example/conferenceapp/images/logo.png");
        if (logoUrl != null)
            logoBig.setImage(new Image(logoUrl.toExternalForm()));

        /* колонки таблицы */
        logoCol.setCellValueFactory(p -> {
            ImageView iv = new ImageView();
            iv.setFitHeight(60);
            iv.setFitWidth(60);
            iv.setPreserveRatio(true);
            FxUtil.loadEventLogo(iv, p.getValue().getLogoPath());
            return new ReadOnlyObjectWrapper<>(iv);
        });
        titleCol    .setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getTitle()));
        directionCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getDirection()));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        dateCol     .setCellValueFactory(p -> {
            if (p.getValue().getStart() == null) {
                return new ReadOnlyObjectWrapper<>("—");
            }
            return new ReadOnlyObjectWrapper<>(p.getValue().getStart().format(df));
        });
        timeCol     .setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(formatPeriod(p.getValue(), tf)));

        eventTable.setItems(filtered);

        /* данные + фильтры */
        loadData();
        loadDirections();
        directionFilter.setOnAction(e -> applyFilters());
        dateFilter     .setOnAction(e -> applyFilters());

        loginBtn.setOnAction(e -> LoginController.open());

        /* двойной клик → карточка Details */
        eventTable.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    DetailsController.open(row.getItem());
                }
            });
            return row;
        });
    }

    private String formatPeriod(Event event, DateTimeFormatter tf) {
        if (event.getStart() == null) {
            return "—";
        }
        String start = event.getStart().format(tf);
        String end = event.getEnd() != null ? event.getEnd().format(tf) : "??";
        return start + " – " + end;
    }


    /* ---------- загрузка всех мероприятий из DAO ---------- */
    private void loadData() {
        List<Event> all = eventDao.find(null, null);
        master.setAll(all);
    }

    private void loadDirections() {
        ObservableList<String> dirs = FXCollections.observableArrayList(eventDao.listDirections());
        directionFilter.setItems(dirs);
        directionFilter.getSelectionModel().clearSelection();
        directionFilter.setValue(null);
    }

    /* ---------- применение фильтров ---------- */
    private void applyFilters() {
        String    dir = directionFilter.getValue();
        LocalDate d   = dateFilter.getValue();

        filtered.setPredicate(ev ->
                (dir == null || ev.getDirection().equals(dir)) &&
                        (d   == null || ev.getStart().toLocalDate().isEqual(d))
        );
    }

    /* ---------- кнопка «Сбросить» ---------- */
    @FXML private void onClearFilters() {
        directionFilter.setValue(null);
        dateFilter.setValue(null);
        applyFilters();
    }
}
