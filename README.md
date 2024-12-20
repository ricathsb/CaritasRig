# Caritas Rig

Caritas Rig adalah aplikasi Android yang dirancang untuk membantu pengguna memilih dan merakit PC sesuai kebutuhan mereka. Aplikasi ini menyediakan berbagai fitur yang mempermudah pengguna dalam mengeksplorasi, membandingkan, dan membangun PC impian mereka dengan dukungan teknologi terkini.

## Fitur Utama

### Bagian Login
- **Login Google dan Email:** Pengguna dapat masuk menggunakan akun Google atau email.
- **Verifikasi Email:** Menjamin keamanan akun pengguna dengan mengharuskan verifikasi email.
- **Reset Password:** Fitur untuk mereset kata sandi jika pengguna lupa.

### Bagian Aplikasi
- **Melihat Berita Teknologi:** Menampilkan berita teknologi terbaru menggunakan [News API](https://newsapi.org/).
- **Membandingkan Part:** Fitur untuk membandingkan performa prosesor dan GPU.
- **Benchmark Part:** Memungkinkan pengguna untuk melihat hasil benchmark dari prosesor dan GPU.
- **Share Build:** Berbagi hasil rakitan PC ke komunitas online.
- **Change Language:** Mendukung berbagai bahasa: English, Indonesian, French, Japanese, Chinese, German, Hindi.
- **Change Currency:** Mendukung konversi mata uang: IDR, USD, EUR, JPY, CNY, INR, GBP. Menggunakan [CurrencyFreaks API](https://currencyfreaks.com/) untuk mendapatkan kurs mata uang real-time.
- **Favorite Part:** Menambahkan komponen ke daftar favorit untuk referensi di masa mendatang.

### Bagian Build PC
- **Merakit PC:** Fitur untuk memilih komponen PC seperti prosesor, GPU, motherboard, dan lainnya.
- **Cek Kompabilitas:** Memastikan semua komponen yang dipilih kompatibel satu sama lain.
- **Estimasi Wattage:** Menghitung kebutuhan daya dari komponen yang dipilih.
- **Estimasi Harga:** Menampilkan perkiraan harga total dari build PC dalam berbagai mata uang.
- **Kustomisasi Harga:** Pengguna dapat menyesuaikan harga komponen secara manual.

## Framework dan Library yang Digunakan
- **Firebase Authentication:** Untuk login dengan Google dan email.
- **Firebase Realtime Database & Firebase Storage:** Menyimpan data pengguna dan komponen.
- **[Android Image Cropper](https://github.com/CanHub/Android-Image-Cropper):** Untuk memotong gambar.
- **[AAY Chart](https://github.com/TheChance101/AAY-chart):** Menampilkan data dalam bentuk grafik/chart.
- **[PcPartPicker Scraping](https://github.com/N-O-U-R/PcPartPicker-Scraping):** Menyediakan data komponen PC yang sudah discrape.
- **[News API](https://newsapi.org/):** Mengambil berita teknologi terkini.
- **[CurrencyFreaks API](https://currencyfreaks.com/):** Digunakan untuk mendapatkan kurs mata uang real-time dan melakukan konversi harga.

## Kredit

### Library yang Digunakan
- [Android Image Cropper](https://github.com/CanHub/Android-Image-Cropper)
- [AAY Chart](https://github.com/TheChance101/AAY-chart)
- PcPartPicker Scraping
- [News API](https://newsapi.org/)
- [CurrencyFreaks API](https://currencyfreaks.com/)

### Kolaborator
- Ferry Fernandoli Sirait (221401097) - Project Manager
- Rakha Aditya (221301014) - Front-end Developer
- Hotbaen Eliezer Situmorang (221401070) - Back-end Developer
- Brian Abednego Silitonga (221401088) - Front-end Developer
- Richard Fajar Christian (221401122) - Back-end Developer
- Samuel Bryan P. Sitanggang (221401122) - Back-end Developer

## Cara Buka Projek
1. Clone repository ini.
2. Buka proyek di Android Studio.
3. Tambahkan file `google-services.json` ke folder `app`.
4. Build dan jalankan aplikasi di perangkat Android Anda.

## Cara Instalasi
1. Pilih pada bagian release
2. Pilih versi terbaru
3. Unduh aplikasi.
4. Pasang dan jalankan di perangkat android anda.
   

## Kontribusi
Kontribusi sangat diterima! Silakan buat issue atau pull request untuk perbaikan dan fitur baru.
