package com.example.data.local

import com.example.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(private val chatDao: ChatDao) {

    val messages: Flow<List<ChatMessage>> = chatDao.getAllMessages().map { entities ->
        entities.map { it.toChatMessage() }
    }

    suspend fun saveMessage(message: ChatMessage) {
        chatDao.insertMessage(ChatEntity.fromChatMessage(message))
    }

    suspend fun saveMessages(messages: List<ChatMessage>) {
        chatDao.insertMessages(messages.map { ChatEntity.fromChatMessage(it) })
    }

    suspend fun deleteMessageById(id: String) {
        chatDao.deleteMessageById(id)
    }

    suspend fun clearHistory() {
        chatDao.clearAllMessages()
    }

    suspend fun getMessageCount(): Int {
        return chatDao.getMessageCount()
    }

    suspend fun seedInitialIfEmpty(seedMessages: List<ChatMessage>) {
        if (chatDao.getMessageCount() == 0) {
            chatDao.insertMessages(seedMessages.map { ChatEntity.fromChatMessage(it) })
        }
    }
}
