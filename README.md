# ORM Educational Platform

Учебная платформа для онлайн-курсов по ORM и Hibernate.

## Технологии

- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Data JPA / Hibernate**
- **PostgreSQL** (production) / **H2** (tests)
- **Maven** для сборки
- **Docker / Docker Compose** для контейнеризации

## Структура проекта

```
org.example.orm/
├── entity/          — 15 JPA-сущностей + 3 enum
├── repository/      — 15 Spring Data JPA репозиториев
├── service/         — 7 сервисных классов
├── controller/      — 5 REST контроллеров
├── dto/             — DTO для API
├── exception/       — GlobalExceptionHandler
└── demo/            — DemoDataLoader (предзаполнение)
```

## Сущности (15 шт.)

| Сущность | Описание | Связи |
|---|---|---|
| User | Пользователь (STUDENT/TEACHER/ADMIN) | 1-1 Profile, 1-M Courses/Enrollments/Submissions/Reviews |
| Profile | Профиль пользователя | 1-1 User |
| Category | Категория курса | 1-M Courses |
| Course | Учебный курс | M-1 Category/Teacher, 1-M Modules/Enrollments/Reviews, M-M Tags |
| Enrollment | Запись студента на курс | M-1 Course/Student |
| Module | Модуль курса | M-1 Course, 1-M Lessons, 1-1 Quiz |
| Lesson | Урок | M-1 Module, 1-M Assignments |
| Assignment | Задание | M-1 Lesson, 1-M Submissions |
| Submission | Решение студента | M-1 Assignment/Student |
| Quiz | Тест | 1-1 Module, 1-M Questions |
| Question | Вопрос теста | M-1 Quiz, 1-M AnswerOptions |
| AnswerOption | Вариант ответа | M-1 Question |
| QuizSubmission | Результат теста | M-1 Quiz/Student |
| CourseReview | Отзыв о курсе | M-1 Course/Student |
| Tag | Тег курса | M-M Course |

## Типы связей

- **OneToOne**: User ↔ Profile
- **ManyToOne**: Course → Category/Teacher, Module → Course, Lesson → Module, Assignment → Lesson, Submission → Assignment/Student, Quiz → Module, Question → Quiz, AnswerOption → Question, QuizSubmission → Quiz/Student, CourseReview → Course/Student, Enrollment → Course/Student
- **OneToMany**: Category → Courses, Course → Modules/Enrollments/Reviews, Module → Lessons, Lesson → Assignments, Quiz → Questions, Question → AnswerOptions
- **ManyToMany**: Course ↔ Tag

## Запуск

### Требования

- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### Вариант 1: Локальный запуск

#### Настройка базы данных

```bash
createdb orm_platform
```

#### Запуск приложения

```bash
mvn spring-boot:run
```

Или через IDE — запустить `OrmApplication.java`.

#### Переменные окружения

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

По умолчанию: `postgres/postgres` на `localhost:5432`.

### Вариант 2: Docker Compose (рекомендуется)

```bash
docker-compose up --build
```

Это запустит:
- PostgreSQL на порту 5432
- Приложение на порту 8080

## REST API

### Курсы

| Метод | Endpoint | Описание |
|---|---|---|
| POST | `/api/courses` | Создать курс |
| GET | `/api/courses/{id}` | Получить курс |
| GET | `/api/courses` | Список всех курсов |
| GET | `/api/courses/search?title=...` | Поиск по названию |
| PUT | `/api/courses/{id}` | Обновить курс |
| DELETE | `/api/courses/{id}` | Удалить курс |

### Записи

| Метод | Endpoint | Описание |
|---|---|---|
| POST | `/api/enrollments` | Записать студента на курс |
| GET | `/api/enrollments/course/{courseId}` | Студенты курса |
| GET | `/api/enrollments/student/{studentId}` | Курсы студента |
| DELETE | `/api/enrollments/{id}` | Отписаться |

### Задания и решения

| Метод | Endpoint | Описание |
|---|---|---|
| POST | `/api/assignments` | Создать задание |
| GET | `/api/assignments/{id}` | Получить задание |
| POST | `/api/assignments/{id}/submit` | Отправить решение |
| POST | `/api/submissions/{id}/grade` | Оценить решение |
| GET | `/api/assignments/{id}/submissions` | Все решения |
| GET | `/api/students/{id}/submissions` | Решения студента |

### Тесты

| Метод | Endpoint | Описание |
|---|---|---|
| POST | `/api/quizzes/{id}/take` | Пройти тест |
| GET | `/api/quizzes/{id}` | Получить тест |
| GET | `/api/quizzes/{id}/submissions` | Результаты теста |

### Отзывы

| Метод | Endpoint | Описание |
|---|---|---|
| POST | `/api/reviews` | Оставить отзыв |
| GET | `/api/reviews/course/{courseId}` | Отзывы о курсе |
| GET | `/api/reviews/student/{studentId}` | Отзывы студента |

## Тесты

```bash
mvn test
```

### Типы тестов

- **Repository Integration Tests** (5): Course, Enrollment, Submission, Quiz, LazyLoading
- **Service Tests** (7): CourseService, EnrollmentService, AssignmentService, SubmissionService, QuizService, ReviewService, UserService
- **Controller Tests** (1): CourseController
- **Application Context Test** (1): OrmApplicationTests

**Итого: 20+ тестов**

Используются интеграционные тесты с H2 in-memory БД:
- CRUD операции (create, read, update, delete)
- Каскадное сохранение связанных объектов
- Проверка уникальных ограничений
- Демонстрация LazyInitializationException
- Проверка бизнес-логики сервисов

## Архитектура

```
Controller (REST API + DTO + @Valid)
    ↓
Service (Business Logic + @Transactional)
    ↓
Repository (Data Access - Spring Data JPA)
    ↓
Entity (JPA/Hibernate + @JsonIgnore)
    ↓
PostgreSQL
```

## Ленивая загрузка

Все коллекции (OneToMany, ManyToMany) используют `FetchType.LAZY`.

Для доступа к ленивым связям необходимо находиться в транзакции или использовать `JOIN FETCH` в JPQL запросах.

### Решение проблемы LazyInitializationException

1. **JOIN FETCH в репозитории**: `findByIdWithFullStructure()` загружает курс с модулями и уроками
2. **Транзакционный метод сервиса**: `@Transactional` обеспечивает открытую сессию
3. **Тест LazyLoadingTest**: демонстрирует проблему и решение

## Демо-данные

При запуске автоматически создаются:
- 1 преподаватель, 2 студента
- 1 курс с 2 модулями, 2 уроками
- 2 задания, 2 решения с оценками
- 1 тест с 2 вопросами
- 1 результат теста
- 1 отзыв

## Конфигурация профилей

- **dev** (по умолчанию): PostgreSQL, ddl-auto: update
- **test**: H2 in-memory, ddl-auto: create-drop

## Docker

### Сборка образа

```bash
docker build -t orm-platform .
```

### Запуск с PostgreSQL

```bash
docker-compose up --build
```

### Остановка

```bash
docker-compose down
```


