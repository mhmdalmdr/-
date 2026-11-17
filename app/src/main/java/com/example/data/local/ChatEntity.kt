package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ChatMessage
import com.example.model.GroundingSource

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val stepGuidance: String? = null,
    val groundingSources: List<GroundingSource> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val isSearchGrounded: Boolean = false,
    val attachmentName: String? = null,
    val attachmentMimeType: String? = null,
    val tutorRoleName: String? = null,
    val modelUsed: String? = null
) {
    fun toChatMessage(): ChatMessage {
        return ChatMessage(
            id = id,
            text = text,
            isUser = isUser,
            timestamp = timestamp,
            stepGuidance = stepGuidance,
            groundingSources = groundingSources,
            searchQueries = searchQueries,
            isSearchGrounded = isSearchGrounded,
            attachmentName = attachmentName,
            attachmentMimeType = attachmentMimeType,
            tutorRoleName = tutorRoleName,
            modelUsed = modelUsed
        )
    }

    companion object {
        fun fromChatMessage(message: ChatMessage): ChatEntity {
            return ChatEntity(
                id = message.id,
                text = message.text,
                isUser = message.isUser,
                timestamp = message.timestamp,
                stepGuidance = message.stepGuidance,
                groundingSources = message.groundingSources,
                searchQueries = message.searchQueries,
                isSearchGrounded = message.isSearchGrounded,
                attachmentName = message.attachmentName,
                attachmentMimeType = message.attachmentMimeType,
                tutorRoleName = message.tutorRoleName,
                modelUsed = message.modelUsed
            )
        }
    }
}
