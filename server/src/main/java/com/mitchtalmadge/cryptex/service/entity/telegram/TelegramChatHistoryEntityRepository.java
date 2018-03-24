package com.mitchtalmadge.cryptex.service.entity.telegram;

import com.mitchtalmadge.cryptex.domain.entity.telegram.TelegramChatHistoryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramChatHistoryEntityRepository extends CrudRepository<TelegramChatHistoryEntity, Long> {

    TelegramChatHistoryEntity findFirstByPhoneNumberAndChatId(String phoneNumber, int chatId);

}
