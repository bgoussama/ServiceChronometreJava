# ServiceChronometreJava

> Application Android démontrant l'utilisation d'un Foreground Service et d'un
> Bound Service pour créer un chronomètre persistant en Java pur.

![Platform](https://img.shields.io/badge/platform-Android-green)
![Language](https://img.shields.io/badge/language-Java-orange)
![MinSDK](https://img.shields.io/badge/minSdk-24-blue)
![Service](https://img.shields.io/badge/type-ForegroundService-red)

## Objectif pédagogique

Comprendre le cycle de vie des Services Android :
- Foreground Service avec notification persistante
- Bound Service pour communication Activity ↔ Service
- Persistance du chronomètre même quand l'app est fermée
- Bonnes pratiques Android 8+ et Android 14+

## Démo vidéo 



https://github.com/user-attachments/assets/0b6a7899-e5c5-4a8c-ba53-9bae0dfb8931



## Fonctionnalités

| Bouton | Action |
|---|---|
| DEMARRER SERVICE | Lance le chrono + notification persistante |
| ARRETER SERVICE | Arrête le chrono + supprime la notification |

Le chronomètre **continue à tourner** même si l'app est fermée — visible
dans le tiroir de notifications.

## Architecture
MainActivity (View)
│
│ bindService() / ServiceConnection
▼
ChronometreService (Foreground Service)
│
├── ScheduledExecutorService (thread background)
├── NotificationManager (notification persistante)
└── LocalBinder (communication avec Activity)

## Concepts clés

| Concept | Explication |
|---|---|
| `startForeground()` | Obligatoire depuis Android 8 — empêche le kill système |
| `START_STICKY` | Redémarre automatiquement si tué par le système |
| `LocalBinder` | Permet à l'Activity de récupérer l'instance du service |
| `foregroundServiceType` | Obligatoire depuis Android 14 |
| `onDestroy()` | Arrête l'executor et supprime la notification |

## Stack technique

- Java — Android SDK 24+
- AndroidX Core `1.13.1`
- Foreground Service + Bound Service
- ScheduledExecutorService
- NotificationCompat

## Structure
app/src/main/
├── java/com/example/servicechronometrejava/
│   ├── MainActivity.java         — UI + ServiceConnection
│   └── ChronometreService.java   — Foreground + Bound Service
└── res/
└── layout/
└── activity_main.xml

## Lancer le projet

```bash
git clone https://github.com/bgoussama/ServiceChronometreJava.git
```

Ouvrir dans Android Studio → Run sur émulateur API 26+




## Auteur

**Oussama Bagy** — ENSA Marrakech, GCDSTE 2022–2027
