# KingsLegacy — готовая автоматическая сборка APK

Этот вариант специально сделан **без Gradle Wrapper и без Android Studio**.
GitHub сам устанавливает Gradle 8.9, Java 17 и Android SDK, затем собирает APK.

## Самый простой способ

### 1. Создай репозиторий
На GitHub нажми **New repository** → назови его `KingsLegacy` → **Create repository**.

### 2. Загрузи этот проект
На странице нового репозитория нажми **Add file → Upload files**.

Из этого ZIP нужно загрузить **все файлы и папки проекта**, включая папку:
`.github/workflows/`

Не загружай сам ZIP внутрь репозитория — загрузи его содержимое.

### 3. Запусти сборку
Открой вкладку **Actions** → выбери **Build KingsLegacy APK** →
**Run workflow**.

### 4. Забери APK
Когда появится зелёная галочка, открой запуск workflow.
Внизу будет **Artifacts → KingsLegacy-APK**.
Скачай ZIP и достань из него `app-debug.apk`.

## Важно
Android Studio, Gradle и Android SDK на твоём компьютере устанавливать не нужно.

Это debug-версия APK для установки на телефон. Для Google Play позже понадобится
отдельная release-сборка с подписью.
