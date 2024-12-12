package ru.mirea.pkmn.repository;

import ru.mirea.pkmn.entity.CardEntity;
import ru.mirea.pkmn.entity.StudentEntity;

import java.util.UUID;

public interface PkmnRepository {

    CardEntity getCard(String name);

    CardEntity getCard(UUID uuid);

    CardEntity getCard(StudentEntity student);

    StudentEntity getStudent(String fullName);

    StudentEntity getStudent(UUID uuid);

    void saveCard(CardEntity card);

    void saveStudent(StudentEntity student);

    boolean cardExists(String cardName);
}
