# RailwayApp - Products Module

RailwayApp adalah aplikasi desktop multiplatform berbasis **Kotlin + Compose**.  
Modul **Products** menggunakan arsitektur **MVVM + Clean Architecture** dengan support **offline-first** dan sinkronisasi otomatis ke server.

---

## 🏗️ Struktur Proyek

```text
composeApp/
├── src/
│   ├── commonMain/
│   │   └── kotlin/
│   │       └── com.rahman.railwayapp/
│   │           ├── core/
│   │           │   ├── network/          # NetworkMonitor
│   │           │   └── util/             # IdGenerator, TimeProvider
│   │           ├── data/
│   │           │   ├── local/            # ProductLocalDataSource, ProductEntity
│   │           │   ├── queue/            # QueueDataSource, SyncTask
│   │           │   └── remote/           # ProductsApi, DTO
│   │           ├── domain/
│   │           │   ├── model/            # Product
│   │           │   ├── repository/       # ProductRepository
│   │           │   └── usecase/          # CRUD Use Cases
│   │           └── presentation/
│   │               └── viewmodel/          # ProductScreen, ProductViewModel
│   └── jvmMain/
│       └── kotlin/com.rahman.railwayapp/  # App.kt, main.kt
└── build.gradle.kts


---

## 📐 Arsitektur

![Architecture Diagram](./architecture.png)

**Alur Data:**

1. **UI Layer** (`ProductScreen`)  
   - Menampilkan data produk dan menerima input user (CRUD)  
   - Mengakses **ViewModel** untuk melakukan operasi

2. **ViewModel Layer** (`ProductViewModel`)  
   - Menyediakan `StateFlow` UI state  
   - Memanggil **Repository** untuk semua operasi  

3. **Repository Layer** (`ProductRepositoryImpl`)  
   - Mengatur sinkronisasi data antara **local** dan **remote**  
   - Menentukan apakah operasi dijalankan langsung atau di-queue  

4. **Local Data Source** (`ProductLocalDataSource`)  
   - Menyimpan semua data lokal, offline-first  
   - Menandai data yang sudah sinkron dengan server  

5. **Queue Layer** (`QueueDataSource`)  
   - Menyimpan operasi pending saat offline  
   - Menjalankan ulang saat online  

6. **Remote API** (`ProductsApi`)  
   - Hanya repository yang mengakses server menggunakan **Ktor Client**  
   - UI & ViewModel **tidak langsung memanggil API**  

## 🔗 Products API Documentation

**Base URL:**  
  https://multitenant-apis-production.up.railway.app


| Method | Endpoint                  | Description                         |
|--------|---------------------------|-------------------------------------|
| GET    | /products/:userId          | Get all products for a user         |
| GET    | /products/:userId/:id      | Get specific viewmodel for a user     |
| POST   | /products/:userId          | Create new viewmodel                  |
| PUT    | /products/:userId/:id      | Update viewmodel                      |
| DELETE | /products/:userId/:id      | Delete viewmodel                      |

---

## ⚡ Teknologi

- **Kotlin Multiplatform**  
- **Jetpack Compose Desktop**  
- **Ktor Client** (HTTP API)  
- **Coroutines + Flow**  
- **MVVM + Clean Architecture**  
- **Offline-first dengan local database dan queue**

---

## 🚀 Cara Menjalankan

1. Clone repository:  
```bash
git clone https://github.com/cybertank378/RailwayApp-Test
cd composeApp

./gradlew run