# Sprint_7
## О проекте
Автотесты API для проверки учебного сервиса Яндекс.Самокат: http://qa-scooter.praktikum-services.ru/

Документация API: https://qa-scooter.praktikum-services.ru/docs/#api-Orders-GetOrderByTrackNumber

## Используемые инструменты
- Java 11
- maven 4.0.0
- JUnit 4.13.2
- rest-assured 5.3.0
- allure 2.15.0
- maven-surefire-plugin 2.22.2
- gson 2.10.1
- javafaker 1.0.2

## Запуск тестов и построение отчёта:
- mvn clean test
- mvn allure:serve