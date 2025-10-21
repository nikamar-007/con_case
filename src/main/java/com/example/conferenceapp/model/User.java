package com.example.conferenceapp.model;

/**
 * Сущность пользователя системы.
 * <pre>
 *  id            — PK из БД
 *  idNumber      — отображаемый «ID Number»
 *  fullName      — Фамилия Имя Отчество одной строкой
 *  role          — роль (Participant / Moderator / Organizer / Jury)
 *  photoPath     — относительный или абсолютный путь к фотографии
 * </pre>
 */
public class User {

    public enum Role { PARTICIPANT, MODERATOR, ORGANIZER, JURY }

    private final int    id;
    private final String idNumber;
    private final String fullName;
    private final Role   role;
    private final String photoPath;

    public User(int id,
                String idNumber,
                String fullName,
                Role role,
                String photoPath) {
        this.id        = id;
        this.idNumber  = idNumber;
        this.fullName  = fullName;
        this.role      = role;
        this.photoPath = photoPath;
    }

    /* ---------- getters ---------- */

    public int    getId()        { return id; }
    public String getIdNumber()  { return idNumber; }
    public String getFullName()  { return fullName; }
    public Role   getRole()      { return role; }
    public String getPhotoPath() { return photoPath; }

    /* ---------- «Имя» и «Отчество» из fullName ---------- */

    /** вернёт 2-е слово из ФИО либо всё ФИО, если пробелов нет */
    public String getFirstName() {
        String[] p = fullName.split("\\s+");
        return p.length > 1 ? p[1] : fullName;
    }

    /** вернёт 3-е слово (отчество) либо пустую строку */
    public String getMiddleName() {
        String[] p = fullName.split("\\s+");
        return p.length > 2 ? p[2] : "";
    }
}
