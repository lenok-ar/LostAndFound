# Потерял-Нашёл

Android-приложение для публикации и поиска объявлений о потерянных вещах в КемГУ.

## Возможности

- просмотр, добавление и открытие объявлений;
- авторизация через Яндекс ID и VK ID;
- профиль пользователя с защищённым хранением токена;
- раздел «О нас» с картой и построением маршрута;
- ИИ-помощник GigaChat в полной версии;
- push-уведомления Firebase;
- Firebase Remote Config, Firestore и Crashlytics;
- события аналитики AppMetrica.

## Архитектура

Проект построен по Clean Architecture и разделён на модули:

- `app` — точка входа, навигация, варианты сборки;
- `core:domain` — модели, интерфейсы репозиториев, use cases;
- `core:data` — Room, реализации репозиториев;
- `core:services` — внешние сервисы: авторизация, аналитика, карта, ИИ;
- `core:navigation` — общие маршруты;
- `feature:*` — независимые функциональные модули.

Архитектурные ограничения проверяются тестами Konsist в
`app/src/test/kotlin/com/example/lostandfound/architecture/ArchitectureTest.kt`.

## Критерии семестрового проекта

| Критерий | Реализация |
|---|---|
| Clean Architecture | модули `core:domain`, `core:data`, `core:services`, `feature:*` |
| Фоновая работа | `RemoteConfigSyncWorker`, Firebase `PushMessagingService` |
| Compose-анимации | `animateContentSize`, `animateColorAsState` в `FeedScreen` |
| XML/Compose integration | `AndroidView` с картой в `AboutScreen` |
| Build variants и R8 | flavors `demo`/`full`, release minify и shrink resources |
| Firebase | FCM, Remote Config, Firestore, Crashlytics |
| Внешние интеграции | Яндекс ID, VK ID, Yandex MapKit, AppMetrica, GigaChat |

## Варианты сборки

- `demo` — приложение без активного перехода к ИИ-помощнику, отдельное название;
- `full` — полная версия с ИИ-помощником.

```powershell
.\gradlew.bat :app:assembleDemoDebug
.\gradlew.bat :app:assembleFullDebug
.\gradlew.bat :app:assembleFullRelease
```

## Локальные ключи

Секреты хранятся только в `local.properties`, который исключён из Git:

```properties
APPMETRICA_API_KEY=
YANDEX_CLIENT_ID=
YANDEX_MAPKIT_API_KEY=
VK_CLIENT_ID=
VK_CLIENT_SECRET=
GIGACHAT_AUTH_KEY=
```

Для Firebase требуется файл `app/google-services.json`.

## Проверка

```powershell
.\gradlew.bat :app:testFullDebugUnitTest
.\gradlew.bat :feature:auth:testDebugUnitTest
.\gradlew.bat :app:assembleDemoDebug :app:assembleFullDebug
```

Готовые APK находятся в `app/build/outputs/apk/`.

## Загрузка на GitHub

Сборочные каталоги, APK, настройки Android Studio и локальные ключи исключены
через `.gitignore`, поэтому в репозиторий загружается только исходный код.

Рекомендуемое разбиение изменений на коммиты:

```powershell
# 1. Конфигурация проекта и общие модули
git add .gitignore README.md settings.gradle.kts build.gradle.kts gradle gradlew gradlew.bat core
git commit -m "chore: configure project and core modules"

# 2. Слои domain и data
git add domain data
git commit -m "feat: add domain and data layers"

# 3. Функциональные модули
git add feature
git commit -m "feat: add application features"

# 4. Приложение, Firebase и тесты
git add app/src app/build.gradle.kts app/proguard-rules.pro firestore.rules firebase
git commit -m "feat: integrate app services and tests"
```

После создания пустого репозитория на GitHub:

```powershell
git remote add origin https://github.com/USERNAME/LostAndFound.git
git push -u origin combined
git push origin master layer-based feature-based
```

Файл `app/google-services.json`, `local.properties`, APK и содержимое папок
`build` в GitHub не загружаются. APK при необходимости прикладывается отдельно
через раздел **Releases**.

## Материалы для защиты

В отчёт рекомендуется добавить скриншоты главного экрана, добавления объявления,
профиля, карты, ИИ-помощника, push-уведомления, AppMetrica, Crashlytics,
Firebase Remote Config, Git-веток и успешной сборки Gradle.
