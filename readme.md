# Keuanganku - Personal Finance Management App

Keuanganku is an Android application for managing personal finances, tracking income and expenses, and generating financial reports.

## Features

### 1. Transaction Management

- Add income and expense transactions
- Edit and delete transactions
- Categorize transactions
- Add notes to transactions
- Group transactions by date
- Share transaction details

### 2. Account Management

- Multiple account support
- Track account balances
- View account transaction history
- Account-wise financial summaries

### 3. Financial Reports

- Daily, weekly and monthly reports
- Income and expense summaries
- Profit/loss calculation
- Line chart visualization of financial trends
- Custom date range selection for reports

### 4. Dashboard

- Total balance overview
- Income/expense pie chart
- Recent transactions list
- Period-wise financial summaries

### 5. Additional Features

- Indonesian Rupiah (IDR) currency formatting
- Date and time formatting
- Swipe navigation between periods
- Data persistence using Room database
- Initial data population

## Technical Details

### Project Structure

```
app/
├── build.gradle.kts        # App level build configuration
├── src/
│   └── main/
│       ├── java/
│       │   └── com/neurallift/keuanganku/
│       │       ├── data/               # Data layer
│       │       │   ├── dao/            # Database access objects
│       │       │   ├── model/          # Data models
│       │       │   └── repository/     # Repositories
│       │       ├── ui/                 # UI layer
│       │       │   ├── akun/           # Account related UI
│       │       │   ├── home/           # Home/Dashboard UI
│       │       │   ├── laporan/        # Reports UI
│       │       │   └── transaksi/      # Transaction UI
│       │       └── utils/              # Utility classes
│       └── res/                        # Android resources
```

### Architecture

- MVVM (Model-View-ViewModel) architecture pattern
- Repository pattern for data operations
- Room persistence library for local database
- LiveData for observable data
- ViewModel for UI state management
- Fragment-based navigation

### Key Components

#### Data Models

- [`Transaksi`](app/src/main/java/com/neurallift/keuanganku/data/model/Transaksi.java): Transaction entity
- [`Kategori`](app/src/main/java/com/neurallift/keuanganku/data/model/Kategori.java): Category entity
- [`Akun`](app/src/main/java/com/neurallift/keuanganku/data/model/Akun.java): Account entity

#### ViewModels

- [`HomeViewModel`](app/src/main/java/com/neurallift/keuanganku/ui/home/HomeViewModel.java): Dashboard logic
- [`LaporanViewModel`](app/src/main/java/com/neurallift/keuanganku/ui/laporan/LaporanViewModel.java): Reports logic
- [`TransaksiViewModel`](app/src/main/java/com/neurallift/keuanganku/ui/transaksi/viewmodel/TransaksiViewModel.java): Transaction management

#### Repositories

- [`TransaksiRepository`](app/src/main/java/com/neurallift/keuanganku/data/repository/TransaksiRepository.java): Transaction data operations
- [`KategoriRepository`](app/src/main/java/com/neurallift/keuanganku/data/repository/KategoriRepository.java): Category data operations
- [`AkunRepository`](app/src/main/java/com/neurallift/keuanganku/data/repository/AkunRepository.java): Account data operations

#### Utilities

- [`DateTimeUtils`](app/src/main/java/com/neurallift/keuanganku/utils/DateTimeUtils.java): Date/time operations
- [`FormatUtils`](app/src/main/java/com/neurallift/keuanganku/utils/FormatUtils.java): Formatting utilities

### Dependencies

```kotlin
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
}
```

### Database Schema

The app uses Room database with the following entities:

- `transaksi`: Stores transaction records
- `kategori`: Stores transaction categories
- `akun`: Stores account information

## Setup & Development

### Requirements

- Android Studio Electric Eel or higher
- Android SDK 24 or higher
- Java 11

### Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

### Contribution Guidelines

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Create pull request

## License

[License details to be added]

## Credits

Developed by NeuralLift
