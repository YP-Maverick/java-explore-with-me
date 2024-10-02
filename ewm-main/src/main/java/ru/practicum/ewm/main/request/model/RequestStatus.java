package ru.practicum.ewm.main.request.model;

public enum RequestStatus {
    PENDING,   // Ожидает подтверждения
    CONFIRMED, // Подтвержден
    REJECTED,  // Отклонен
    CANCELED   // Отменен
}