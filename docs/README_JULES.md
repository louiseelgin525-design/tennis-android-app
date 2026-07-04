# TennisTourney — handoff для Jules

## Какой файл использовать

Актуальный код:

```text
project_files/app/src/main/java/com/example/tennistourney/MainActivity.kt
```

Дубль:

```text
CURRENT_MainActivity.kt
```

Manifest:

```text
project_files/app/src/main/AndroidManifest.xml
```

## Важно

Папка `previous_reference_versions_DO_NOT_USE_AS_CURRENT` содержит старые промежуточные версии. Не использовать их как основную версию.

## Последний фикс

Последний фикс сделан после краша на кнопке просмотра результатов. Возможная причина — смена ориентации. Поэтому `FinalResults` снова открывается в `LANDSCAPE`, как шахматка.
