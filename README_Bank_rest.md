# Система управления банковскими картами
REST API для управления банковскими картами с ролями ADMIN/USER, JWT-аутентификацией,
шифрованием данных, переводами между картами и Docker-поддержкой.

## Технологии
- Java 17
- Spring Boot 3
- Spring Security + JWT (в кукис)
- Spring Data JPA + Hibernate
- PostgreSQL
- Liquibase (миграции)
- MapStruct
- Swagger / OpenAPI
- Docker + Docker Compose

## Запуск локально (без Docker)
1. Установи JDK 17 и PostgreSQL
2. Создай базу данных `bankcard_db` (пользователь/пароль: postgres/postgres)
3. Создай файл `.env` в корне проекта (пример ниже) и заполни секреты
4. Запусти приложение: ./mvnw spring-boot:run или через IDE (Run BankcardApplication)

## Запуск через Docker Compose (рекомендуемый способ)
1. Установи Docker Desktop
2. В корне проекта создай файл `.env` (пример ниже)
3. Запусти: docker-compose up --build
4. Приложение будет доступно на http://localhost:8080/swagger-ui.html

**Важно:** первый запуск создаст чистую БД.  
Чтобы создать первого администратора:

- Подключись к PostgreSQL в контейнере:
  docker exec -it bankcard_postgres psql -U postgres -d bankcard_db
- Вставь админа (пароль "admin123"):
```sql
INSERT INTO users (id, username, password, email, role, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'admin',
    '$2a$10$1g1g1g1g1g1g1g1g1g1g1u1g1g1g1g1g1g1g1g1g1g1g1g1g1g',  -- BCrypt-хеш для "admin123"
    'admin@example.com',
    'ROLE_ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

Выйди из psql: \q
Логинься в Swagger: POST /api/auth/login → username: admin, password: admin123
После этого создавай юзеров, карты и тестируй переводы.

Файл .env (секреты — не коммитим в Git!)
Создай файл .env в корне проекта (он в .gitignore):
text# PostgreSQL (для локального запуска без Docker)
DB_URL=jdbc:postgresql://localhost:5432/bankcard_db
DB_USERNAME=имя бд
DB_PASSWORD=твой пароль

# JWT
JWT_SECRET=your-super-secure-secret-key-1234567890abcdef1234567890
JWT_EXPIRATION=86400000

# Шифрование номеров карт (AES-256, 32 символа)
CARD_ENCRYPTION_KEY=ThisIsMySuperSecret16ByteKey1234
Для Docker — эти же переменные подтягиваются автоматически из .env (если он есть) 
или задаются в docker-compose.yml через environment.

Важно: докер-компоуст также поставили в гит игнор так, как он содержит хард код, но 
там все береться из environment (мой пример того что было в файле по хард коду): 
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bankcard_db
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - JWT_SECRET=your-super-secure-secret-key-1234567890abcdef1234567890
      - CARD_ENCRYPTION_KEY=ThisIsMySuperSecret16ByteKey1234

Основные эндпоинты (Swagger)
Аутентификация: POST /api/auth/login
Создание юзера (админ): POST /api/admin/users
Создание карты (админ): POST /api/admin/cards?userId={id}
Свои карты (юзер): GET /api/cards/my?page=0&size=10&status=ACTIVE
Перевод между своими картами (юзер): POST /api/transfers

Полная документация: http://localhost:8080/swagger-ui.html
