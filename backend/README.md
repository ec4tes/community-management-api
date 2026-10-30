# Backend geliştirme notları

Projenin özellikleri, mimarisi ve Docker kurulumu için [ana README](../README.md) dosyasına bakın.

## IDE / Maven ile çalıştırma

JDK 21 ve çalışan bir PostgreSQL veritabanı gerekir. Aşağıdaki PowerShell örneği, `localhost:5432` üzerinde önceden oluşturulmuş `community_db` veritabanını kullanır. Kullanıcı adı ve parolayı kendi veritabanınıza göre değiştirin.

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
$env:DB_URL = "jdbc:postgresql://localhost:5432/community_db"
$env:DB_USERNAME = "community_user"
$env:DB_PASSWORD = "replace-with-your-local-password"
$env:DB_SCHEMA = "public"
$env:FLYWAY_ENABLED = "false"
$env:JPA_DDL_AUTO = "update"

# Her çalıştırmada yeni bir geliştirme anahtarı üretir; önceki tokenlar geçersiz olur.
$jwtBytes = New-Object byte[] 32
$jwtGenerator = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$jwtGenerator.GetBytes($jwtBytes)
$jwtGenerator.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)

# backend dizininde çalıştırın.
.\mvnw.cmd spring-boot:run
```

Spring Boot, proje kökündeki `.env` dosyasını bu çalıştırma biçiminde otomatik okumaz. Değerleri terminal ortamına veya IDE run configuration ayarlarına ekleyin. `local` profili farklı olarak `LOCAL_DB_URL`, `LOCAL_DB_USERNAME`, `LOCAL_DB_PASSWORD` ve `LOCAL_JWT_SECRET` değişkenlerini kullanır.

API varsayılan olarak `8000` portunda açılır. Swagger UI: http://localhost:8000/swagger-ui/index.html

Bu örnekte Hibernate tabloları oluşturur/günceller; Flyway kapalıdır. Migration dosyaları boş veritabanında çalışacak eksiksiz bir başlangıç zinciri oluşturmadığından, üretim kurulumu için ayrıca ele alınmalıdır. İlk topluluk ve yönetici hesabına ilişkin sınırlamalar ana README'de açıklanmıştır.

## Test ve paketleme

Backend dizininde:

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Linux / macOS'ta `./mvnw` kullanın. Paketleme komutu testleri de çalıştırır ve çıktıyı `target/` altına yazar.

Test yapılandırması `src/test/resources/application.yml` dosyasındadır: H2 bellek içi veritabanı, `create-drop` şema yönetimi ve kapalı Flyway kullanılır.

## Kod organizasyonu

İstekler controller üzerinden servis katmanına ulaşır. Servisler iş kurallarını ve topluluk erişimini kontrol eder; repository arayüzleri veri erişimini sağlar. DTO/entity dönüşümleri MapStruct mapper'larında tutulur.

Yeni bir topluluk kapsamlı işlem eklerken ilgili kaynak ve kullanıcıların aynı toplulukta olduğunu doğrulayın, sorguları topluluk kapsamıyla sınırlandırın ve farklı topluluklardan erişim senaryosunu mevcut test yapısına ekleyin.
