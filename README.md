# Piga Ride 🏍️

A role-based Android application for managing boda boda (motorcycle taxi) operations — connecting **passengers**, **riders**, and **administrators** in one app.

Built as a diploma project by **Josephat Ontuga** — Diploma in ICT, Level 6, Sigalagala National Polytechnic.

---

## Overview

Piga Ride digitises the informal boda boda booking process. Passengers can request a ride and see a fare estimate instantly, riders can toggle their availability and respond to requests, and an administrator can oversee the whole platform — all without depending on an internet connection or an external backend.

The app stores all data locally on-device using **Room (SQLite)**, making it fully self-contained: no server, no API keys, no ongoing hosting cost. This was a deliberate trade-off for a single-device academic project — see [Roadmap](#roadmap) below for what a production version would add.




<img width="646" height="1280" alt="image" src="https://github.com/user-attachments/assets/536fe57e-e7fc-40b7-9f06-c4d5df4782ac" />

<img width="646" height="1280" alt="image" src="https://github.com/user-attachments/assets/6a27afff-3e61-42de-b174-a65a007f1b89" />

<img width="646" height="1280" alt="image" src="https://github.com/user-attachments/assets/280b56f9-e997-4a11-977b-46b42bab4a2c" />

<img width="778" height="1280" alt="image" src="https://github.com/user-attachments/assets/5c7cb27c-4046-43f4-94d5-d41e18afb454" />

<img width="646" height="1280" alt="image" src="https://github.com/user-attachments/assets/87f7edac-3bc2-4448-81d2-f300f9865a16" />

<img width="646" height="1280" alt="image" src="https://github.com/user-attachments/assets/618c490b-0d87-475c-afac-efc11095faf6" />








## Features

### 👤 Passenger
- Register and log in (by phone number or email)
- Request a ride with pickup, destination, and a live fare estimate
- View trip history
- See a "last trip" summary on the dashboard

### 🏍️ Rider
- Register with license number and vehicle registration
- Toggle online/offline availability
- Accept or decline incoming ride requests
- View trip history and earnings

### 🛠️ Administrator
- Dashboard with total riders and total trips at a glance
- Recent trips feed with status badges
- Search and filter riders (All / Approved / Pending / Suspended)
- Approve or suspend rider accounts

### Shared
- Role-aware navigation drawer per user type
- Dark, Material Design–inspired UI
- SHA-256 password hashing
- Session persistence via SharedPreferences

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| IDE | Android Studio |
| UI | Android Views / XML (no Jetpack Compose) |
| Local database | Room (SQLite) |
| Session management | SharedPreferences |
| UI components | Material Components, DrawerLayout, NavigationView, RecyclerView, CardView, Flexbox |

## Project Structure





## Getting Started

1. Clone the repo:
```bash
   git clone https://github.com/Josephat22/PigaRide.git
```
2. Open the project in **Android Studio** (Empty Views Activity template, Java).
3. Let Gradle sync — the project uses AGP 8.x with Room and Material Components dependencies already declared in `app/build.gradle.kts`.
4. Run on an emulator or device (min SDK 26 / Android 8.0).

### Test Credentials

- **Admin login:** `admin@bodaboda.com` / `admin123`
- Passenger and rider accounts are created via the in-app **Register** screen.

## Roadmap

Planned improvements for a production-ready version:
- [ ] Shared backend (Firebase or REST API) for multi-device sync
- [ ] M-Pesa Daraja integration for live payments
- [ ] Real GPS location selection and live rider tracking (Google Maps SDK)
- [ ] Push notifications for new ride requests
- [ ] SOS / safety alert feature
- [ ] Password reset flow
- [ ] Multi-language support

## Developer

**Josephat Ontuga**
📧 ceetech8090@gmail.com
🐙 [github.com/Josephat22](https://github.com/Josephat22)

## License

This project was developed for academic purposes as part of a Diploma in ICT program.
