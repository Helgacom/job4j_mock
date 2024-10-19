package ru.checkdev.notification.repository;

import org.springframework.data.repository.CrudRepository;
import ru.checkdev.notification.domain.TgUser;

import java.util.Optional;

public interface TgUserRepository extends CrudRepository<TgUser, Integer> {

    Optional<TgUser> findByChatId(String id);
}
