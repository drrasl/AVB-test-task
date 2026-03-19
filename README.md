# 🚀 AVB Invest — Микросервисное приложение для управления компаниями и сотрудниками

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![Docker](https://img.shields.io/badge/Docker-✓-2496ED.svg)](https://www.docker.com/)

## 📋 Описание проекта

Проект представляет собой систему из двух взаимодействующих микросервисов для управления компаниями и их сотрудниками. Сервисы общаются между собой через REST API с использованием технологий Spring Cloud.

### 🔹 Основные возможности

- ✅ Создание, чтение, обновление и удаление компаний (REST интерфейс, CRUD эндпоинты)
- ✅ Управление сотрудниками (пользователями) компаний
- ✅ Межсервисное взаимодействие через Feign Client
- ✅ Отказоустойчивость с Resilience4j Circuit Breaker
- ✅ Централизованная конфигурация через Spring Cloud Config
- ✅ Service Discovery через Netflix Eureka
- ✅ API Gateway для единой точки входа
- ✅ Поддержка PostgreSQL (default) и H2 (test)

---

## 🏗 Архитектура

Микросервисное приложение. У каждого микросервиса бизнес-логики (user-service, company-service) есть своя БД, вокруг микросервисы архетиктуры (discovery-server, config-server, geteway-server).

### 🔹 Инфраструктурные сервисы

| Сервис | Порт | Назначение |
|--------|------|-----------|
| `discovery-server` | 8761 | Eureka Service Discovery |
| `config-server` | 8888 | Spring Cloud Config Server |
| `gateway-server` | 8080 | API Gateway (вход в систему) |

### 🔹 Бизнес-сервисы

| Сервис | Порт | БД | Описание |
|--------|------|----|----------|
| `company-service` | random | PostgreSQL | Управление компаниями |
| `user-service` | random | PostgreSQL | Управление сотрудниками |

---

## 🛠 Технологический стек

```yaml
Язык:           Java 21 (LTS)
Фреймворк:      Spring Boot 3.3.4
Spring Cloud:   2023.0.x (2024.1.0)
Базы данных:    PostgreSQL 16.1 (default), H2 (test)
ORM:            Spring Data JPA + Hibernate 6.5.3
Межсервисное:   OpenFeign + Spring Cloud LoadBalancer
Отказоустойчивость: Resilience4j Circuit Breaker
Конфигурация:   Spring Cloud Config Server (native)
Service Discovery: Netflix Eureka
API Gateway:    Spring Cloud Gateway
Контейнеризация: Docker + Docker Compose
Утилиты:        Lombok, Spring Boot Actuator, Validation API
```

---

## 📦 Структура проекта

```
avb-invest/
├── core/
│   ├── company-service/          # Микросервис компаний
│   │   ├── src/main/java/com/avbinvest/company/
│   │   │   ├── controller/       # REST контроллеры
│   │   │   ├── service/          # Бизнес-логика
│   │   │   ├── repository/       # JPA репозитории
│   │   │   ├── model/            # JPA сущности
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── client/           # Feign клиенты
│   │   │   └── exception/        # Обработка исключений
│   │   ├── src/main/resources/
│   │   │   ├── application.yml   # Конфигурация
│   │   │   └── schema.sql        # Схема БД для H2
│   │   └── pom.xml
│   │
│   └── user-service/             # Микросервис пользователей
│       └── ... (аналогичная структура)
│
├── infra/
│   ├── discovery-server/         # Eureka Server
│   ├── config-server/            # Config Server
│   └── gateway-server/           # API Gateway
│
├── docker-compose.yml            # Оркестрация контейнеров
├── .gitignore
└── README.md
```

---

## 🔧 Функциональность

### 👤 User Service — Управление сотрудниками

#### Сущность `User`

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | `Long` | Уникальный идентификатор (автоинкремент) |
| `firstName` | `String` | Имя (3–250 символов) |
| `lastName` | `String` | Фамилия (3–250 символов) |
| `phone` | `String` | Телефон (1–20 символов) |
| `companyId` | `Long` | ID компании (внешний ключ) |

#### Эндпоинты

```http
GET    /users              # Получить всех пользователей
GET    /users/{id}         # Получить пользователя по ID
POST   /users              # Создать нового пользователя
PATCH  /users/{id}         # Обновить пользователя
DELETE /users/{id}         # Удалить пользователя
```

---

### 🏢 Company Service — Управление компаниями

#### Сущность `Company`

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | `Long` | Уникальный идентификатор (автоинкремент) |
| `companyName` | `String` | Название компании (3–250 символов) |
| `budget` | `BigDecimal` | Бюджет (точность 15,2) |

#### Таблица связей `company_employees`

```sql
CREATE TABLE company_employees (
    company_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    PRIMARY KEY (company_id, employee_id),
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);
```

#### Эндпоинты

```http
GET    /companies              # Получить все компании
GET    /companies/{id}         # Получить компанию по ID
POST   /companies              # Создать новую компанию
PATCH  /companies/{id}         # Обновить компанию
DELETE /companies/{id}         # Удалить компанию
```

---

## 🔄 Межсервисное взаимодействие

### 🔹 Feign Clients

#### User Service → Company Service

```java
@FeignClient(name = "company-service")
public interface CompanyClient {
    
    @GetMapping("/internal/companies/{id}")
    CompanyShortDto getCompanyById(@PathVariable Long id);
    
    @PostMapping("/internal/companies/by-ids")
    List<CompanyShortDto> getCompaniesByIds(@RequestBody List<Long> ids);
}
```

#### Company Service → User Service

```java
@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/internal/users")
    List<UserShortDto> getUsersByIds(@RequestParam List<Long> userIds);
}
```

### 🔹 Circuit Breaker (Resilience4j)

```yaml
resilience4j.circuitbreaker:
  configs:
    default:
      slidingWindowSize: 10
      failureRateThreshold: 50
      waitDurationInOpenState: 10000
      permittedNumberOfCallsInHalfOpenState: 3
      minimumNumberOfCalls: 5
      automaticTransitionFromOpenToHalfOpenEnabled: true
```

---

## 🐳 Запуск через Docker Compose

### 🔹 Предварительные требования

```bash
# Установите Docker Desktop:
# https://www.docker.com/products/docker-desktop

# Проверьте версии:
docker --version      # ≥ 24.0
docker compose version # ≥ 2.20
```

### 🔹 Порядок запуска

```bash
# 1. Клонировать репозиторий
git clone <repository-url>
cd avb-invest

# 2. Собрать артефакты (если не собраны)
mvn clean package -DskipTests

# 3. Запустить весь стек
docker compose up -d --build

# 4. Проверить статус
docker compose ps

# 5. Остановить
docker compose down

# 6. Очистить данные (осторожно!)
docker compose down -v
```

### 🔹 Порты сервисов

| Сервис | Контейнер | Хост | Описание |
|--------|-----------|------|----------|
| Gateway | 8080 | 8080 | Единая точка входа |
| Eureka | 8761 | 8761 | Service Discovery UI |
| Config Server | 8888 | 8888 | Конфигурационный сервер |
| PostgreSQL | 5432 | 5432 | Основная БД |
| Company Service | random | — | Внутренний порт |
| User Service | random | — | Внутренний порт |

---

## 🌐 Доступ к сервисам

### 🔹 Основные эндпоинты

```bash
# API Gateway (все запросы через него)
http://localhost:8080

# Eureka Dashboard
http://localhost:8761

# Config Server health
http://localhost:8888/actuator/health

# H2 Console (только для профиля test)
http://localhost:<service-port>/h2-console
```
---

## 🔒 Особенности реализации

1. **Уникальность пользователей**: составной уникальный индекс `(first_name, last_name, phone_number)`
2. **Каскадное удаление**: при удалении компании удаляются связи в `company_employees`
3. **Транзакционность**: все операции с БД обернуты в `@Transactional`
4. **Валидация**: входных данных через `@Valid` и `@Validated`
5. **Отказоустойчивость**: Circuit Breaker для межсервисных вызовов
6. **Логирование**: структурированные логи с уровнями DEBUG/INFO/ERROR
7. **Actuator**: мониторинг здоровья и метрик через `/actuator/*`

---

## 📊 Мониторинг и отладка

### 🔹 Actuator Endpoints

```bash
# Health check
GET /actuator/health

# Info about application
GET /actuator/info

# Metrics (если подключен micrometer)
GET /actuator/metrics

# Circuit Breaker events
GET /actuator/circuitbreakers
```

### 🔹 Просмотр логов

```bash
# Все логи в реальном времени
docker compose logs -f

# Логи конкретного сервиса
docker compose logs -f company-service

# Последние 100 строк + следование
docker compose logs -f --tail 100 user-service

# С временными метками
docker compose logs -ft gateway-server
```
---
